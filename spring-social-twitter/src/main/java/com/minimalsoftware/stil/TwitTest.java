package com.minimalsoftware.stil;
import java.util.List;

import org.springframework.social.*;
import org.springframework.social.twitter.*;




public class TwitTest {

	TwitterTemplate twitter;
	TwitterProfile profile;

	private String apiKey = "z6NXVM0AlA3UqjvqN9PSHg";
	private String apiSecret = "zAE9dzzYVlsYfBZkcXx5CRFzBRZ3ZykG8ga6f2j56DQ";
	private String accessToken = "14330569-67VHYybF2v3RxvxnuOaU13H5ZvKbTlCP0xzXchHXo";
	private String accessTokenSecret = "4fnZgKIM7pz4MI3wwEqkMM5M9quAok8BRfDFZkkJTk";

	private String screenName;

	public void init()
	{
		twitter = new TwitterTemplate(apiKey, apiSecret, accessToken, accessTokenSecret);
		profile = twitter.getUserProfile("topchimp"); //could be empty
		screenName = profile.getScreenName();
		printFriends(screenName);
		printFavorites();


	}

	public void printFriends(String screenName)
	{
		List<String> friends = twitter.getFriends(screenName);
        for (String myFriends : friends) {
            System.out.println(myFriends);
        }
	}


	public void printFavorites()
	{
		List<Tweet> tweets = twitter.getFavoriteTimeline();
        for (Tweet myTweets : tweets) {
            System.out.println(myTweets.getText());
        }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TwitTest t = new TwitTest();
		t.init();

	}

}
