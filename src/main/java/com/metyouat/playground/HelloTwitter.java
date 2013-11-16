package com.metyouat.playground;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class HelloTwitter {
	public static void main(String[] args) throws TwitterException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		      .setOAuthConsumerKey("G8zZOeZpML3R55IcXq2Z8g")
		      .setOAuthConsumerSecret("5P7ALzTr0MdrvhqCN815rdD31OSnrxDDhZeltrd7Duo")
		      .setOAuthAccessToken("2197032072-Rbo2CNr61TAivXz0TNaJeTUlFJFGVld6D1QeFVm")
		      .setOAuthAccessTokenSecret("RtoK8BBVgLvFjXYtsGD27WRgfahQ5y6GIbPc0e0yj8WsV");

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		twitter.updateStatus("Hello, Twitter!");
	}
}
