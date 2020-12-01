package com.sonatype.example.workout.api;

import com.sonatype.workout.model.User;
import com.sonatype.workout.model.Workout;

public interface WorkoutService {
	
	public Workout load(Integer id);
	public Integer save(Workout workout);

}
