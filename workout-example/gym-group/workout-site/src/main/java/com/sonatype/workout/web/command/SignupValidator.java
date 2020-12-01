package com.sonatype.workout.web.command;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.sonatype.example.workout.api.UserService;

public class SignupValidator implements Validator {

	private UserService userService;

	public SignupValidator() {
	}

	public void validate(Object o, Errors e) {
		SignupCommand cmd = (SignupCommand) o;

		if (StringUtils.isEmpty(cmd.getFirstName())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(e, "firstName",
					"field.required", "First Name is a Required Field");
		}
		if (StringUtils.isEmpty(cmd.getLastName())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(e, "lastName",
					"field.required", "Last Name is a Required Field");
		}
		if (StringUtils.isEmpty(cmd.getLogin())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(e, "login",
					"field.required", "Login is a Required Field");
		}

		if (userService.forLogin(cmd.getLogin()) != null) {
			e.rejectValue(
					"login",
					"login.taken",
					"I'm sorry but the login you selected is already taken, please select a different login");
		}

		if (StringUtils.isEmpty(cmd.getPassword())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(e, "password",
					"field.required", "You must supply a password.");
		}
		if (StringUtils.isEmpty(cmd.getPasswordConfirm())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(e, "passwordConfirm",
					"field.required",
					"You can't leave your password confirmation empty.");
		}
		if (StringUtils.isNotEmpty(cmd.getPassword())
				&& StringUtils.isNotEmpty(cmd.getPasswordConfirm())) {
			if (!cmd.getPassword().equals(cmd.getPasswordConfirm())) {
				e.rejectValue("passwordConfirm", "not.equal",
						"You password confirmation doesn't match your password.");
			}

		}
	}

	public boolean supports(Class c) {
		return SignupCommand.class.equals(c);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	
	
	
}
