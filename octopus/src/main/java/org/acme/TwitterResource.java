
package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

import org.acme.domain.TwitterPost;
import org.acme.domain.TwitterSentiment;
import org.acme.domain.TwitterSentimentRevised;
import org.acme.domain.TwitterSentimentRevised2;
import org.acme.SentimentAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acme.config.OctopusTwitterPropertiesConfig;
import javax.inject.Inject;

import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Path("/twitter")
public class TwitterResource {

	private static final Logger LOGGER = LoggerFactory.getLogger("TwitterResource");
	private final OctopusTwitterPropertiesConfig config;


	@Inject
	public TwitterResource(OctopusTwitterPropertiesConfig config) {
		this.config = config;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String hello() {
		return "hello world";
	}

	public ConfigurationBuilder getConfigurationBuilder() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		return cb.setDebugEnabled(true).setOAuthConsumerKey(this.config.twitterapiOauthConsumerKey())
				.setOAuthConsumerSecret(this.config.twitterapiOauthConsumerSecret())
				.setOAuthAccessToken(this.config.twitterapiOauthAccessToken())
				.setOAuthAccessTokenSecret(this.config.twitterapiOauthAccessTokenSecret());
	}

	@GET
	@Path("stream/{query}")
	@Produces(MediaType.TEXT_PLAIN)
	public List<String> streamTwitter(@PathParam("query") String query) throws TwitterException, InterruptedException {

		TwitterStream twitterStream = new TwitterStreamFactory(getConfigurationBuilder().build()).getInstance();
		List<String> tweets = new ArrayList<String>();
		List<TwitterPost> twitterPosts = new ArrayList<TwitterPost>();


		StatusListener statusListener = new StatusListener() {

			public void onStatus(Status status) {
				String tweetString = "@" + status.getUser().getScreenName() + ":" + status.getText();
				//System.out.println(status.getUser().getScreenName());
				tweets.add(tweetString);
				String handle = "@" + status.getUser().getScreenName();
				Instant timestamp = status.getCreatedAt().toInstant();
				String post = status.getText().toString();
				List<String> hashtagList = new ArrayList<String>();
				HashtagEntity[] tags = status.getHashtagEntities();
				for(HashtagEntity tag : tags) {
					hashtagList.add(tag.getText());
				}

				TwitterPost somePost = new TwitterPost(query, handle, timestamp, post, hashtagList);
				twitterPosts.add(somePost);
				
				TwitterSentimentRevised2 q =  SentimentAnalysis.aggregateSentimentAnalyzer2(query, twitterPosts);
				
				System.out.println("Twitter Post: " + status.getText());
				System.out.println(q.toString());
				

				String sentiment = SentimentAnalysis.calculateSentiment(status.getText().toString());
				//TwitterSentimentRevised someSentiment = new TwitterSentimentRevised(query, status.getText(),sentiment);    
				//System.out.println(someSentiment.toString());
			}

			public void onException(Exception ex) {
				// TODO Auto-generated method stub

			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// TODO Auto-generated method stub

			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// TODO Auto-generated method stub

			}

			public void onScrubGeo(long userId, long upToStatusId) {
				// TODO Auto-generated method stub

			}

			public void onStallWarning(StallWarning warning) {
				// TODO Auto-generated method stub

			}
		};

		FilterQuery filter = new FilterQuery();
		String hashtag = "#" + query;
		String keywords[] = { hashtag };
		filter.track(keywords);

		twitterStream.addListener(statusListener);
		twitterStream.filter( filter);
		return tweets;

	}
	
	@GET
	@Path("kafka/{query}")
	@Produces(MediaType.TEXT_PLAIN)
	public List<TwitterPost> streamTwitter2(@PathParam("query") String query)
			throws TwitterException, InterruptedException {
		List<TwitterPost> twitterPosts = new ArrayList<TwitterPost>();

		TwitterStream twitterStream = new TwitterStreamFactory(getConfigurationBuilder().build()).getInstance();
		// List<String> tweets = new ArrayList<String>();

		StatusListener statusListener = new StatusListener() {

			public void onStatus(Status status) {
				 String tweetString = query + "@" + status.getUser().getScreenName() + ":" +
						 status.getCreatedAt().toString() + status.getText() +
						 status.getHashtagEntities();
				 System.out.println(tweetString);
				// tweets.add(tweetString);
				String handle = "@" + status.getUser().getScreenName();
				Instant timestamp = status.getCreatedAt().toInstant();
				String post = status.getText().toString();
				List<String> hashtagList = new ArrayList<String>();
				HashtagEntity[] tags = status.getHashtagEntities();
				for(HashtagEntity tag: tags) {
					hashtagList.add(tag.getText());
				}

				TwitterPost somePost = new TwitterPost(query, handle, timestamp, post, hashtagList);

				twitterPosts.add(somePost);
				
				//TwitterSentiment someSentiment = SentimentAnalysis.aggregateSentimentAnalyzer(query, twitterPosts);
				//System.out.println(someSentiment.toString());

			}

			public void onException(Exception ex) {
				// TODO Auto-generated method stub

			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// TODO Auto-generated method stub

			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// TODO Auto-generated method stub

			}

			public void onScrubGeo(long userId, long upToStatusId) {
				// TODO Auto-generated method stub

			}

			public void onStallWarning(StallWarning warning) {
				// TODO Auto-generated method stub
				
			}
		};

		FilterQuery filter = new FilterQuery();
		String hashtag = "#" + query;
		String keywords[] = { hashtag };
		filter.language("english");
		filter.track(keywords);
		twitterStream.addListener(statusListener);
		twitterStream.filter(filter);
		return twitterPosts;

	}
}