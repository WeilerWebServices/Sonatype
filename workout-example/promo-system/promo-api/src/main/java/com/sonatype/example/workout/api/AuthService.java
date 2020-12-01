package com.sonatype.example.workout.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AuthService {

	public AuthService() {}
	
	public boolean authenticate(String username, String password, byte[] expected) {
		byte[] supplied = hashPassword( username, password);
		return Arrays.equals(supplied, expected);
	}

	public byte[] hashPassword(String username, String password) {
		// TODO: Never initialize to null this is dumb
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(username.getBytes());
			baos.write(password.getBytes());
		} catch (IOException e) {
		}
		md.update(baos.toByteArray());

		byte[] byteData = md.digest();
		return byteData;
	}

}
