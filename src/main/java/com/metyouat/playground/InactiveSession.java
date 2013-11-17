package com.metyouat.playground;

import twitter4j.Status;
import twitter4j.TwitterException;

public class InactiveSession implements Session {
	@Override
	public void addStatus(Player player, Status status, MasterSession master) throws IllegalStateException, TwitterException {
		if(PlayingSession.containsUser(status, master.getId()) && status.getHashtagEntities().length > 0){
			player.masterFollow();
			player.changeGame(status);
		}
	}
}
