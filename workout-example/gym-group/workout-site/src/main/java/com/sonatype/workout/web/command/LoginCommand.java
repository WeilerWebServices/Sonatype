package com.sonatype.workout.web.command;

public class LoginCommand {

	private String login;
	private String password;
	
	public LoginCommand() {}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
