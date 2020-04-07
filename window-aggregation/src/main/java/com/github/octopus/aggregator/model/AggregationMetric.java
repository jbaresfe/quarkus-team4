package com.github.octopus.aggregator.model;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AggregationMetric {
	private String hashtag;
	private long count;

	public AggregationMetric(String hashtag, long count) {
		this.hashtag = hashtag;
		this.count = count;
	}

	public AggregationMetric() {

	}

	public String getHashtag() {
		return this.hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public long getCount() {
		return this.count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AggregationMetric that = (AggregationMetric) o;
		return this.hashtag.equals(that.hashtag);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.hashtag);
	}
}
