package com.metyouat.playground;

import java.util.ArrayList;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;

import com.metyouat.playground.Game.BaseGame;
import com.metyouat.playground.Session.BaseSession;

public class PlayerSession extends BaseSession{
	public static final BaseGame GAME = new BaseGame(){
		public BaseSession newSession(Player player) {
			return new PlayerSession().withGame(GAME);
		};
	};
	
	private List<Status> statuses = new ArrayList<>();
	
	@Override
	public void addStatus(Player player, Status status) throws IllegalStateException, TwitterException {
		if(findTag(status, "imet") != null){
			statuses.add(status);
		}else if(findMention(status, bot().getId()) != null){
			HashtagEntity tag = firstTag(status);
			if(tag == null){
				statuses.add(status);
			}else if(statuses.isEmpty()){
				statuses.add(status);
				replyTo(status, "I'll watch your feed for tweets tagged with #IMet and add them to the #"+tag.getText()+" event");
			}else{
				player.withSession(game().newSession(player)).onStatus(status);
			}
		}
	}
}
