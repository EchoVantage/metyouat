package com.metyouat.playground;

import java.util.List;

import twitter4j.User;

public class Player {
	private List<String> tags;
	private String game;
	private String originalProfileImageURL;
	private Long targetUserId;
	private boolean following;

	public Player(long id, String screenName, String name, String originalProfileImageURL, boolean following, String game, Long targetUserId){
		this.originalProfileImageURL = originalProfileImageURL;
		this.following = following;
		this.game = game;
		this.targetUserId = targetUserId;
	}

	public List<String> getTags() {
	   return tags;
   }
	
	public boolean isFollowing() {
	   return following;
   }
	
	public void setFollowing(boolean following) {
	   this.following = following;
   }

	public Long getTargetUserId() {
	   return targetUserId;
   }

	public String getOriginalProfileImageURL() {
	   return originalProfileImageURL;
   }

	public String getGame() {
	   return game;
   }

	public void setTargetUserId(Long targetUserId) {
		this.targetUserId = targetUserId;
   }

	public void setTags(List<String> tags) {
		this.tags = tags;
   }

	public void setGame(String game) {
		this.game = game;
   }

	public void setUser(User user) {
		this.originalProfileImageURL = user.getOriginalProfileImageURL();
   }

}
