package com.metyouat.playground;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

import com.metyouat.playground.Game.BaseGame;

public interface Session {
	abstract class BaseSession implements Session {
		public static UserMentionEntity findMention(Status status, long id) {
			for (UserMentionEntity user : status.getUserMentionEntities()) {
				if (user.getId() == id) {
					return user;
				}
			}
			return null;
		}

		public static HashtagEntity findTag(Status status, String text) {
			for (HashtagEntity tag : status.getHashtagEntities()) {
				if (tag.getText().equalsIgnoreCase(text)) {
					return tag;
				}
			}
			return null;
		}
		
		public static HashtagEntity firstTag(Status status){
			HashtagEntity tag = null;
			for(HashtagEntity entity: status.getHashtagEntities()){
				if(tag == null || tag.getStart() > entity.getStart()){
					tag = entity;
				}
			}
			return tag;
		}

		private Game game;

		protected BaseSession(Game game) {
			assert game != null : "Missing game";
			this.game = game;
		}

		protected BaseSession() {
			// must call withGame
		}

		@Override
		public Game game() {
			assert game != null : "Session missing game";
			return game;
		}
		
		public Bot bot() {
			return game().bot();
		}

		public void replyTo(Player player, String message)
				throws TwitterException {
			bot().replyTo(player, message);
		}
		
		public void replyTo(Status status, String message)
				throws TwitterException {
			bot().replyTo(status, message);
		}
		
		BaseSession withGame(BaseGame game) {
			assert this.game == null || this.game == game : "Cannot change game on session";
			assert game != null : "Missing game";
			this.game = game;
			return this;
		}
	}

	Game game();

	void addStatus(Player player, Status status) throws IllegalStateException, TwitterException;
}
