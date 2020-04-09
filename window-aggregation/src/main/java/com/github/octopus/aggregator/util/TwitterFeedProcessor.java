package com.github.octopus.aggregator.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.octopus.aggregator.model.TwitterPost;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public final class TwitterFeedProcessor implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterFeedProcessor.class);
	private final Emitter<TwitterPost> postEmitter;
	private final Twitter twitter;
	private final String query;

	public TwitterFeedProcessor(Emitter<TwitterPost> postEmitter, Twitter twitter, String query) {
		this.postEmitter = postEmitter;
		this.twitter = twitter;
		this.query = query;
	}

	@Override
	public void run() {
		try {
			this.twitter.search(new Query(String.format("#%s", this.query)))
				.getTweets()
				.forEach(this::processTweet);
		}
		catch (TwitterException ex) {
			LOGGER.error("Error getting tweets: {}", ex.getMessage(), ex);
		}
	}

	private void processTweet(Status tweet) {
		// Process the tweet
		String tweetString = this.query + "@" + tweet.getUser().getScreenName() + ":" + tweet.getCreatedAt().toString() + tweet.getText() + tweet.getHashtagEntities();
		TwitterFeedProcessor.LOGGER.info("tweet string = {}", tweetString);
		String handle = String.format("@%s", tweet.getUser().getScreenName());
		Instant timestamp = tweet.getCreatedAt().toInstant();
		String post = tweet.getText();
		HashtagEntity[] tags = tweet.getHashtagEntities();

		List<String> hashtagList = new ArrayList<>(
			Arrays.stream(tags)
				.map(HashtagEntity::getText)
				.collect(Collectors.toList())
		);

		// Create our POJO that will be sent to Kafka
		TwitterPost twitterPost = new TwitterPost(this.query, handle, timestamp, post, hashtagList);

		// Send to Kafka
		this.postEmitter.send(twitterPost);
		TwitterFeedProcessor.LOGGER.info("Sent post to kafka: {}", twitterPost);
	}
}
