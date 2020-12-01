package com.sonatype.workout.web.command;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class WorkoutValidator implements Validator {

	public WorkoutValidator() {
	}

	public void validate(Object o, Errors e) {
		WorkoutCommand cmd = (WorkoutCommand) o;

		Date date = cmd.getDate();
		
		if( date == null ) {
			e.rejectValue("date", "required", "Workout Date is Required");
		}

	}

	public boolean supports(Class c) {
		return WorkoutCommand.class.equals(c);
		
	}
	
}
