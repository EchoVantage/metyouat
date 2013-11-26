package com.metyouat.playground;

import java.util.List;

import twitter4j.User;

public class Player {
	private List<String> tags;
	private String game;
	private String originalProfileImageURL;
	private Long targetUserId;
	private boolean following;
	private long id;

	public Player(final long id, final String screenName, final String name, final String originalProfileImageURL, final boolean following, final String game, final Long targetUserId) {
		this.id = id;
		this.originalProfileImageURL = originalProfileImageURL;
		this.following = following;
		this.game = game;
		this.targetUserId = targetUserId;
	}

	public String getGame() {
		return game;
	}

	public String getOriginalProfileImageURL() {
		return originalProfileImageURL;
	}

	public List<String> getTags() {
		return tags;
	}

	public Long getTargetUserId() {
		return targetUserId;
	}

	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(final boolean following) {
		this.following = following;
	}

	public void setGame(final String game) {
		this.game = game;
	}

	public void setTags(final List<String> tags) {
		this.tags = tags;
	}

	public void setTargetUserId(final Long targetUserId) {
		this.targetUserId = targetUserId;
	}

	public void setUser(final User user) {
		assert user.getId() == id;
		this.originalProfileImageURL = user.getOriginalProfileImageURL();
	}

	public long getId() {
	   return id;
   }

}
