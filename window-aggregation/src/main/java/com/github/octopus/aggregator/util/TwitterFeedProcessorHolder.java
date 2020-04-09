package com.github.octopus.aggregator.util;

import java.util.concurrent.Future;

public final class TwitterFeedProcessorHolder {
	private final TwitterFeedProcessor twitterFeedProcessor;
	private final Future<?> processorFuture;

	public TwitterFeedProcessorHolder(TwitterFeedProcessor twitterFeedProcessor, Future<?> processorFuture) {
		this.twitterFeedProcessor = twitterFeedProcessor;
		this.processorFuture = processorFuture;
	}

	public TwitterFeedProcessor getTwitterFeedProcessor() {
		return this.twitterFeedProcessor;
	}

	public Future<?> getProcessorFuture() {
		return this.processorFuture;
	}
}
