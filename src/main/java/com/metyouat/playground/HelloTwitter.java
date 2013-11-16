package com.metyouat.playground;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class HelloTwitter {
	public static void main(String[] args) throws TwitterException {
		Twitter twitter = Configurator.getTwitter();

		twitter.updateStatus("Testing...");
	}
}
