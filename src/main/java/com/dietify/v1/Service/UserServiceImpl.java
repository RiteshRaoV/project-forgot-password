package com.dietify.v1.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dietify.v1.Config.CustomUserDetailsService;
import com.dietify.v1.Entity.User;
import com.dietify.v1.Repository.UserRepo;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public boolean existsByEmail(String email) {
		return userRepo.existsByEmail(email);
	}

	@Override
	public void removeSessionMessage() {

		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();

		session.removeAttribute("msg");
	}

	@Override
	public User saveUser(User user) {

		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		user.setRole("ROLE_USER");
		User newuser = userRepo.save(user);

		return newuser;
	}

	@Override
	public void initiatePasswordReset(String email) {
		customUserDetailsService.initiatePasswordReset(email);
	}

	@Override
	public void resetPassword(String email, String token, String newPassword) {
		customUserDetailsService.resetPassword(email, token, newPassword);
	}

}