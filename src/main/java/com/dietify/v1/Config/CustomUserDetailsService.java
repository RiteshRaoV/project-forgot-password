package com.dietify.v1.Config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dietify.v1.Entity.User;
import com.dietify.v1.Repository.UserRepo;
import com.dietify.v1.Service.EmailService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EmailService emailService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepo.findByEmail(username);
		System.out.println(user);
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		} else {
			return new CustomUser(user);
		}
	}

	public void initiatePasswordReset(String email) {
		User user = userRepo.findByEmail(email);
		if (user != null) {
			String resetToken = generateResetToken();
			user.setResetToken(resetToken);
			userRepo.save(user);
			sendResetPasswordEmail(user.getEmail(), resetToken);
		}
	}

	public void resetPassword(String email, String token, String newPassword) {
		User user = userRepo.findByEmailAndResetToken(email, token);
		if (user != null) {
			user.setPassword(newPassword);
			user.setResetToken(null);
			userRepo.save(user);
		}
	}

	private String generateResetToken() {
		return UUID.randomUUID().toString();
	}

	private void sendResetPasswordEmail(String email, String token) {
		String resetLink = "http://localhost:1111/reset?token=" + token;
		String subject = "Reset Your Password";
		String body = "To reset your password, click the following link: " + resetLink;
		emailService.sendEmail(email, subject, body);
	}

}