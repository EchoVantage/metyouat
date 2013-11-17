package com.metyouat.playground;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import com.metyouat.playground.Game.BaseGame;

public class Bot {
	public enum PlayerType{
		BOT{
			@Override
			void init(Bot bot) {
				bot.createPlayer(bot.getId(), BOT);
			}
		}, PLAYER, BYSTANDER;
		
		void init(Bot bot){
			// do nothing
		}
	}
	
	private Twitter twitter;
	private ConcurrentMap<Long, Player> players = new ConcurrentHashMap<>();
	private EnumMap<PlayerType, BaseGame> games;
	long id;

	public Bot(Twitter twitter) throws IllegalStateException, TwitterException {
		// must call withGame
		assert twitter != null : "Missing twitter";
		this.twitter = twitter;
		id = twitter.getId();
		games = new EnumMap<>(PlayerType.class);
	}
	
	public Bot(Twitter twitter, EnumMap<PlayerType, ? extends BaseGame> games) throws IllegalStateException, TwitterException{
		this(twitter);
		assert games != null : "Missing default games";
		for(Map.Entry<PlayerType, ? extends BaseGame> entry: games.entrySet()){
			withGame(entry.getKey(), entry.getValue());
		}
	}
	
	Bot withGame(PlayerType type, BaseGame game) {
		assert type != null : "Missing type";
		assert game != null : "Missing default game";
		assert this.games.get(type) == null : "Cannot change default game for "+type+" on bot";
		this.games.put(type, game.withBot(this));
		type.init(this);
		return this;
	}
	
	public void replyTo(Status status, String message) throws TwitterException {
		twitter.updateStatus(new StatusUpdate("@"+status.getUser().getScreenName()+" "+message).inReplyToStatusId(status.getId()));
	}

	public void replyTo(Player player, String message) throws TwitterException {
		twitter.sendDirectMessage(player.getId(), message);
	}

	public Player createPlayer(long id, PlayerType type) {
		Player player = new Player(id);
		Player old = players.putIfAbsent(player.getId(), player);
		return old == null ? player.withSession(gameFor(type).newSession(player)) : old;
	}

	public Game gameFor(PlayerType type){
		BaseGame game = games.get(type);
		assert game != null : "No game for "+type;
		return game;
	}
	
	public Player getPlayer(User user, PlayerType type) {
		return getPlayer(user.getId(), type).withUser(user);
	}

	public Player getPlayer(long id, PlayerType type) {
		Player player = players.get(id);
		return player == null ? createPlayer(id, type) : player;
	}

	public void follow(Player player) throws TwitterException {
		User user = twitter.createFriendship(player.getId());
		player.withUser(user);
	}

	public long getId() {
		return id;
	}
}
