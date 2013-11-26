package com.metyouat.playground;

import java.io.IOException;
import java.net.URL;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

public interface TwitterBot {
	interface Callback{

		void onFriend(long friendId);

		void onStatus(Status status);
		
	}
	
	public void replyTo(final Status status, final String message)
	      throws TwitterException;

	public void replyTo(final Status status, final String message, final URL media)
	      throws TwitterException, IOException;

	public long getId();

	public User follow(long id) throws TwitterException;

}
