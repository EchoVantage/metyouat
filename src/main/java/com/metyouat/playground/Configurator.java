package com.metyouat.playground;

import static com.twitter.hbc.core.HttpHosts.USERSTREAM_HOST;
import static java.util.Collections.singletonList;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import scala.actors.threadpool.Arrays;
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
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.v3.Twitter4jUserstreamClient;
import com.twitter.hbc.twitter4j.v3.handler.UserstreamHandler;
import com.twitter.hbc.twitter4j.v3.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.v3.message.StallWarningMessage;

public class Configurator implements UserstreamHandler{
	private static final String accessTokenSecret = "RtoK8BBVgLvFjXYtsGD27WRgfahQ5y6GIbPc0e0yj8WsV";
	private static final String accessToken = "2197032072-Rbo2CNr61TAivXz0TNaJeTUlFJFGVld6D1QeFVm";
	private static final String consumerSecret = "5P7ALzTr0MdrvhqCN815rdD31OSnrxDDhZeltrd7Duo";
	private static final String consumerKey = "G8zZOeZpML3R55IcXq2Z8g";

	private final Twitter twitter;
	private final Twitter4jUserstreamClient client;
	private ExecutorService executorService;
	private ConcurrentMap<Long, Player> participants = new ConcurrentHashMap<>();
	private MasterSession master;
	
	public Configurator() throws IllegalStateException, TwitterException {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder()
	      .setDebugEnabled(true)
	      .setOAuthConsumerKey(consumerKey)
	      .setOAuthConsumerSecret(consumerSecret)
	      .setOAuthAccessToken(accessToken)
	      .setOAuthAccessTokenSecret(accessTokenSecret);
		twitter = new TwitterFactory(configBuilder.build()).getInstance();
		master = new MasterSession(twitter);
		getPlayer(twitter.getId(), master);

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
		ex.printStackTrace();
	}
	
	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		System.out.println("Track limit: "+numberOfLimitedStatuses);
	}
	
	@Override
	public void onStatus(Status status) {
		System.out.println("Status: "+status);
		getPlayer(status.getUser()).onStatus(status);
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		System.out.println("Stall warning: "+warning);
	}
	
	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		System.out.println("Scrub geo: "+userId+" "+upToStatusId);
	}
	
	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		System.out.println("Deletion: "+statusDeletionNotice);
	}
	
	@Override
	public void onUserProfileUpdate(User updatedUser) {
		System.out.println("profile update: "+updatedUser);
	}
	
	@Override
	public void onUserListUpdate(User listOwner, UserList list) {
		System.out.println("list update: "+listOwner+" "+list);
	}
	
	@Override
	public void onUserListUnsubscription(User subscriber, User listOwner,
			UserList list) {
		System.out.println("list unsub: "+subscriber+" "+listOwner+" "+list);
	}
	
	@Override
	public void onUserListSubscription(User subscriber, User listOwner,
			UserList list) {
		System.out.println("list sub: "+subscriber+" "+listOwner+" "+list);
	}
	
	@Override
	public void onUserListMemberDeletion(User deletedMember, User listOwner,
			UserList list) {
		System.out.println("list member delete: "+deletedMember+" "+listOwner+" "+list);
	}
	
	@Override
	public void onUserListMemberAddition(User addedMember, User listOwner,
			UserList list) {
		System.out.println("list member add: "+addedMember+" "+listOwner+" "+list);
	}
	
	@Override
	public void onUserListDeletion(User listOwner, UserList list) {
		System.out.println("list delete: "+listOwner+" "+list);
	}
	
	@Override
	public void onUserListCreation(User listOwner, UserList list) {
		System.out.println("list create: "+listOwner+" "+list);
	}
	
	@Override
	public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
		System.out.println("unfav: "+source+" "+target+" "+unfavoritedStatus);
	}
	
	@Override
	public void onUnblock(User source, User unblockedUser) {
		System.out.println("unblock: "+source+" "+unblockedUser);
	}
	
	@Override
	public void onFriendList(long[] friendIds) {
		System.out.println("friends: "+Arrays.toString(friendIds));
		for(long id: friendIds){
			participants.put(id, getPlayer(id, Session.PLAYING));
		}
	}
	
	@Override
	public void onFollow(User source, User followedUser) {
		System.out.println("follow: "+source+" "+followedUser);
	}
	
	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {
		System.out.println("fav: "+source+" "+target+" "+favoritedStatus);
	}
	
	@Override
	public void onDirectMessage(DirectMessage directMessage) {
		System.out.println("dm: "+directMessage);
	}
	
	@Override
	public void onDeletionNotice(long directMessageId, long userId) {
		System.out.println("del: "+directMessageId+" "+userId);
	}
	
	@Override
	public void onBlock(User source, User blockedUser) {
		System.out.println("follow: "+source+" "+blockedUser);
	}
	
	@Override
	public void onUnknownMessageType(String msg) {
		System.out.println("err: "+msg);
	}
	
	@Override
	public void onUnfollow(User source, User target) {
		System.out.println("unfollow: "+source+" "+target);
	}
	
	@Override
	public void onStallWarningMessage(StallWarningMessage warning) {
		System.out.println("stall: "+warning);
	}
	
	@Override
	public void onRetweet(User source, User target, Status retweetedStatus) {
		System.out.println("retweet: "+source+" "+target+" "+retweetedStatus);
	}
	
	@Override
	public void onDisconnectMessage(DisconnectMessage disconnectMessage) {
		System.out.println("disconnect: "+disconnectMessage);
	}

	public static void main(String[] args) throws IllegalStateException, TwitterException {
		Configurator config = new Configurator();
		config.start();
		config.close();
	}

	public Twitter getTwitter() {
		return twitter;
	}
	
	private Player getPlayer(long id, Session meta){
		Player player = participants.get(id);
		if(player == null){
			player = new Player(id, meta, master);
			Player old = participants.putIfAbsent(id, player);
			if(old != null){
				player = old;
			}
		}
		return player;
	}

	private Player getPlayer(User user){
		return getPlayer(user.getId(), Session.INACTIVE).setUser(user);
	}
}
