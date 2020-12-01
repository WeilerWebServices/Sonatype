package com.sonatype.workout.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.sonatype.example.workout.api.ExerciseService;
import com.sonatype.workout.model.Exercise;

public class ExercisesController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	private ExerciseService exerciseService;
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		List<Exercise> exercises = exerciseService.all();
		Map<String, Object> vars = new HashMap<String,Object>();
		vars.put( "exercises", exercises);
		return new ModelAndView("exercises", vars);
	}

	public ExerciseService getExerciseService() {
		return exerciseService;
	}

	public void setExerciseService(ExerciseService exerciseService) {
		this.exerciseService = exerciseService;
	}
	
}