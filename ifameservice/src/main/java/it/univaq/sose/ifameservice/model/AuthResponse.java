package it.univaq.sose.ifameservice.model;

import java.io.Serializable;

public class AuthResponse implements Serializable {
	
	private String message;
	private String token;
	
	public AuthResponse(String token, String message) {
		this.token = token;
		this.message = message;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
