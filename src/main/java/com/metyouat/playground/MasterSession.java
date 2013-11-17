package com.metyouat.playground;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class MasterSession implements Session{
	private Twitter twitter;

	public MasterSession(Twitter twitter) {
		this.twitter = twitter;
   }
	
	public long getId() throws IllegalStateException, TwitterException{
		return twitter.getId();
	}
	
	public void replyTo(Status status, String message) throws TwitterException {
		twitter.updateStatus(new StatusUpdate("@"+status.getUser().getScreenName()+", "+message).inReplyToStatusId(status.getId()));
	}

	public void replyTo(long userId, String message) throws TwitterException {
		twitter.sendDirectMessage(userId, message);
	}

	public boolean mentioned(Status status) throws TwitterException {
		for(UserMentionEntity mention: status.getUserMentionEntities()){
			if(mention.getId() == twitter.getId()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void addStatus(Player player, Status status, MasterSession master) {
		// ignore
	}
	
	public User follow(long id) throws TwitterException {
		return twitter.createFriendship(id);
   }
}
