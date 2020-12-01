package com.sonatype.example.workout.api;

import com.sonatype.workout.model.User;

public interface UserService {
	
	public User load(Integer id);
	public User forLogin(String login);
	public Integer save(User user);

}
