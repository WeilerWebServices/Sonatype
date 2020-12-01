package com.sonatype.example.workout.api;

import java.util.List;

import com.sonatype.workout.model.Exercise;

public interface ExerciseService {
	
	public Exercise load(Integer id);
	public Integer save(Exercise exercise);
	public List<Exercise> all();
	
}
