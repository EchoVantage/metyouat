package com.metyouat.playground;

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
//		try {
//			if(user.isFollowRequestSent()){
//				user = twitter.createFriendship(user.getId());
//			}
//			Set<String> tags = new HashSet<>();
//			for(HashtagEntity tag: status.getHashtagEntities()){
//				tags.add(tag.getText().toLowerCase());
//			}
//			if(tags.contains("imet")){
//				if(friend){
//					iMet(status);
//					replyTo(status, "MetYouAt #"+game);
//				}
//			}else if(status.getInReplyToUserId() == twitter.getId()){
//				if(tags.size() == 0){
//					if(friend){
//						replyTo(status, "MetYouAt #"+game);
//					}else{
//						replyTo(status, "I'm the MetYouAt Game Master, find out more at http://metyouat.com");
//					}
//				} else if(tags.size() == 1){
//					String tag = tags.iterator().next();
//					met.setGame(tag);
//					if(met.isFriend()){
//						replyTo(status, "MetYouAt #"+game);
//					}else{
//						replyTo(status, "follow me to play MetYouAt #"+tag);
//					}
//				} else {
//					if(met.isFriend()){
//						replyTo(status, "Play one game at a time, MetYouAt #"+met.game);
//					}else{
//						replyTo(status, "I'm the MetYouAt Game Master, find out more at http://metyouat.com");
//					}
//				}
//			}else if(mentioned(status) && status.getInReplyToUserId() == -1 && tags.size() == 1){
//				String tag = tags.iterator().next();
//				met.setGame(tag);
//				if(met.isFriend()){
//					replyTo(status, "MetYouAt #"+met.game);
//				}
//			}
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (TwitterException e) {
//			e.printStackTrace();
//		}
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

	public Player withUser(User user) {
		assert user != null : "missing user";
		assert user.getId() == id : "Cannot change user on player";
		this.user = user;
		return this;
	}

	public Player withSession(Session session) {
		assert session != null : "missing session";
		this.session = session;
		return this;
	}
}
