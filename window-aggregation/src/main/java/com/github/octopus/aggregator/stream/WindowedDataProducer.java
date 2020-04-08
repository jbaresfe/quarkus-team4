package com.github.octopus.aggregator.stream;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.octopus.aggregator.config.WindowAggregationPropertiesConfig;
import com.github.octopus.aggregator.model.Aggregation;
import com.github.octopus.aggregator.model.TwitterPost;
import io.quarkus.kafka.client.serialization.JsonbSerde;

/**
 * The main class for windowing the data and producing on another topic
 * @author Eric Deandrea
 */
@ApplicationScoped
public class WindowedDataProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WindowedDataProducer.class);
	private final WindowAggregationPropertiesConfig config;

	@Inject
	public WindowedDataProducer(WindowAggregationPropertiesConfig config) {
		this.config = config;
	}

	@Produces
	public Topology buildTopology() {
		LOGGER.debug("Starting streams");
		StreamsBuilder builder = new StreamsBuilder();
		JsonbSerde<TwitterPost> twitterPostSerde = new JsonbSerde<>(TwitterPost.class);
		JsonbSerde<Aggregation> aggregationSerde = new JsonbSerde<>(Aggregation.class);

		// This uses the Kafka Streams Tumbling time windowing following the Stateful transformations
		// https://kafka.apache.org/24/documentation/streams/developer-guide/dsl-api.html#stateful-transformations
		// As well as https://kafka.apache.org/24/documentation/streams/developer-guide/dsl-api.html#window-final-results
		builder
			.stream(this.config.postsTopicName(), Consumed.with(Serdes.String(), twitterPostSerde))
			.groupBy((key, post) -> post)
			.windowedBy(TimeWindows.of(Duration.ofMillis(this.config.aggregationTimeWindowMillis())))
			.aggregate(
				Aggregation::new,
				(post, value, aggregation) -> aggregation.updateFrom(value),
				Materialized.<TwitterPost, Aggregation, WindowStore<Bytes, byte[]>>as(this.config.postsStoreName()).withValueSerde(aggregationSerde)
			)
			.suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
			.toStream()
			.to(this.config.countsTopicName());

		return builder.build();
	}
}
