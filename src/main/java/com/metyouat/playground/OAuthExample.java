package com.metyouat.playground;

import java.io.IOException;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class OAuthExample {
	public static void main(String[] args) throws TwitterException, IOException {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("PNQjdFdx2s9560ofbOWQ", "uz8YmVfB2mUqbj0YBN3kS7Vs7A4Bm2Qnty9gbnEivE");
		RequestToken requestToken = twitter.getOAuthRequestToken("http://localhost:8080/auth-complete");
		System.out.println(requestToken);
		System.out.println(requestToken.getAuthenticationURL());
		System.in.read();
		twitter.getOAuthAccessToken(requestToken);
		IDs followersIDs = twitter.getFollowersIDs("mikedeck", 0);
		System.out.println(followersIDs);
	}
}
