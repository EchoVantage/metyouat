package com.metyouat.playground;

import static com.metyouat.playground.Bot.PlayerType.BYSTANDER;
import static com.twitter.hbc.core.HttpHosts.USERSTREAM_HOST;
import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.metyouat.playground.Bot.PlayerType;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.v3.Twitter4jUserstreamClient;
import com.twitter.hbc.twitter4j.v3.handler.UserstreamHandler;
import com.twitter.hbc.twitter4j.v3.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.v3.message.StallWarningMessage;

public class BotMain implements UserstreamHandler{
	private static final String accessTokenSecret = "RtoK8BBVgLvFjXYtsGD27WRgfahQ5y6GIbPc0e0yj8WsV";
	private static final String accessToken = "2197032072-Rbo2CNr61TAivXz0TNaJeTUlFJFGVld6D1QeFVm";
	private static final String consumerSecret = "5P7ALzTr0MdrvhqCN815rdD31OSnrxDDhZeltrd7Duo";
	private static final String consumerKey = "G8zZOeZpML3R55IcXq2Z8g";

	private final Twitter twitter;
	private final Twitter4jUserstreamClient client;
	private ExecutorService executorService;
	private Bot bot;
	
	public BotMain() throws IllegalStateException, TwitterException {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder()
	      .setDebugEnabled(true)
	      .setOAuthConsumerKey(consumerKey)
	      .setOAuthConsumerSecret(consumerSecret)
	      .setOAuthAccessToken(accessToken)
	      .setOAuthAccessTokenSecret(accessTokenSecret);
		twitter = new TwitterFactory(configBuilder.build()).getInstance();
		bot = new Bot(twitter);
		bot.withGame(PlayerType.BOT, BotSession.GAME);
		bot.withGame(PlayerType.BYSTANDER, FriendSession.GAME);
		bot.withGame(PlayerType.PLAYER, PlayerSession.GAME);

		final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(100000);

		final UserstreamEndpoint endpoint = new UserstreamEndpoint();
		endpoint.withFollowings(true);
		endpoint.allReplies(true);
		
		ClientBuilder builder = new ClientBuilder()
		  .name("MetYouAt Streamer")                              // optional: mainly for the logs
		  .hosts(USERSTREAM_HOST)
		  .authentication(new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret))
		  .endpoint(endpoint)
		  .processor(new StringDelimitedProcessor(msgQueue));

		executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).build());
		client = new Twitter4jUserstreamClient(builder.build(), msgQueue, singletonList((UserStreamListener)this), executorService);
	}
	
	public void start(){
		client.connect();
		client.process();
		client.process();
		client.process();
	}
	
	public void close(){
		client.stop();
		executorService.shutdown();
		twitter.shutdown();
	}

	@Override
	public void onException(Exception ex) {
		logEvent("Exception",map("ex",ex));
	}
	
	private static void logEvent(String type, Map<String, Object> map) {
		System.out.println(type+": "+map);
   }

	private static Map<String,Object> map(String key, Object value) {
	   return map(new HashMap<String, Object>(), key, value);
   }

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		logEvent("TrackLimitationNotice",map("numberOfLimitedStatuses",numberOfLimitedStatuses));
	}
	
	@Override
	public void onStatus(Status status) {
		logEvent("Status",map("status",status));
		try {
			bot.getPlayer(status.getUser(), BYSTANDER).onStatus(status);
		} catch (IllegalStateException | TwitterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		logEvent("StallWarning",map("warning",warning));
	}
	
	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		logEvent("ScrubGeo",map(map("userId",userId),"upToStatusId",upToStatusId));
	}
	
	private static Map<String, Object> map(Map<String, Object> map, String key, Object value) {
		map.put(key, value);
	   return map;
   }

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		logEvent("DeletionNotice", map("statusDeletionNotice",statusDeletionNotice));
	}
	
	@Override
	public void onUserProfileUpdate(User updatedUser) {
		logEvent("UserProfileUpdate",map("updatedUser",updatedUser));
	}
	
	@Override
	public void onUserListUpdate(User listOwner, UserList list) {
		logEvent("UserListUpdate",map(map("listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUserListUnsubscription(User subscriber, User listOwner,
			UserList list) {
		logEvent("UserListUnsubscription",map(map(map("subscriber",subscriber),"listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUserListSubscription(User subscriber, User listOwner,
			UserList list) {
		logEvent("UserListSubscription",map(map(map("subscriber",subscriber),"listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUserListMemberDeletion(User deletedMember, User listOwner,
			UserList list) {
		logEvent("UserListMemberDeletion",map(map(map("deletedMember",deletedMember),"listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUserListMemberAddition(User addedMember, User listOwner,
			UserList list) {
		logEvent("UserListMemberAddition",map(map(map("addedMember",addedMember),"listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUserListDeletion(User listOwner, UserList list) {
		logEvent("UserListDeletion",map(map("listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUserListCreation(User listOwner, UserList list) {
		logEvent("UserListCreation",map(map("listOwner",listOwner),"list",list));
	}
	
	@Override
	public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
		logEvent("Unfavorite",map(map(map("source",source),"target",target),"unfavoritedStatus",unfavoritedStatus));
	}
	
	@Override
	public void onUnblock(User source, User unblockedUser) {
		logEvent("Unblock",map(map("source",source),"unblockedUser",unblockedUser));
	}
	
	@Override
	public void onFriendList(final long[] friendIds) {
		logEvent("FriendList",map("friendIds",PrimitiveList.asList(friendIds)));
		for(long id: friendIds){
			bot.createPlayer(id, PlayerType.PLAYER);
		}
	}
	
	@Override
	public void onFollow(User source, User followedUser) {
		logEvent("Follow",map(map("source",source),"followedUser",followedUser));
	}
	
	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {
		logEvent("Favorite",map(map(map("source",source),"target",target),"favoritedStatus",favoritedStatus));
	}
	
	@Override
	public void onDirectMessage(DirectMessage directMessage) {
		logEvent("DirectMessage",map("directMessage",directMessage));
	}
	
	@Override
	public void onDeletionNotice(long directMessageId, long userId) {
		logEvent("DeletionNotice",map(map("directMessageId",directMessageId),"userId",userId));
	}
	
	@Override
	public void onBlock(User source, User blockedUser) {
		logEvent("Block",map(map("source",source),"blockedUser",blockedUser));
	}
	
	@Override
	public void onUnknownMessageType(String msg) {
		logEvent("UnknownMessageType",map("msg",msg));
	}
	
	@Override
	public void onUnfollow(User source, User target) {
		logEvent("Unfollow",map(map("source",source),"target",target));
	}
	
	@Override
	public void onStallWarningMessage(StallWarningMessage warning) {
		logEvent("StallWarningMessage",map("warning",warning));
	}
	
	@Override
	public void onRetweet(User source, User target, Status retweetedStatus) {
		logEvent("Retweet",map(map(map("source",source),"target",target),"favoritedStatus",retweetedStatus));
	}
	
	@Override
	public void onDisconnectMessage(DisconnectMessage disconnectMessage) {
		logEvent("DisconnectMessage",map("disconnectMessage",disconnectMessage));
	}

	public static void main(String[] args) throws IllegalStateException, TwitterException {
		BotMain config = new BotMain();
		config.start();
		config.close();
	}

	public Twitter getTwitter() {
		return twitter;
	}
}
