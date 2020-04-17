package org.acme.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Aggregation {
	private Instant startWindow = Instant.now();
	private Instant endWindow;
	private List<AggregationMetric> metrics = new ArrayList<>();

	public Instant getStartWindow() {
		return this.startWindow;
	}

	public void setStartWindow(Instant startWindow) {
		this.startWindow = startWindow;
	}

	public Aggregation withStartWindow(Instant startWindow) {
		setStartWindow(startWindow);
		return this;
	}

	public Instant getEndWindow() {
		return this.endWindow;
	}

	public void setEndWindow(Instant endWindow) {
		this.endWindow = endWindow;
	}

	public Aggregation withEndWindow(Instant endWindow) {
		setEndWindow(endWindow);
		return this;
	}

	public List<AggregationMetric> getMetrics() {
		return Collections.unmodifiableList(this.metrics);
	}

	public void setMetrics(List<AggregationMetric> metrics) {
		this.metrics.addAll(Optional.ofNullable(metrics).orElseGet(ArrayList::new));
	}

	public Aggregation withMetrics(List<AggregationMetric> metrics) {
		setMetrics(metrics);
		return this;
	}

	public Aggregation updateFrom(TwitterPost post) {
		this.endWindow = Instant.now();

		// First go through all the hashtags which have already been recorded and increment the count
		Collection<String> postHashtags = post.getHashtags();
		Set<String> currentHashtags = this.metrics.stream()
			.map(AggregationMetric::getHashtag)
			.collect(Collectors.toSet());

		this.metrics.stream()
			.filter(metric -> postHashtags.contains(metric.getHashtag()))
			.forEach(AggregationMetric::incrementCount);

		// Then go through all the hashtags which haven't yet been recorded
		this.metrics.addAll(
			postHashtags.stream()
				.filter(hashtag -> !currentHashtags.contains(hashtag))
				.map(AggregationMetric::new)
				.collect(Collectors.toList())
		);

		return this;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Aggregation.class.getSimpleName() + "[", "]")
			.add("startWindow=" + this.startWindow)
			.add("endWindow=" + this.endWindow)
			.add("metrics=" + this.metrics)
			.toString();
	}
}
