package com.metyouat.playground;

import twitter4j.Status;
import twitter4j.TwitterException;

import com.metyouat.playground.Bot.PlayerType;
import com.metyouat.playground.Game.BaseGame;
import com.metyouat.playground.Session.BaseSession;

public class FriendSession extends BaseSession{
	public static final BaseGame GAME = new BaseGame(new FriendSession());
	
	@Override
	public void addStatus(Player player, Status status) throws IllegalStateException, TwitterException {
		if(findMention(status, bot().getId()) != null){
			bot().follow(player);
			player.withSession(bot().gameFor(PlayerType.PLAYER).newSession(player)).onStatus(status);
		}
	}
}
