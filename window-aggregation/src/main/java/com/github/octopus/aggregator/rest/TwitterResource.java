
package com.github.octopus.aggregator.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.octopus.aggregator.config.TwitterPropertiesConfig;
import com.github.octopus.aggregator.config.WindowAggregationPropertiesConfig;
import com.github.octopus.aggregator.model.TwitterPost;
import com.github.octopus.aggregator.util.TwitterFeedProcessor;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Path("/twitter")
public class TwitterResource {
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
	private static final Logger LOGGER = LoggerFactory.getLogger("TwitterResource");
	private final TwitterPropertiesConfig configtwitter;
	private final WindowAggregationPropertiesConfig config;
	private final Emitter<TwitterPost> postEmitter;
	private final Twitter twitter;

	@Inject
	public TwitterResource(WindowAggregationPropertiesConfig config, TwitterPropertiesConfig configtwitter, @Channel("twitter-posts-out") Emitter<TwitterPost> postEmitter) {
		this.config = config;
		this.postEmitter = postEmitter;
		this.configtwitter = configtwitter;
		this.twitter = new TwitterFactory(getConfigurationBuilder().build()).getInstance();
	}

	private ConfigurationBuilder getConfigurationBuilder() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		return cb.setDebugEnabled(true).setOAuthConsumerKey(this.configtwitter.consumerKey())
			.setOAuthConsumerSecret(this.configtwitter.consumerSecret())
			.setOAuthAccessToken(this.configtwitter.accessToken())
			.setOAuthAccessTokenSecret(this.configtwitter.accessTokenSecret());
	}

	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response streamTwitter(@PathParam("query") String query) {
		EXECUTOR_SERVICE.submit(new TwitterFeedProcessor(this.postEmitter, this.twitter, query));
		return Response.noContent().build();
	}
}
