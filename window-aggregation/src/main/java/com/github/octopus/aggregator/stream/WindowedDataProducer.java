package com.github.octopus.aggregator.stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

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
	private final WindowAggregationPropertiesConfig config;

	@Inject
	public WindowedDataProducer(WindowAggregationPropertiesConfig config) {
		this.config = config;
	}

	@Produces
	public Topology buildTopology() {
		StreamsBuilder builder = new StreamsBuilder();
		JsonbSerde<TwitterPost> twitterPostSerde = new JsonbSerde<>(TwitterPost.class);
		JsonbSerde<Aggregation> aggregationSerde = new JsonbSerde<>(Aggregation.class);
		KeyValueBytesStoreSupplier twitterPostSupplier = Stores.persistentKeyValueStore(this.config.postsStoreName());

		builder
			.stream(this.config.postsTopicName(), Consumed.with(Serdes.String(), twitterPostSerde))
			.groupByKey()
			.aggregate(
				Aggregation::new,
				(postString, post, aggregation) -> aggregation.updateFrom(post),
				Materialized.<String, Aggregation>as(twitterPostSupplier)
					.withKeySerde(Serdes.String())
					.withValueSerde(aggregationSerde)
			)
			.toStream()
			.to(this.config.countsTopicName(), Produced.with(Serdes.String(), aggregationSerde));

		return builder.build();
	}
}
