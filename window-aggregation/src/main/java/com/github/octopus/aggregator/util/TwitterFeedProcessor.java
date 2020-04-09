package com.github.octopus.aggregator.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.octopus.aggregator.config.TwitterPropertiesConfig;
import com.github.octopus.aggregator.model.TwitterPost;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterFeedProcessor implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterFeedProcessor.class);
	private final TwitterPropertiesConfig configtwitter;
	private final Emitter<TwitterPost> postEmitter;
	private final TwitterStream twitterStream;
	private final String query;
	private Instant startTime;

	public TwitterFeedProcessor(Emitter<TwitterPost> postEmitter, TwitterPropertiesConfig configtwitter, String query)
	{
		this.postEmitter = postEmitter;
		this.query = query;
		this.configtwitter = configtwitter;
		this.twitterStream = new TwitterStreamFactory(getConfigurationBuilder().build()).getInstance();
	}

	private ConfigurationBuilder getConfigurationBuilder() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		return cb.setDebugEnabled(true).setOAuthConsumerKey(this.configtwitter.consumerKey())
			.setOAuthConsumerSecret(this.configtwitter.consumerSecret())
			.setOAuthAccessToken(this.configtwitter.accessToken())
			.setOAuthAccessTokenSecret(this.configtwitter.accessTokenSecret());
	}

	public void stop() {
		this.twitterStream.clearListeners();
	}

	@Override
	public void run() {
		this.startTime = Instant.now();
		StatusListener statusListener = new StatusAdapter() {
			@Override
			public void onStatus(Status status) {
				String tweetString = TwitterFeedProcessor.this.query + "@" + status.getUser().getScreenName() + ":" + status.getCreatedAt().toString() + status.getText() + status.getHashtagEntities();
				TwitterFeedProcessor.LOGGER.info("tweet string = {}", tweetString);
				String handle = String.format("@%s", status.getUser().getScreenName());
				Instant timestamp = status.getCreatedAt().toInstant();
				String post = status.getText();
				HashtagEntity[] tags = status.getHashtagEntities();

				List<String> hashtagList = new ArrayList<>(
					Arrays.stream(tags)
						.map(HashtagEntity::getText)
						.collect(Collectors.toList())
				);

				TwitterPost twitterPost = new TwitterPost(TwitterFeedProcessor.this.query, handle, timestamp, post, hashtagList);
				TwitterFeedProcessor.this.postEmitter.send(twitterPost);
				TwitterFeedProcessor.LOGGER.info("Sent post to kafka: {}", twitterPost);
			}
		};

		this.twitterStream.addListener(statusListener);
		this.twitterStream.filter(new FilterQuery(String.format("#%s", this.query)));
//		Thread.sleep(30000);
	}
}
