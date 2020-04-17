package org.acme.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TwitterPost {
	private String query;
	private String handle;
	private Instant timestamp;
	private String post;
	private final Set<String> hashtags = new HashSet<>();

	public TwitterPost() {

	}

	public TwitterPost(String query, String handle, Instant timestamp, String post, Collection<String> hashtags) {
		this.query = query;
		this.handle = handle;
		this.timestamp = timestamp;
		this.post = post;
		this.hashtags.addAll(Optional.ofNullable(hashtags).orElseGet(HashSet::new));
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public TwitterPost withQuery(String query) {
		setQuery(query);
		return this;
	}

	public String getHandle() {
		return this.handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public TwitterPost withHandle(String handle) {
		setHandle(handle);
		return this;
	}

	public Instant getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public TwitterPost withTimestamp(Instant timestamp) {
		setTimestamp(timestamp);
		return this;
	}

	public String getPost() {
		return this.post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public TwitterPost withPost(String post) {
		setPost(post);
		return this;
	}

	public Set<String> getHashtags() {
		return Collections.unmodifiableSet(this.hashtags);
	}

	public void setHashtags(Collection<String> hashtags) {
		this.hashtags.addAll(Optional.ofNullable(hashtags).orElseGet(HashSet::new));
	}

	public TwitterPost withHashtags(Collection<String> hashtags) {
		setHashtags(hashtags);
		return this;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", TwitterPost.class.getSimpleName() + "[", "]")
			.add("query='" + this.query + "'")
			.add("handle='" + this.handle + "'")
			.add("timestamp=" + this.timestamp)
			.add("post='" + this.post + "'")
			.add("hashtags=" + this.hashtags)
			.toString();
	}
}