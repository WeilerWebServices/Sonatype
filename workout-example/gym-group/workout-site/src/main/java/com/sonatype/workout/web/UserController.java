package com.sonatype.workout.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.sonatype.example.workout.api.UserService;
import com.sonatype.workout.model.User;

public class UserController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("User Controller");

		User user = userService.load(1);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("user", user);

		return new ModelAndView("user.jsp", vars);
	}
}