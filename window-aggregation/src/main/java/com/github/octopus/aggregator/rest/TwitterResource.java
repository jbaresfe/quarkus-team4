
package com.github.octopus.aggregator.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit; 
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import com.github.octopus.aggregator.config.TwitterPropertiesConfig;
import com.github.octopus.aggregator.config.WindowAggregationPropertiesConfig;
import com.github.octopus.aggregator.model.TwitterPost;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.HashtagEntity;

@Path("/twitter")
public class TwitterResource {

	private static final Logger LOGGER = LoggerFactory.getLogger("TwitterResource");
	private final TwitterPropertiesConfig configtwitter;
	private final WindowAggregationPropertiesConfig config;
	private final Emitter<TwitterPost> postEmitter;

	@Inject
	public TwitterResource(WindowAggregationPropertiesConfig config, TwitterPropertiesConfig configtwitter, @Channel("twitter-posts-out") Emitter<TwitterPost> postEmitter) {
		this.config = config;
		this.postEmitter = postEmitter;
		this.configtwitter = configtwitter;
	}	

	public ConfigurationBuilder getConfigurationBuilder() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		return cb.setDebugEnabled(true).setOAuthConsumerKey(this.configtwitter.consumerKey())
				.setOAuthConsumerSecret(this.configtwitter.consumerSecret())
				.setOAuthAccessToken(this.configtwitter.accessToken())
				.setOAuthAccessTokenSecret(this.configtwitter.accessTokenSecret());
	}

	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TwitterPost> streamTwitter(@PathParam("query") String query) throws TwitterException, InterruptedException {
		List<TwitterPost> twitterPosts = new ArrayList<TwitterPost>();
		
		TwitterStream twitterStream = new TwitterStreamFactory(getConfigurationBuilder().build()).getInstance();
		//List<String> tweets = new ArrayList<String>();

		StatusListener statusListener = new StatusListener() {

			public void onStatus(Status status) {
				String tweetString = query + "@" + status.getUser().getScreenName() + ":" + status.getCreatedAt().toString() + status.getText() + status.getHashtagEntities();
				System.out.println(tweetString);
				//tweets.add(tweetString);
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
		//Thread.sleep(500);
		//twitterStream.shutdown();

		List<TwitterPost> posts = twitterPosts;
		posts.forEach(this.postEmitter::send);

		return posts;
	}
}