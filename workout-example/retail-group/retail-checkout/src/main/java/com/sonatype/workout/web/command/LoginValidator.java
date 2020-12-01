package com.sonatype.workout.web.command;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sonatype.example.workout.api.AuthService;
import com.sonatype.example.workout.api.UserService;
import com.sonatype.workout.model.User;

public class LoginValidator implements Validator {

	private UserService userService;
	private AuthService authService;

	public LoginValidator() {
	}

	public void validate(Object o, Errors e) {
		LoginCommand cmd = (LoginCommand) o;

		User user = userService.forLogin(cmd.getLogin());
		String password = cmd.getPassword();
		
		if (user == null) {
			e.rejectValue(
					"login",
					"login.taken",
					"Invalid username or password");
		} else if( !authService.authenticate( user.getLogin(), password, user.getPassword() ) ) {
			e.rejectValue(
					"login",
					"login.taken",
					"Invalid username of password");
		}

		
	}

	public boolean supports(Class c) {
		return LoginCommand.class.equals(c);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public AuthService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}
	
}
