package com.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.login.model.*;
import com.login.service.GymMemberService;
import com.login.service.UserService;
import com.login.repo.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private GymMemberService service;
	private final UserService userService;
	private final UserRepository userRepository;

	public HomeController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@GetMapping("/")
	public String showRegistrationForm(Model model) {
		model.addAttribute("gymMember", new GymMember());
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

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user, Model model) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			model.addAttribute("error", "Username already exists!");
			return "register"; // Stay on the register page and show the error
		}
		userService.registerUser(user.getName(), user.getUsername(), user.getPassword());
		return "redirect:/login";

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
        return "redirect:/login";  // Redirect to the home page after logout
    }
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}
	
	@GetMapping("/forgot")
	public String forgotPassword() {
		return "forgot";
	}
}
