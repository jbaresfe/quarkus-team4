package com.github.octopus.aggregator.stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.octopus.aggregator.config.WindowAggregationPropertiesConfig;

/**
 * The main class for windowing the data and producing on another topic
 * @author Eric Deandrea
 */
//@ApplicationScoped
public class WindowedDataProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WindowedDataProducer.class);
	private final WindowAggregationPropertiesConfig config;

	@Inject
	public WindowedDataProducer(WindowAggregationPropertiesConfig config) {
		this.config = config;
		LOGGER.info("Created {}", getClass().getName());
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
