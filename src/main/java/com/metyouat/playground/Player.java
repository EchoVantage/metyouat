package com.metyouat.playground;

import java.util.HashSet;
import java.util.Set;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class Player {
	private User user;
	private Session meta;
	private Session current;
	private long id;
	private MasterSession master;
	
	public Player(long id, Session meta, MasterSession master) {
		this.id = id;
		this.meta = meta;
		this.current = meta;
		this.master = master;
   }

	public void onStatus(Status status) throws IllegalStateException, TwitterException {
		user = status.getUser();
		current.addStatus(this, status, master);
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

	public Player setUser(User user) {
		this.user = user;
	   return this;
   }

	public long getId() {
	   return id;
   }

	public void masterFollow() throws TwitterException {
		user = master.follow(id);
   }

	public void iMet(Status status) {
	   // TODO Auto-generated method stub
	   
   }

	public void changeGame(Status status) {
	   // TODO Auto-generated method stub
	   
   }
}
