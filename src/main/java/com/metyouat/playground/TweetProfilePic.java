package com.metyouat.playground;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.common.io.ByteStreams;

public class TweetProfilePic {
	private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^(.+)_[a-z]+(\\.[^.]+)$");

	public static void main(String[] args) throws TwitterException, IOException {
		String user = "mikedeck";

		Twitter twitter = new BotMain().getTwitter();

		String imgUrlString = twitter.showUser(user).getOriginalProfileImageURL();
		Matcher m = IMAGE_URL_PATTERN.matcher(imgUrlString);
		if(!m.matches()) {
			throw new RuntimeException("The URL did not match the expected pattern");
		}
		URL imgUrl = new URL(m.group(1) + m.group(2));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(InputStream in = imgUrl.openStream()) {
			ByteStreams.copy(in, out);
		}

		byte[] imageBytes = out.toByteArray();
		StatusUpdate status = new StatusUpdate("@" + user + " this is your profile pic.");
		status.media(user + "'s Profile Pic", new ByteArrayInputStream(imageBytes));
		twitter.updateStatus(status);
	}
}
