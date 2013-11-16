package com.metyouat.playground;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Configurator {
	private static final ConfigurationBuilder configBuilder = new ConfigurationBuilder()
	      .setDebugEnabled(true)
	      .setOAuthConsumerKey("G8zZOeZpML3R55IcXq2Z8g")
	      .setOAuthConsumerSecret("5P7ALzTr0MdrvhqCN815rdD31OSnrxDDhZeltrd7Duo")
	      .setOAuthAccessToken("2197032072-Rbo2CNr61TAivXz0TNaJeTUlFJFGVld6D1QeFVm")
	      .setOAuthAccessTokenSecret("RtoK8BBVgLvFjXYtsGD27WRgfahQ5y6GIbPc0e0yj8WsV");

	private static final TwitterFactory factory = new TwitterFactory(configBuilder.build());

	public static Twitter getTwitter() {
		return factory.getInstance();
	}
}
