package com.metyouat.playground;

import twitter4j.Status;

import com.metyouat.playground.Game.BaseGame;
import com.metyouat.playground.Session.BaseSession;

public class BotSession extends BaseSession {
	public static final BaseGame GAME = new BaseGame(new BotSession()){
		@Override
		public BaseSession newSession(Player player) {
			assert player.getId() == bot().getId() : "Only the bot can have a bot session";
			return super.newSession(player);
		}
	};
	
	@Override
	public void addStatus(Player player, Status status) {
		// ignore
	}
}
