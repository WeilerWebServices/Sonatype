package com.sonatype.workout.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sonatype.example.workout.api.WorkoutService;
import com.sonatype.workout.model.User;
import com.sonatype.workout.model.Workout;
import com.sonatype.workout.web.command.WorkoutCommand;

public class WorkoutController extends SimpleFormController {

	protected final Log logger = LogFactory.getLog(getClass());

	private WorkoutService workoutService;
	
	public WorkoutController() {
		super();
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return new WorkoutCommand();
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		WorkoutCommand bean = (WorkoutCommand) command;
		
		Workout workout = new Workout();
		workout.setDate( bean.getDate() );
		workout.setUser( (User) request.getSession().getAttribute("user") );
		Integer workoutId = workoutService.save( workout );
		
		workout = workoutService.load( workoutId );
		
		return super.onSubmit(request, response, command, errors);
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		// TODO Auto-generated method stub	
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
		binder.registerCustomEditor( Date.class, editor );
		super.initBinder(request, binder);
	}

	public WorkoutService getWorkoutService() {
		return workoutService;
	}

	public void setWorkoutService(WorkoutService workoutService) {
		this.workoutService = workoutService;
	}
	
}