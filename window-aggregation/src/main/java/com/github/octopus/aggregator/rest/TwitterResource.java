
package com.github.octopus.aggregator.rest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.event.Observes;
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
import com.github.octopus.aggregator.model.TwitterPost;
import com.github.octopus.aggregator.util.TwitterFeedProcessor;
import io.quarkus.runtime.ShutdownEvent;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Path("/twitter")
public class TwitterResource {
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
	private static final Logger LOGGER = LoggerFactory.getLogger("TwitterResource");
	private final TwitterPropertiesConfig twitterConfig;
	private final Emitter<TwitterPost> postEmitter;
	private final Twitter twitter;

	@Inject
	public TwitterResource(TwitterPropertiesConfig twitterConfig, @Channel("twitter-posts-out") Emitter<TwitterPost> postEmitter) {
		this.postEmitter = postEmitter;
		this.twitterConfig = twitterConfig;
		this.twitter = new TwitterFactory(getConfigurationBuilder().build()).getInstance();
	}

	private ConfigurationBuilder getConfigurationBuilder() {
		return new ConfigurationBuilder()
			.setDebugEnabled(true)
			.setOAuthConsumerKey(this.twitterConfig.consumerKey())
			.setOAuthConsumerSecret(this.twitterConfig.consumerSecret())
			.setOAuthAccessToken(this.twitterConfig.accessToken())
			.setOAuthAccessTokenSecret(this.twitterConfig.accessTokenSecret());
	}

	public void onStop(@Observes ShutdownEvent shutdownEvent) {
		EXECUTOR_SERVICE.shutdown();
	}

	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response streamTwitter(@PathParam("query") String query) {
		LOGGER.info("Starting processing for query: {}", query);
		// Throw the processor on a background thread so we can return immediately

		EXECUTOR_SERVICE.submit(new TwitterFeedProcessor(this.postEmitter, this.twitter, query));
		return Response.noContent().build();
	}
}
