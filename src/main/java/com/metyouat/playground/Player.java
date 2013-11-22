package com.metyouat.playground;

import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

public class Player {
	private User user;
	private long id;
	private Session session;
	
	public Player(long id){
		this.id = id;
   }

	public void onStatus(Status status) throws IllegalStateException, TwitterException {
		session().addStatus(this, status);
   }
	
	public User user() {
		assert user != null : "Missing user";
		return user;
	}
	
	public Session session() {
		assert session != null : "Missing session";
		return session;
	}
	
	public void setSession(Session session) {
		withSession(session);
	}
	
	public void setUser(User user) {
		withUser(user);
	}

	public long getId() {
	   return id;
   }

	public Player withUser(User newUser) {
		assert newUser != null : "missing user";
		assert newUser.getId() == id : "Cannot change user on player";
		this.user = newUser;
		return this;
	}

	public Player withSession(Session newSession) {
		assert newSession != null : "missing session";
		this.session = newSession;
		return this;
	}
	
	public URL getProfileImage() throws MalformedURLException{
		return new URL(user().getOriginalProfileImageURL());
	}
}
