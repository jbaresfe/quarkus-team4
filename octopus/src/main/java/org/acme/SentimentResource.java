
package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.resteasy.annotations.SseElementType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reactivestreams.Publisher;
import io.smallrye.reactive.messaging.annotations.Channel;
import javax.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.acme.domain.TwitterSentiment;
import org.acme.domain.Aggregation;


@Path("/sentiments")
public class SentimentResource {

	
	@Inject
    @RestClient
    TwitterService twitterService;

    @Inject
    @Channel("hashtag-counts")
	Publisher<Aggregation> _sentiments;
	
    public static final Logger log = LoggerFactory.getLogger(SentimentResource.class);	

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String hello() {
		return "hello";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
    @Path("/query/{topic}")
    public javax.ws.rs.core.Response getTwitterSentiments(@PathParam("topic") String topic) {
		log.info("Quering TopicXX:" + topic);
		return twitterService.getByTopic(topic);

    }

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{query}")
	public String query(@PathParam("query") String query) {

		log.info("Starting Query:" + query);
		log.info("KAFKA DATA:" + _sentiments);
		return query;
	}


	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	@SseElementType(MediaType.APPLICATION_JSON)
	@Path("/refresh3/{topic}")
	public Publisher<Aggregation> refreshstring(@PathParam("topic") String topic) {

		log.info("Refresh string Topic:" + topic);
		return _sentiments;
	}


}