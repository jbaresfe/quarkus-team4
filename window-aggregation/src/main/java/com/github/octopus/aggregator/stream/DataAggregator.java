package com.github.octopus.aggregator.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.octopus.aggregator.config.WindowAggregationPropertiesConfig;
import com.github.octopus.aggregator.model.Aggregation;
import com.github.octopus.aggregator.model.TwitterPost;
import com.github.octopus.aggregator.util.FunctionalReadWriteLockGuard;
import io.quarkus.scheduler.Scheduled;

/**
 * Class responsible for doing time-based data windowing and aggregation
 * @author Eric Deandrea
 */
@ApplicationScoped
public class DataAggregator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataAggregator.class);
	private final List<TwitterPost> posts = new ArrayList<>();
	private final FunctionalReadWriteLockGuard lockGuard = new FunctionalReadWriteLockGuard(new ReentrantReadWriteLock(true));
	private final WindowAggregationPropertiesConfig config;
	private final Emitter<Aggregation> aggregationEmitter;

	@Inject
	public DataAggregator(WindowAggregationPropertiesConfig config, @Channel("hashtag-counts") Emitter<Aggregation> aggregationEmitter) {
		this.config = config;
		this.aggregationEmitter = aggregationEmitter;
	}

	/**
	 * Records incoming posts
	 * @param post The incoming {@link TwitterPost}
	 */
	@Incoming("twitter-posts-in")
	public void recordPost(TwitterPost post) {
		LOGGER.info("Got incoming post: {}", post);
		this.lockGuard.doInWriteLock(() -> this.posts.add(post));
	}

	/**
	 * Processes an aggregation. Runs every 10 seconds.
	 */
	@Scheduled(every = "10s")
	public void processAggregation() {
		LOGGER.info("Processing aggregation from last {}ms", this.config.aggregationTimeWindowMillis());
		List<TwitterPost> postsToProcess = this.lockGuard.doInWriteLock(() -> {
			List<TwitterPost> allPosts = List.copyOf(this.posts);
			this.posts.clear();

			return allPosts;
		});

		if (!postsToProcess.isEmpty()) {
			Aggregation aggregation = new Aggregation();
			postsToProcess.forEach(aggregation::updateFrom);

			LOGGER.info("Aggregation to post: {}", aggregation);
			this.aggregationEmitter.send(aggregation);
		}
	}
}
