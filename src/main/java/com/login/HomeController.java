package com.login;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.login.model.*;
import com.login.service.GymMemberService;
import com.login.service.OtpService;
import com.login.service.OtpService;
import com.login.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.mail.MessagingException;

import com.login.repo.*;

@Controller
public class HomeController {

	@Autowired
	private GymMemberService service;
	
	@Autowired
	private OtpService otpService;
	

	@Autowired
	private PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final UserRepository userRepository;

	public HomeController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
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
		
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			model.addAttribute("error", "Username already exists!");
			return "register"; // Stay on the register page and show the error
		}
		
		session.setAttribute("fullName", user.getName());
		session.setAttribute("username", user.getUsername());
		session.setAttribute("password", user.getPassword());
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(user.getUsername(), otp);  
        model.addAttribute("email",user.getUsername());
        model.addAttribute("warning","if you have not received mail please check your email-id again");
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

}
