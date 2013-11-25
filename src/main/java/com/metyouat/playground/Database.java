package com.metyouat.playground;

import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class Database {

	private void saveUser(long userId, String screenName, String name, String originalProfileImageURL) {
	   // TODO Auto-generated method stub
	   
   }

	private void saveStatus(long statusId, long userId, long time, String text) {
	   // TODO Auto-generated method stub
	   
   }

	private long saveMet(long userId, long targetUserId, long statusId) {
	   // TODO Auto-generated method stub
	   return 0;
   }

	private void linkMet(long metId, String tag) {
	   // TODO Auto-generated method stub
	   
   }

	public Player getPlayer(long userId) {
	   // TODO Auto-generated method stub
	   return null;
   }

	private Long newTarget(long userId) {
	   // TODO Auto-generated method stub
	   return null;
   }

	private void linkStatus(long statusId, String tag) {
	   // TODO Auto-generated method stub
	   
   }

	private void setGame(long userId, long statusId, String string) {
	   // TODO Auto-generated method stub
	   
   }

	public void saveStatus(Status status, List<String> tags) {
		saveStatus(status.getId(), status.getUser().getId(), status.getCreatedAt().getTime(), status.getText());
		for(String tag: tags){
			linkStatus(status.getId(), tag);
		}
   }

	public void saveMention(Status status, UserMentionEntity mention, List<String> tags) {
		long metId = saveMet(status.getUser().getId(), mention.getId(), status.getId());
		for(String tag: tags){
			linkMet(metId, tag);
		}
   }

	public void saveUser(User user) {
		saveUser(user.getId(), user.getScreenName(), user.getName(), user.getOriginalProfileImageURL());
   }

	public Long newTarget(User user) {
		return newTarget(user.getId());
   }

	public String setGame(Status status) {
		HashtagEntity first = null;
		for (HashtagEntity tag : status.getHashtagEntities()) {
			if (first == null || first.getStart() > tag.getStart()) {
				first = tag;
			}
		}
		String game = first == null ? null : first.getText();
		setGame(status.getUser().getId(), status.getId(), game);
	   return game;
   }
}
