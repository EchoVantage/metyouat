package com.metyouat.playground;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class Database implements AutoCloseable {
	private Connection c;
	private PreparedStatement savePlayer;
	private PreparedStatement saveStatus;
	private PreparedStatement tagStatus;
	private PreparedStatement saveMet;
	private PreparedStatement tagMet;
	private PreparedStatement newTarget;
	private PreparedStatement setGame;
	private PreparedStatement getPlayer;
	private PreparedStatement getStatusTags;
	private PreparedStatement followingPlayer;
	private PreparedStatement saveMention;
	private PreparedStatement getPoints;
	private PreparedStatement getLeaderboard;
	private PreparedStatement getFeed;

	@Override
	public void close() throws Exception {
		savePlayer.close();
		saveMention.close();
		saveStatus.close();
		tagStatus.close();
		saveMet.close();
		tagMet.close();
		newTarget.close();
		setGame.close();
		getPlayer.close();
		getStatusTags.close();
		getPoints.close();
		getLeaderboard.close();
		getFeed.close();
	}

	public void connect(final String host, final String user, final String password, final String schema, final String database) throws SQLException {
		try(Connection creator = DriverManager.getConnection("jdbc:postgresql://" + host + "/postgres", user, password);) {
			try(Statement create = creator.createStatement()) {
				try(ResultSet r = create.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + database + "'")) {
					if(!r.next()) {
						create.execute("CREATE DATABASE " + database);
					}
				}
			}
		}
		c = DriverManager.getConnection("jdbc:postgresql://" + host + "/" + database, user, password);
		StringBuilder ddl = new StringBuilder();
		try(InputStream in = ClassLoader.getSystemResourceAsStream(database + ".sql");
		      BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
			String line;
			while((line = reader.readLine()) != null) {
				ddl.append(line).append('\n');
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}

		try(Statement init = c.createStatement()) {
			init.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
			init.execute("SET search_path TO " + schema + ",public");
			init.execute(ddl.toString());
		}

		savePlayer = c.prepareStatement("SELECT savePlayer(?, ?, ?, ?)");
		saveMention = c.prepareStatement("SELECT saveMention(?, ?, ?)");
		saveStatus = c.prepareStatement("SELECT saveStatus(?, ?, ?, ?)");
		tagStatus = c.prepareStatement("SELECT tagStatus(?, ?)");
		saveMet = c.prepareStatement("SELECT * FROM saveMet(?, ?, ?)");
		tagMet = c.prepareStatement("SELECT tagMet(?, ?)");
		newTarget = c.prepareStatement("SELECT newTarget(?)");
		setGame = c.prepareStatement("SELECT setGame(?, ?, ?)");
		getPlayer = c.prepareStatement("SELECT * FROM getPlayer(?)");
		getStatusTags = c.prepareStatement("SELECT * FROM getStatusTags(?)");
		followingPlayer = c.prepareStatement("SELECT followingPlayer(?)");
		getPoints = c.prepareStatement("SELECT * FROM getPoints(?)");
		getLeaderboard = c.prepareStatement("SELECT * FROM getLeaderboard(?)");
		getFeed = c.prepareStatement("SELECT * FROM getFeed(?)");
	}

	public void followingPlayer(final long playerId) throws SQLException {
		followingPlayer.setLong(1, playerId);
		followingPlayer.execute();
	}

	public Player getPlayer(final long playerId) throws SQLException {
		getPlayer.setLong(1, playerId);
		try(ResultSet r = getPlayer.executeQuery()) {
			r.next();
			Player player = new Player(r.getLong("id"), r.getString("screenName"), r.getString("name"), r.getString("originalProfileImageURL"), r.getBoolean("following"), r.getString("game"), r.getLong("targetPlayerId"));
			player.setTags(getStatusTags(r.getLong("gameStatusId")));
			return player;
		}
	}

	public long getPoints(final long playerId) throws SQLException {
		getPoints.setLong(1, playerId);
		try(ResultSet r = getPoints.executeQuery()) {
			if(r.next()){
				return r.getLong("points");
			}
			return 0;
		}
	}

	public List<Map<String,Object>> getLeaderboard(final String game) throws SQLException {
		getLeaderboard.setString(1, game);
		try(ResultSet r = getLeaderboard.executeQuery()) {
			return rip(r);
		}
	}

	private List<Map<String, Object>> rip(ResultSet r) throws SQLException {
	   List<Map<String,Object>> ret = new ArrayList<>();
	   while(r.next()){
	   	Map<String,Object> o = new HashMap<>();
	   	for(int i=0;i<r.getMetaData().getColumnCount();i++){
	   		o.put(r.getMetaData().getColumnLabel(i+1), r.getObject(i+1));
	   	}
	   	ret.add(o);
	   }
	   return ret;
   }

	public List<Map<String, Object>> getFeed(final String game) throws SQLException {
		getFeed.setString(1, game);
		try(ResultSet r = getFeed.executeQuery()) {
			return rip(r);
		}
	}

	public List<String> getStatusTags(final long statusId) throws SQLException {
		List<String> tags = new ArrayList<>();
		getStatusTags.setLong(1, statusId);
		try(ResultSet r = getStatusTags.executeQuery()) {
			while(r.next()) {
				tags.add(r.getString(1));
			}
		}
		return tags;
	}

	public Long newTarget(final User player) throws SQLException {
		return newTarget(player.getId());
	}

	public void saveMention(final Status status, final UserMentionEntity mention, final List<String> tags) throws SQLException {
		if(mention.getId() == status.getUser().getId()) {
			return;
		}
		saveMention(mention.getId(), mention.getScreenName(), mention.getName());
		long metId = saveMet(status.getUser().getId(), mention.getId(), status.getId());
		for(String tag : tags) {
			tagMet(metId, tag);
		}
	}

	public void savePlayer(final User player) throws SQLException {
		savePlayer(player.getId(), player.getScreenName(), player.getName(), player.getOriginalProfileImageURL());
	}

	public void saveStatus(final Status status, final List<String> tags) throws SQLException {
		saveStatus(status.getId(), status.getUser().getId(), status.getCreatedAt().getTime(), status.getText());
		for(String tag : tags) {
			tagStatus(status.getId(), tag);
		}
	}

	public String setGame(final Status status) throws SQLException {
		HashtagEntity first = null;
		for(HashtagEntity tag : status.getHashtagEntities()) {
			if(!"imet".equalsIgnoreCase(tag.getText()) && (first == null || first.getStart() > tag.getStart())) {
				first = tag;
			}
		}
		String game = first == null ? null : first.getText();
		//XXX: For today's game...
		game = "CommonDesk";
		setGame(status.getUser().getId(), status.getId(), game);
		return game;
	}

	private Long newTarget(final long playerId) throws SQLException {
		newTarget.setLong(1, playerId);
		try(ResultSet r = newTarget.executeQuery()) {
			r.next();
			long value = r.getLong(1);
			return r.wasNull() ? null : value;
		}
	}

	private void saveMention(final long playerId, final String screenName, final String name) throws SQLException {
		saveMention.setLong(1, playerId);
		saveMention.setString(2, screenName);
		saveMention.setString(3, name);
		saveMention.execute();
	}

	private long saveMet(final long playerId, final long targetPlayerId, final long statusId) throws SQLException {
		saveMet.setLong(1, playerId);
		saveMet.setLong(2, targetPlayerId);
		saveMet.setLong(3, statusId);
		try(ResultSet r = saveMet.executeQuery()) {
			r.next();
			return r.getLong(1);
		}
	}

	private void savePlayer(final long playerId, final String screenName, final String name, final String originalProfileImageURL) throws SQLException {
		savePlayer.setLong(1, playerId);
		savePlayer.setString(2, screenName);
		savePlayer.setString(3, name);
		savePlayer.setString(4, originalProfileImageURL);
		savePlayer.execute();
	}

	private void saveStatus(final long statusId, final long playerId, final long ts, final String message) throws SQLException {
		saveStatus.setLong(1, statusId);
		saveStatus.setLong(2, playerId);
		saveStatus.setLong(3, ts);
		saveStatus.setString(4, message);
		saveStatus.execute();
	}

	private void setGame(final long playerId, final long gameStatusId, final String game) throws SQLException {
		setGame.setLong(1, playerId);
		setGame.setLong(2, gameStatusId);
		setGame.setString(3, game);
		setGame.execute();
	}

	private void tagMet(final long metId, final String tag) throws SQLException {
		tagMet.setLong(1, metId);
		tagMet.setString(2, tag);
		tagMet.execute();
	}

	private void tagStatus(final long statusId, final String tag) throws SQLException {
		tagStatus.setLong(1, statusId);
		tagStatus.setString(2, tag);
		tagStatus.execute();
	}
}
