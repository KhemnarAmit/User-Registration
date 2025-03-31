package com.login;

import java.util.Optional;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import com.login.model.*;
import com.login.service.*;
import com.login.service.ContactService;
import com.login.service.GymMemberService;
import com.login.service.OtpService;
import com.login.service.PaymentService;
import com.login.service.OtpService;
import com.login.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.login.repo.*;

@Controller
public class HomeController {

	@Autowired
	private GymMemberService service;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final UserRepository userRepository;
	
	@Autowired
	private final ContactService contactService;

	public HomeController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.contactService = new ContactService();
	}

	@GetMapping("/")
	public String showRegistrationForm(Model model) {
		model.addAttribute("gymMember", new GymMember());
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("username",username);
		return "index";
	}

	@GetMapping("/login")
	public String loginpage() {
		return "login";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/gymregister")
	public String registerUser(@ModelAttribute GymMember gymMember, Model model) {
		boolean isRegistered = service.registerMember(gymMember);
		if (!isRegistered) {
			model.addAttribute("errorMessage", "User with this email already exists!");
			return "error";
		}
		return "registrationsuccess";
	}

	@PostMapping("/logout")
	public String logout() {
		// Handle the logout logic
		return "redirect:/login"; // Redirect to the home page after logout
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}
	
	
	
	@PostMapping("/verify")
	public String sendOtp(@ModelAttribute User user, Model model, HttpSession session) throws MessagingException {
	    
	    // Check if the username already exists in the database
	    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
	        model.addAttribute("error", "Username already exists!");
	        return "register"; // Stay on the register page and show the error
	    }
	    
	    // Save the user's details in the session
	    session.setAttribute("fullName", user.getName());
	    session.setAttribute("username", user.getUsername());
	    session.setAttribute("password", user.getPassword());

	    String otp = otpService.generateOtp();
	    otpService.sendOtpEmail(user.getUsername(), otp);

	    model.addAttribute("email", user.getUsername());
	    model.addAttribute("warning", "If you have not received mail, please check your email address again.");
	    model.addAttribute("warning1", "Please check the spam folder as well.");
	    return "verify";  // Redirect to OTP verification page
	}


	
	@PostMapping("/verify1")
	public String verifyOtp(@RequestParam("otp") String otp, Model model) {
	    boolean isValidOtp = otpService.verifyOtp(otp); // Check OTP validity
	    
	    // Debugging logs to check what's happening
	    System.out.println("Entered OTP: " + otp);
	    System.out.println("Generated OTP: " + otpService.getGeneratedOtp());
	    
	    if (isValidOtp) {
	        return "success";  // Redirect to success page if OTP matches
	    } else {
	        model.addAttribute("error", "Invalid OTP. Please try again.");
	        return "verify";  // Redirect back to OTP verification page with error message
	    }
	}
	
	@PostMapping("/success")
	public String successRegistration(HttpSession session) {
		String fullName = (String) session.getAttribute("fullName");
		String username = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");
		userService.registerUser(fullName,username,password);
		System.out.println("registration is successfull");
		return "login";
	}
	
	
	@PostMapping("/contact_save") 
	public ModelAndView registerContactUs(@ModelAttribute Contact contact) {
		ModelAndView modelAndView = new ModelAndView();
		if(contactService.registerContact(contact)) {
		modelAndView.addObject("name", contact.getName());
		modelAndView.addObject("email",contact.getEmail());
		modelAndView.addObject("message",contact.getMessage());
		modelAndView.setViewName("contactsend");
		return modelAndView;
		}
		else {
			modelAndView.addObject("contacterror","make sure you can reach only once per mail id\n if you already reach out to us please wait for our response");
			modelAndView.setViewName("error") ;
		}
		
		return modelAndView;
	}
	
	
	@PostMapping("/savePaymentData") 
	public ModelAndView savePaymentData(@ModelAttribute Payment payment) throws MessagingException {
		ModelAndView modelAndView = new ModelAndView();
		if(paymentService.registerPaymentDetails(payment)) {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message,true);
			String mailText = "Hello "+payment.getName()+" \nyour registration is successful"+"\nhere are the details of your payment that we received from you"+"\nplease take a note that once deadline is finished we will automatically stop your Gym Service";
			modelAndView.addObject("transactionID", payment.getTransactionID());
			modelAndView.addObject("name", payment.getName());
			modelAndView.addObject("email", payment.getEmail());
			modelAndView.addObject("date", payment.getDate());
			modelAndView.addObject("file", payment.getImage());
			modelAndView.setViewName("paymentSaveSuccess");
			helper.setTo(payment.getEmail());
			helper.setText(mailText);
			helper.setSubject("Gym Registration Successful");
			mailSender.send(message);
			return modelAndView;
		}
		else {
			modelAndView.addObject("paymentError", "Payment details not saved \n please check file size and try to save again \n if issue still not solved contact us by 'Contact Us' page");
			modelAndView.setViewName("error");
			return modelAndView;
		}
		
	}

}
