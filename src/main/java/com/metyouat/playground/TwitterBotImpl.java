package com.metyouat.playground;

import static com.twitter.hbc.core.HttpHosts.USERSTREAM_HOST;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import twitter4j.StatusUpdate;
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

public class TwitterBotImpl implements TwitterBot, UserstreamHandler, AutoCloseable {
	private static void logEvent(final String type, final Map<String, Object> map) {
		System.out.println(System.currentTimeMillis()+" "+type + ": " + map);
	}

	private static Map<String, Object> map(final Map<String, Object> map, final String key, final Object value) {
		map.put(key, value);
		return map;
	}

	private static Map<String, Object> map(final String key, final Object value) {
		return map(new HashMap<String, Object>(), key, value);
	}

	private Twitter twitter;

	private Twitter4jUserstreamClient client;

	private ExecutorService executorService;
	
	private Callback callback;
	
	public TwitterBotImpl(Callback callback){
		this.callback = callback;
	}

	public void connect(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder()
		      .setDebugEnabled(true)
		      .setOAuthConsumerKey(consumerKey)
		      .setOAuthConsumerSecret(consumerSecret)
		      .setOAuthAccessToken(accessToken)
		      .setOAuthAccessTokenSecret(accessTokenSecret);
		twitter = new TwitterFactory(configBuilder.build()).getInstance();

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
		client = new Twitter4jUserstreamClient(builder.build(), msgQueue, singletonList((UserStreamListener) this), executorService);
		client.connect();
		client.process();
		client.process();
		client.process();
	}

	@Override
   public void close() throws Exception {
		client.stop();
		executorService.shutdown();
		twitter.shutdown();
	}
	
	@Override
	public long getId() {
		try {
	      return twitter.getId();
      } catch(IllegalStateException | TwitterException e) {
	      throw new RuntimeException(e);
      }
	}
	
	@Override
	public User follow(long id) throws TwitterException {
	   return twitter.createFriendship(id);
	}

	@Override
	public void onBlock(final User source, final User blockedUser) {
		logEvent("Block", map(map("source", source), "blockedUser", blockedUser));
	}

	@Override
	public void onDeletionNotice(final long directMessageId, final long userId) {
		logEvent("DeletionNotice", map(map("directMessageId", directMessageId), "userId", userId));
	}

	@Override
	public void onDeletionNotice(final StatusDeletionNotice statusDeletionNotice) {
		logEvent("DeletionNotice", map("statusDeletionNotice", statusDeletionNotice));
	}

	@Override
	public void onDirectMessage(final DirectMessage directMessage) {
		logEvent("DirectMessage", map("directMessage", directMessage));
	}

	@Override
	public void onDisconnectMessage(final DisconnectMessage disconnectMessage) {
		logEvent("DisconnectMessage", map("disconnectMessage", disconnectMessage));
	}

	@Override
	public void onException(final Exception ex) {
		logEvent("Exception", map("ex", ex));
		ex.printStackTrace();
	}

	@Override
	public void onFavorite(final User source, final User target, final Status favoritedStatus) {
		logEvent("Favorite", map(map(map("source", source), "target", target), "favoritedStatus", favoritedStatus));
	}

	@Override
	public void onFollow(final User source, final User followedUser) {
		logEvent("Follow", map(map("source", source), "followedUser", followedUser));
	}

	@Override
	public void onFriendList(final long[] friendIds) {
		logEvent("FriendList", map("friendIds", PrimitiveList.asList(friendIds)));
		for(long friendId : friendIds) {
			callback.onFriend(friendId);
		}
	}

	@Override
	public void onRetweet(final User source, final User target, final Status retweetedStatus) {
		logEvent("Retweet", map(map(map("source", source), "target", target), "favoritedStatus", retweetedStatus));
	}

	@Override
	public void onScrubGeo(final long userId, final long upToStatusId) {
		logEvent("ScrubGeo", map(map("userId", userId), "upToStatusId", upToStatusId));
	}

	@Override
	public void onStallWarning(final StallWarning warning) {
		logEvent("StallWarning", map("warning", warning));
	}

	@Override
	public void onStallWarningMessage(final StallWarningMessage warning) {
		logEvent("StallWarningMessage", map("warning", warning));
	}

	@Override
	public void onStatus(final Status status) {
		logEvent("Status", map("status", status));
		callback.onStatus(status);
	}

	@Override
	public void onTrackLimitationNotice(final int numberOfLimitedStatuses) {
		logEvent("TrackLimitationNotice", map("numberOfLimitedStatuses", numberOfLimitedStatuses));
	}

	@Override
	public void onUnblock(final User source, final User unblockedUser) {
		logEvent("Unblock", map(map("source", source), "unblockedUser", unblockedUser));
	}

	@Override
	public void onUnfavorite(final User source, final User target, final Status unfavoritedStatus) {
		logEvent("Unfavorite", map(map(map("source", source), "target", target), "unfavoritedStatus", unfavoritedStatus));
	}

	@Override
	public void onUnfollow(final User source, final User target) {
		logEvent("Unfollow", map(map("source", source), "target", target));
	}

	@Override
	public void onUnknownMessageType(final String msg) {
		logEvent("UnknownMessageType", map("msg", msg));
	}

	@Override
	public void onUserListCreation(final User listOwner, final UserList list) {
		logEvent("UserListCreation", map(map("listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserListDeletion(final User listOwner, final UserList list) {
		logEvent("UserListDeletion", map(map("listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserListMemberAddition(final User addedMember, final User listOwner,
	      final UserList list) {
		logEvent("UserListMemberAddition", map(map(map("addedMember", addedMember), "listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserListMemberDeletion(final User deletedMember, final User listOwner,
	      final UserList list) {
		logEvent("UserListMemberDeletion", map(map(map("deletedMember", deletedMember), "listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserListSubscription(final User subscriber, final User listOwner,
	      final UserList list) {
		logEvent("UserListSubscription", map(map(map("subscriber", subscriber), "listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserListUnsubscription(final User subscriber, final User listOwner,
	      final UserList list) {
		logEvent("UserListUnsubscription", map(map(map("subscriber", subscriber), "listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserListUpdate(final User listOwner, final UserList list) {
		logEvent("UserListUpdate", map(map("listOwner", listOwner), "list", list));
	}

	@Override
	public void onUserProfileUpdate(final User updatedUser) {
		logEvent("UserProfileUpdate", map("updatedUser", updatedUser));
	}

	@Override
	public void replyTo(final Status status, final String message)
	      throws TwitterException {
		twitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + message).inReplyToStatusId(status.getId()));
	}

	@Override
	public void replyTo(final Status status, final String message, final URL media)
	      throws TwitterException, IOException {
		try(InputStream stream = media.openStream()) {
			twitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + message).media("Your next target", stream).inReplyToStatusId(status.getId()));
		}
	}
}
