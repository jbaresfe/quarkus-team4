package com.github.octopus.aggregator.rest;

import java.time.Instant;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.github.octopus.aggregator.config.WindowAggregationPropertiesConfig;
import com.github.octopus.aggregator.model.TwitterPost;

@Path("/dummy-data")
public class DummyDataProducerResource {
	private final WindowAggregationPropertiesConfig config;
	private final Emitter<TwitterPost> postEmitter;

	@Inject
	public DummyDataProducerResource(WindowAggregationPropertiesConfig config, @Channel("twitter-posts-out") Emitter<TwitterPost> postEmitter) {
		this.config = config;
		this.postEmitter = postEmitter;
	}

	@GET
	@Path("/load")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TwitterPost> createData() {
		List<TwitterPost> posts = generateData();
		posts.forEach(this.postEmitter::send);

		return posts;
	}

	private static List<TwitterPost> generateData() {
		return List.of(
			new TwitterPost("user1", "edeandrea", Instant.now(), "Man I really hope the #sentimentanalyzer works", List.of("sentimentanalyzer")),
			new TwitterPost("user2", "realDonaldTrump", Instant.now(), "The #economy is doing #tremendous!", List.of("economy", "tremendous"))
		);
	}
}
