package com.metyouat.playground;

import twitter4j.Status;
import twitter4j.TwitterException;

import com.metyouat.playground.Session.BaseSession;

public interface Game {
	class BaseGame implements Game{
		private Bot bot;
		private BaseSession session;

		protected BaseGame(){
			// must call withBot and withSession
		}

		protected BaseGame(BaseSession session){
			// must call withBot
			withSession(session);
		}
		
		protected BaseGame(Bot bot){
			// must call withSession
			withBot(bot);
		}
		
		protected BaseGame(Bot bot, BaseSession session){
			withBot(bot).withSession(session);
		}
		
		BaseGame withBot(Bot bot){
			assert this.bot == null || this.bot == bot: "Cannot change bot on game";
			assert bot != null : "Missing bot";
			this.bot = bot;
			return this;
		}
		
		BaseGame withSession(BaseSession session){
			assert this.session == null || session == this.session : "Cannot change default session on game";
			assert session != null : "Missing default session";
			this.session = session.withGame(this);
			return this;
		}
		
		@Override
		public BaseSession newSession(Player player) {
			assert session != null : "Game missing default session";
			return session;
		}
		
		@Override
		public Bot bot() {
			assert bot != null : "Game missing bot";
			return bot;
		}
	}
	
	BaseSession newSession(Player player);

	Bot bot();
}
