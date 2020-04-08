package com.github.octopus.aggregator.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AggregationTests {
	private static final Instant NOW = Instant.now();

	@ParameterizedTest(name = "{index} ==> Aggregation.updateFromWorks for arguments {argumentsWithNames}")
	@MethodSource("params")
	public void updateFromWorks(Aggregation before, TwitterPost postToAdd, Aggregation expected) {
		Aggregation after = before.updateFrom(postToAdd);

		assertThat(after)
			.isNotNull();

		List<AggregationMetric> actualMetrics = after.getMetrics();
		Map<String, Long> expectedMetricsSizes = expected.getMetrics()
			.stream()
			.collect(Collectors.toMap(AggregationMetric::getHashtag, AggregationMetric::getCount));

		assertThat(actualMetrics)
			.hasSize(expectedMetricsSizes.size());

		actualMetrics.stream().forEach(metric -> assertThat(metric.getCount()).isEqualTo(expectedMetricsSizes.get(metric.getHashtag())));
	}

	static Stream<Arguments> params() {
		return Stream.of(
			Arguments.of(
				new Aggregation(),
				new TwitterPost("query", "handle", NOW, "post", List.of("tag1")),
				new Aggregation().withMetrics(List.of(new AggregationMetric("tag1")))
			),
			Arguments.of(
				new Aggregation().withMetrics(List.of(new AggregationMetric("tag1"))),
				new TwitterPost("query", "handle", NOW, "post", List.of("tag1")),
				new Aggregation().withMetrics(List.of(new AggregationMetric("tag1", 2)))
			),
			Arguments.of(
				new Aggregation(),
				new TwitterPost("query", "handle", NOW, "post", List.of("tag1", "tag2")),
				new Aggregation().withMetrics(List.of(new AggregationMetric("tag1"), new AggregationMetric("tag2")))
			),
			Arguments.of(
				new Aggregation().withMetrics(List.of(new AggregationMetric("tag1"))),
				new TwitterPost("query", "handle", NOW, "post", List.of("tag1", "tag2")),
				new Aggregation().withMetrics(List.of(new AggregationMetric("tag1", 2), new AggregationMetric("tag2")))
			)
		);
	}
}
