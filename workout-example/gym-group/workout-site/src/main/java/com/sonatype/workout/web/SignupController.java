package com.sonatype.workout.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sonatype.example.workout.api.AuthService;
import com.sonatype.example.workout.api.UserService;
import com.sonatype.workout.model.User;
import com.sonatype.workout.web.command.SignupCommand;

public class SignupController extends SimpleFormController {

	private AuthService authService;
	private UserService userService;
	
	public SignupController() {
		super();
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return new SignupCommand();
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		SignupCommand bean = (SignupCommand) command;
		
		User user = new User();
		user.setFirstName( bean.getFirstName() );
		user.setLastName( bean.getLastName() );
		user.setLogin( bean.getLogin() );
		user.setPassword( authService.hashPassword( bean.getLogin(), bean.getPassword() ) );
		userService.save( user );
	
		return super.onSubmit(request, response, command, errors);
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		// TODO Auto-generated method stub
		super.initBinder(request, binder);
	}

	public AuthService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	
}
