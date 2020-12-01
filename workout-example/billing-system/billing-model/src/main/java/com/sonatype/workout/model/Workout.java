package com.sonatype.workout.model;

import java.io.Serializable;
import java.util.Date;

public class Workout implements Serializable {

	private static final long serialVersionUID = -2320502365925548202L;

	private Integer id;
	private User user;
	private Date date;
	private Location location;
	
	public Workout() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	
	
}
