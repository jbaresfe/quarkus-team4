package com.github.octopus.aggregator.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Aggregation {
	private Instant startWindow = Instant.now();
	private Instant endWindow;
	private final List<AggregationMetric> metrics = new ArrayList<>();

	public Aggregation(Instant startWindow, Instant endWindow, List<AggregationMetric> metrics) {
		this.startWindow = startWindow;
		this.endWindow = endWindow;
		this.metrics.addAll(Optional.ofNullable(metrics).orElseGet(ArrayList::new));
	}

	public Aggregation() {

	}

	public Instant getStartWindow() {
		return this.startWindow;
	}

	public void setStartWindow(Instant startWindow) {
		this.startWindow = startWindow;
	}

	public Instant getEndWindow() {
		return this.endWindow;
	}

	public void setEndWindow(Instant endWindow) {
		this.endWindow = endWindow;
	}

	public List<AggregationMetric> getMetrics() {
		return Collections.unmodifiableList(this.metrics);
	}

	public void setMetrics(List<AggregationMetric> metrics) {
		this.metrics.addAll(Optional.ofNullable(metrics).orElseGet(ArrayList::new));
	}

	public Aggregation updateFrom(TwitterPost post) {
		this.endWindow = Instant.now();
		return this;
	}
}
