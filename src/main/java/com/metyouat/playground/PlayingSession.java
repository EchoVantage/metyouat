package com.metyouat.playground;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class PlayingSession implements Session {
	@Override
	public void addStatus(Player player, Status status, MasterSession master) throws IllegalStateException, TwitterException {
		if(containsTag(status, "imet")){
			player.iMet(status);
		}else if(containsUser(status, master.getId())){
			player.changeGame(status);
		}
	}

	static boolean containsUser(Status status, long id) {
		for(UserMentionEntity user: status.getUserMentionEntities()){
			if(user.getId() == id){
				return true;
			}
		}
	   return false;
   }

	private static boolean containsTag(Status status, String text) {
		for(HashtagEntity tag: status.getHashtagEntities()){
			if(tag.getText().equalsIgnoreCase(text)){
				return true;
			}
		}
	   return false;
   }
}
