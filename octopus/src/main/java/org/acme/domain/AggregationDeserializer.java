package org.acme.domain;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class AggregationDeserializer extends JsonbDeserializer<Aggregation> {
        public AggregationDeserializer() {
                super(Aggregation.class);
        }
}