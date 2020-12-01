package com.sonatype.workout.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sonatype.example.workout.api.WorkoutService;
import com.sonatype.workout.model.Workout;
import com.sonatype.workout.web.command.WorkoutCommand;

public class WorkoutFormController extends SimpleFormController {

	private WorkoutService workoutService;
	
	public WorkoutFormController() {
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
		
		Workout w = new Workout();
		w.setDate( bean.getDate() );
		Integer workoutId = workoutService.save( w );
		
		request.setAttribute("workout", workoutService.load(workoutId));
		
		return super.onSubmit(request, response, command, errors);
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		// TODO Auto-generated method stub
		super.initBinder(request, binder);
	}

	public WorkoutService getWorkoutService() {
		return workoutService;
	}

	public void setWorkoutService(WorkoutService workoutService) {
		this.workoutService = workoutService;
	}

}
