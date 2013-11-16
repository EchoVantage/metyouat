package com.metyouat.playground;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.TwitterException;

public class GetImage {
	private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^(.+)_[a-z]+(\\.[^.]+)$");

	public static void main(String[] args) throws TwitterException {
		String imageUrl = new Configurator().getTwitter().showUser("fuwjax").getProfileImageURL();
		Matcher m = IMAGE_URL_PATTERN.matcher(imageUrl);
		if(!m.matches()) {
			throw new RuntimeException("The URL did not match the expected pattern");
		}

		System.out.println(m.group(1) + m.group(2));
	}
}
