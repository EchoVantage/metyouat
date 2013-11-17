package com.metyouat.playground;

import twitter4j.Status;
import twitter4j.TwitterException;

public interface Session {

	Session INACTIVE = new InactiveSession();
	Session PLAYING = new PlayingSession();
	
	void addStatus(Player player, Status status, MasterSession master) throws IllegalStateException, TwitterException;

}
