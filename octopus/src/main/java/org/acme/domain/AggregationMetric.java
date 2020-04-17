package org.acme.domain;

import java.util.Objects;
import java.util.StringJoiner;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AggregationMetric {
	private String hashtag;
	private long count = 0;

	public AggregationMetric(String hashtag, long count) {
		this.hashtag = hashtag;
		this.count = count;
	}

	public AggregationMetric(String hashtag) {
		this(hashtag, 1);
	}

	public AggregationMetric() {

	}

	public String getHashtag() {
		return this.hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public AggregationMetric withHashtag(String hashTag) {
		setHashtag(hashTag);
		return this;
	}

	public long getCount() {
		return this.count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void incrementCount() {
		this.count++;
	}

	public AggregationMetric withCount(long count) {
		setCount(count);
		return this;
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

	@Override
	public String toString() {
		return new StringJoiner(", ", AggregationMetric.class.getSimpleName() + "[", "]")
			.add("hashtag='" + this.hashtag + "'")
			.add("count=" + this.count)
			.toString();
	}
}
