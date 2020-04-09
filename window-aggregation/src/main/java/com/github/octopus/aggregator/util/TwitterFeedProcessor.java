package com.github.octopus.aggregator.util;

import com.github.octopus.aggregator.model.TwitterPost;

import org.jboss.resteasy.spi.RunnableWithException;

import io.reactivex.Emitter;

public class TwitterFeedProcessor implements Runnable {
    private final Emitter<TwitterPost> postEmitter;
    
    @Override
    public void run() {
		List<TwitterPost> twitterPosts = new ArrayList<TwitterPost>();

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
		Thread.sleep(30000);
		twitterStream.shutdown();

		List<TwitterPost> posts = twitterPosts;
		posts.forEach(this.postEmitter::send);

		LOGGER.info("After posting into Kafka");

		return posts;
	}

    public TwitterFeedProcessor(Emitter<TwitterPost> postEmitter, TwitterStream twitterStream, String query)
    {
        this.postEmitter = postEmitter;
        this.twitterStream = twitterStream;
        this.query = query;
    }
}