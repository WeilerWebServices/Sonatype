package com.sonatype.workout.model;

import java.io.Serializable;

public class Exercise implements Serializable {

	private static final long serialVersionUID = 8402492244529925894L;

	private Integer id;
	private String name;
	private ExerciseType type;
	
	public Exercise() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ExerciseType getType() {
		return type;
	}

	public void setType(ExerciseType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
