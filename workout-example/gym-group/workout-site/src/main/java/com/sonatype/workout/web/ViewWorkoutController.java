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

import com.sonatype.example.workout.api.WorkoutService;
import com.sonatype.workout.model.Workout;

public class ViewWorkoutController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	private WorkoutService workoutService;
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Integer workoutId = Integer.parseInt( (String) request.getParameter("id") );
		Workout workout = workoutService.load( workoutId );
		
		Map<String, Object> vars = new HashMap<String,Object>();
		vars.put( "workout", workout);
		return new ModelAndView("view-workout", vars);
	}

	public WorkoutService getWorkoutService() {
		return workoutService;
	}

	public void setWorkoutService(WorkoutService workoutService) {
		this.workoutService = workoutService;
	}
	
}