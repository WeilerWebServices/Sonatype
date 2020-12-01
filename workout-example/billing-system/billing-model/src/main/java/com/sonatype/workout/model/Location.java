package com.sonatype.workout.model;

import java.io.Serializable;

public class Location implements Serializable {

	private static final long serialVersionUID = 8402492244529925894L;

	private Integer id;
	private String name;
	
	public Location() {}

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
