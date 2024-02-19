package com.dietify.v1.Controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dietify.v1.Entity.User;
import com.dietify.v1.Repository.UserRepo;
import com.dietify.v1.Service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepo;

	@ModelAttribute
	public void commonUser(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);
		}

	}

	@GetMapping("/")
	public String landing() {
		return "home";
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "index";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, HttpSession session, Model m) {
		if (userService.existsByEmail(user.getEmail())) {
			session.setAttribute("msg", "Email address already exists");
			return "redirect:/index";
		}
		User savedUser = userService.saveUser(user);

		if (savedUser != null) {
			session.setAttribute("msg", "Registered successfully");
		} else {
			session.setAttribute("msg", "Something went wrong on the server");
		}

		return "redirect:/index";
	}

	@PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        userService.initiatePasswordReset(email);
        return ResponseEntity.ok("Password reset instructions sent to your email.");
    }

	@GetMapping("/reset")
	public String reset(){
		return "reset";
	}
	@GetMapping("/forgot")
	public String forgot(){
		return "forgot";
	}

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok("Password reset successful.");
    }

}