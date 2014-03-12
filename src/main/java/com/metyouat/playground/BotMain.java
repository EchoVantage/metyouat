package com.metyouat.playground;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class BotMain implements TwitterBot.Callback, AutoCloseable {
	private static final int port = 8080;
	private static final String accessTokenSecret = "RtoK8BBVgLvFjXYtsGD27WRgfahQ5y6GIbPc0e0yj8WsV";
	private static final String accessToken = "2197032072-Rbo2CNr61TAivXz0TNaJeTUlFJFGVld6D1QeFVm";
	private static final String consumerSecret = "5P7ALzTr0MdrvhqCN815rdD31OSnrxDDhZeltrd7Duo";
	private static final String consumerKey = "G8zZOeZpML3R55IcXq2Z8g";
	private static final String host = "localhost";
	private static final String username = "fuwjax";
	private static final String password = "";
	private static final String schema = "common";
	private static final String database = "mya";
	private static final String leaderboardHost = "192.168.1.230";
	private static final String root = "src/main/html";
	
	public static UserMentionEntity findMention(final Status status, final Long id) {
		for(UserMentionEntity user : status.getUserMentionEntities()) {
			if(user.getId() == id) {
				return user;
			}
		}
		return null;
	}

	public static HashtagEntity findTag(final Status status, final String text) {
		for(HashtagEntity tag : status.getHashtagEntities()) {
			if(tag.getText().equalsIgnoreCase(text)) {
				return tag;
			}
		}
		return null;
	}

	public static void main(final String[] args) throws Exception {
		BotMain config = new BotMain();
		config.connect();
		config.close();
	}

	public static List<String> tags(final Status status) {
		List<String> tags = new ArrayList<>();
		for(HashtagEntity tag : status.getHashtagEntities()) {
			tags.add(tag.getText());
		}
		return tags;
	}

	private final TwitterBotImpl bot = new TwitterBotImpl(this);

	private final Map<Long, Player> players = new HashMap<>();

	private final Database db = new Database();
	
	private final Leaderboard leaderboard = new Leaderboard();

	@Override
   public void close() throws Exception {
		leaderboard.close();
		bot.close();
		db.close();
	}

	@Override
	public void onFriend(long friendId) {
		// ignore
	}

	@Override
	public void onStatus(Status status) {
		try {
			if(findMention(status, bot.getId()) != null && status.getUserMentionEntities().length > 1) {
				User user = status.getUser();
				db.savePlayer(user);
				Player player = getPlayer(user.getId());
				player.setUser(user);
				List<String> tags = tags(status);
				db.saveStatus(status, tags);
				String game = db.setGame(status);
				player.setTags(tags);
				player.setGame(game);
				if(game == null) {
					bot.replyTo(status, "Your score is "+db.getPoints(user.getId())+". Tag people with #IMet to score points. Join a game for bonuses.");
				} else {
					Long targetUserId = db.newTarget(user);
					if(targetUserId == null) {
						bot.replyTo(status, "Your score is "+db.getPoints(user.getId())+". Tag people with #IMet to score points.");
					} else {
						Player target = getPlayer(targetUserId);
						bot.replyTo(status, "Your score is "+db.getPoints(user.getId())+". Tag people with #IMet to score points. Find this player: ", new URL(target.getOriginalProfileImageURL()));
					}
					player.setTargetUserId(targetUserId);
				}
			}
		} catch(IllegalStateException | TwitterException | IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void connect() throws Exception {
		db.connect(host, username, password, schema, database);
		bot.connect(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		leaderboard.connect(leaderboardHost, port, Paths.get(root), db);
	}

	private Player getPlayer(final long id) throws SQLException {
		Player player = players.get(id);
		if(player == null) {
			player = db.getPlayer(id);
			players.put(id, player);
		}
		return player;
	}
}
