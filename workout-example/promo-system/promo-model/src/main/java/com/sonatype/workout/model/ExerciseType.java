package com.sonatype.workout.model;

import java.io.Serializable;

public class ExerciseType implements Serializable {
	
	private static final long serialVersionUID = -8654938064979052758L;

	private Integer id;
	private String name;
	
	public ExerciseType() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
