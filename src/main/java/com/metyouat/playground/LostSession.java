package com.metyouat.playground;

import twitter4j.Status;

import com.metyouat.playground.Game.BaseGame;
import com.metyouat.playground.Session.BaseSession;

public class LostSession extends BaseSession {
	public LostSession(Bot bot) {
		withGame(new BaseGame(bot, this));
	}

	@Override
	public void addStatus(Player player, Status status) {
		
	}
}
