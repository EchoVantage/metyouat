package com.metyouat.playground;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DirectMessage {
	public static void main(String[] args) throws TwitterException {
		Twitter twitter = Configurator.getTwitter();

		twitter.sendDirectMessage("mikedeck", "This is a direct message sent from the API");
	}
}
