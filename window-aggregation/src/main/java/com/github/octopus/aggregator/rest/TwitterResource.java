
package com.github.octopus.aggregator.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
import com.github.octopus.aggregator.util.FunctionalReadWriteLockGuard;
import com.github.octopus.aggregator.util.TwitterFeedProcessor;
import com.github.octopus.aggregator.util.TwitterFeedProcessorHolder;
import io.quarkus.scheduler.Scheduled;

@Path("/twitter")
public class TwitterResource {
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
	private static final Logger LOGGER = LoggerFactory.getLogger("TwitterResource");
	private final TwitterPropertiesConfig configtwitter;
	private final WindowAggregationPropertiesConfig config;
	private final Emitter<TwitterPost> postEmitter;
	private final List<TwitterFeedProcessorHolder> runningProcessors = new ArrayList<>();
	private final FunctionalReadWriteLockGuard lockGuard = new FunctionalReadWriteLockGuard(new ReentrantReadWriteLock(true));

	@Inject
	public TwitterResource(WindowAggregationPropertiesConfig config, TwitterPropertiesConfig configtwitter, @Channel("twitter-posts-out") Emitter<TwitterPost> postEmitter) {
		this.config = config;
		this.postEmitter = postEmitter;
		this.configtwitter = configtwitter;
	}

	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response streamTwitter(@PathParam("query") String query) {
		TwitterFeedProcessor twitterFeedProcessor = new TwitterFeedProcessor(this.postEmitter, this.configtwitter, query);
		this.lockGuard.doInWriteLock(() -> this.runningProcessors.add(new TwitterFeedProcessorHolder(twitterFeedProcessor, EXECUTOR_SERVICE.submit(twitterFeedProcessor))));

		return Response.noContent().build();
	}

	@Scheduled(every = "1m")
	public void stopThreads() {
		this.lockGuard.doInWriteLock(() -> {
			this.runningProcessors.forEach(processor -> {
				processor.getTwitterFeedProcessor().stop();
				processor.getProcessorFuture().cancel(false);
			});

			this.runningProcessors.clear();
		});
	}
}
