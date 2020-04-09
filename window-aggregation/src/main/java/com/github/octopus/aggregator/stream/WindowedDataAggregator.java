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
 * The main class for windowing the data and producing on another topic
 * @author Eric Deandrea
 */
@ApplicationScoped
public class WindowedDataAggregator {
	private static final Logger LOGGER = LoggerFactory.getLogger(WindowedDataAggregator.class);
	private final WindowAggregationPropertiesConfig config;
	private final Emitter<Aggregation> aggregationEmitter;
	private final List<TwitterPost> posts = new ArrayList<>();
	private final FunctionalReadWriteLockGuard lockGuard = new FunctionalReadWriteLockGuard(new ReentrantReadWriteLock(true));

	@Inject
	public WindowedDataAggregator(WindowAggregationPropertiesConfig config, @Channel("hashtag-counts") Emitter<Aggregation> aggregationEmitter) {
		this.config = config;
		LOGGER.info("Created {}", getClass().getName());
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

//	@Produces
//	public Topology buildTopology() {
//		LOGGER.debug("Starting streams");
//		StreamsBuilder builder = new StreamsBuilder();
//		JsonbSerde<TwitterPost> twitterPostSerde = new JsonbSerde<>(TwitterPost.class);
//		JsonbSerde<Aggregation> aggregationSerde = new JsonbSerde<>(Aggregation.class);
//
//		// This uses the Kafka Streams Tumbling time windowing following the Stateful transformations
//		// https://kafka.apache.org/24/documentation/streams/developer-guide/dsl-api.html#stateful-transformations
//		// As well as https://kafka.apache.org/24/documentation/streams/developer-guide/dsl-api.html#window-final-results
//
//		LOGGER.info("Starting streams: FROM: {}, TO: {}, WINDOW DURATION: {}ms", this.config.postsTopicName(), this.config.countsTopicName(), this.config.aggregationTimeWindowMillis());
//
//		builder
//			.stream(this.config.postsTopicName(), Consumed.with(Serdes.String(), twitterPostSerde))
//			.groupBy((key, post) -> post)
//			.windowedBy(TimeWindows.of(Duration.ofMillis(this.config.aggregationTimeWindowMillis())))
//			.aggregate(
//				Aggregation::new,
//				(post, value, aggregation) -> aggregation.updateFrom(value),
//				Materialized.<TwitterPost, Aggregation, WindowStore<Bytes, byte[]>>as(this.config.postsStoreName()).withValueSerde(aggregationSerde)
//			)
//			.suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
//			.toStream()
//			.to(this.config.countsTopicName());
//
//		return builder.build();
//	}
}
