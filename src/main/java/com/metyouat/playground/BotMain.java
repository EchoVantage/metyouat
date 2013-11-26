package com.metyouat.playground;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
	private static final String accessTokenSecret = "RtoK8BBVgLvFjXYtsGD27WRgfahQ5y6GIbPc0e0yj8WsV";
	private static final String accessToken = "2197032072-Rbo2CNr61TAivXz0TNaJeTUlFJFGVld6D1QeFVm";
	private static final String consumerSecret = "5P7ALzTr0MdrvhqCN815rdD31OSnrxDDhZeltrd7Duo";
	private static final String consumerKey = "G8zZOeZpML3R55IcXq2Z8g";
	private static final String host = "localhost";
	private static final String user = "fuwjax";
	private static final String password = "";
	private static final String schema = "common";
	private static final String database = "mya";
	

	public static UserMentionEntity findMention(final Status status, final long id) {
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
		config.start();
		config.close();
	}

	public static List<String> tags(final Status status) {
		List<String> tags = new ArrayList<>();
		for(HashtagEntity tag : status.getHashtagEntities()) {
			if(!"imet".equalsIgnoreCase(tag.getText())) {
				tags.add(tag.getText());
			}
		}
		return tags;
	}

	private final TwitterBotImpl bot = new TwitterBotImpl(this);

	private final Map<Long, Player> players = new HashMap<>();

	private Database db = new Database();

	public void close() throws Exception {
		bot.close();
		db.close();
	}

	@Override
	public void onFriend(long friendId) {
		try {
			db.followingPlayer(friendId);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatus(Status status) {
		try {
			User user = status.getUser();
			if(user.getId() == bot.getId()) {
				return;
			}
			db.savePlayer(user);
			Player player = getPlayer(user.getId());
			if(!player.isFollowing()) {
				user = bot.follow(user.getId());
				db.followingPlayer(user.getId());
				player.setFollowing(true);
				player.setUser(user);
			}
			List<String> tags = tags(status);
			if(findTag(status, "imet") != null) {
				tags.addAll(player.getTags());
				db.saveStatus(status, tags);
				for(UserMentionEntity mention : status.getUserMentionEntities()) {
					if(mention.getId() != bot.getId()) {
						db.saveMention(status, mention, tags);
					}
				}
				UserMentionEntity mention = player.getTargetUserId() == null ? null : findMention(status, player.getTargetUserId());
				if(mention != null) {
					player.setTargetUserId(newTarget(status, "You found " + mention.getName() + "!"));
				} else {
					bot.replyTo(status, "You have " + db.getPoints(user.getId()) + " points!");
				}
			} else if(findMention(status, bot.getId()) != null) {
				db.saveStatus(status, tags);
				String game = db.setGame(status);
				player.setTags(tags);
				player.setGame(game);
				if(game == null) {
					bot.replyTo(status, "Thanks for playing. You won't get targets or be targeted, but I'll still watch for #IMet tweets.");
				} else {
					player.setTargetUserId(newTarget(status, "You're meeting people at #" + game + "!"));
				}
			}
		} catch(IllegalStateException | TwitterException | IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void start() throws SQLException {
		db.connect(host, user, password, schema, database);
		bot.connect(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}

	private Player getPlayer(final long id) throws SQLException {
		Player player = players.get(id);
		if(player == null) {
			player = db.getPlayer(id);
			players.put(id, player);
		}
		return player;
	}

	private Long newTarget(final Status status, final String prefix) throws TwitterException, IOException, MalformedURLException, SQLException {
		Long targetUserId = db.newTarget(status.getUser());
		if(targetUserId == null) {
			bot.replyTo(status, prefix + " There aren't any available targets; I'll watch your feed for tweets tagged with #IMet.");
		} else {
			Player target = getPlayer(targetUserId);
			bot.replyTo(status, prefix + " I'll watch your feed for tweets tagged with #IMet.", new URL(target.getOriginalProfileImageURL()));
		}
		return targetUserId;
	}
}
