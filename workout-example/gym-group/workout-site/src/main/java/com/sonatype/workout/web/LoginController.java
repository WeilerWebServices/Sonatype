package com.sonatype.workout.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sonatype.example.workout.api.AuthService;
import com.sonatype.example.workout.api.UserService;
import com.sonatype.workout.model.User;
import com.sonatype.workout.web.command.LoginCommand;
import com.sonatype.workout.web.command.SignupCommand;

public class LoginController extends SimpleFormController {

	private UserService userService;
	
	public LoginController() {
		super();
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return new LoginCommand();
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		LoginCommand bean = (LoginCommand) command;
		
		User user = userService.forLogin( bean.getLogin() );
		request.getSession().setAttribute( "user", user );
	
		return super.onSubmit(request, response, command, errors);
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		// TODO Auto-generated method stub
		super.initBinder(request, binder);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	
}
