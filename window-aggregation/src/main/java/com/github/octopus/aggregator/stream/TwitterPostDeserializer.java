package com.github.octopus.aggregator.stream;

import com.github.octopus.aggregator.model.TwitterPost;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class TwitterPostDeserializer extends JsonbDeserializer<TwitterPost> {
	public TwitterPostDeserializer() {
		super(TwitterPost.class);
	}
}
