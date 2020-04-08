package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

import org.acme.domain.TwitterSentiment;
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
import org.acme.domain.TwitterPost;

@Path("/kafka")
public class TwitterKafkaResource extends TwitterPost {

	private static final Logger LOGGER = LoggerFactory.getLogger("TwitterResource");
	private final OctopusTwitterPropertiesConfig config;

	@Inject
	public TwitterKafkaResource(OctopusTwitterPropertiesConfig config) {
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
	@Path("/{query}")
	@Produces(MediaType.TEXT_PLAIN)
	public List<TwitterPost> streamTwitter(@PathParam("query") String query)
			throws TwitterException, InterruptedException {
		List<TwitterPost> twitterPosts = new ArrayList<TwitterPost>();

		TwitterStream twitterStream = new TwitterStreamFactory(getConfigurationBuilder().build()).getInstance();
		// List<String> tweets = new ArrayList<String>();

		StatusListener statusListener = new StatusListener() {

			public void onStatus(Status status) {
				// String tweetString = query + "@" + status.getUser().getScreenName() + ":" +
				// status.getCreatedAt().toString() + status.getText() +
				// status.getHashtagEntities();
				// System.out.println(tweetString);
				// tweets.add(tweetString);
				String handle = "@" + status.getUser().getScreenName();
				Instant timestamp = status.getCreatedAt().toInstant();
				String post = status.getText().toString();
				List<String> hashtagList = new ArrayList<String>();
				HashtagEntity[] tags = status.getHashtagEntities();

				for (HashtagEntity tag : tags) {
					String hash = tag.getText();
					hashtagList.add(hash);
				}

				TwitterPost somePost = new TwitterPost(query, handle, timestamp, post, hashtagList);

				twitterPosts.add(somePost);

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
		twitterStream.filter(filter);
		Thread.sleep(500);
		twitterStream.shutdown();
		return twitterPosts;

	}
}