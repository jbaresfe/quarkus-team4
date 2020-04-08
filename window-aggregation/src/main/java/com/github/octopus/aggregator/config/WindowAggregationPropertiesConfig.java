package com.github.octopus.aggregator.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

/**
 * Properties configuration for the application
 * @author Eric Deandrea
 */
@ConfigProperties(prefix = "window-aggregation")
public interface WindowAggregationPropertiesConfig {
	/**
	 * Gets the topic name for the posts
	 * @return The topic name for the posts
	 */
	@ConfigProperty(name = "posts.topic-name")
	String postsTopicName();

	/**
	 * Gets the topic name for the counts
	 * @return The topic name for the counts
	 */
	@ConfigProperty(name = "counts.topic-name")
	String countsTopicName();

	/**
	 * Gets the store name for the posts
	 * @return The store name for the posts
	 */
	@ConfigProperty(name = "posts.store-name")
	String postsStoreName();

	/**
	 * Gets the time window duration. Defaults to 10 seconds
	 * @return The time window duration
	 */
	@ConfigProperty(name = "counts.time-window.duration.ms", defaultValue = "10000")
	long aggregationTimeWindowMillis();
}
