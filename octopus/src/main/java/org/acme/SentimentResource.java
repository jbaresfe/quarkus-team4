
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

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reactivestreams.Publisher;
import io.smallrye.reactive.messaging.annotations.Channel;
import javax.inject.Inject;

import org.acme.domain.TwitterSentiment;
import org.acme.domain.Aggregation;

@Path("/sentiments")
public class SentimentResource {

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
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{query}")
	public String query(@PathParam("query") String query) {

		log.info("Starting Query:" + query);
		log.info("KAFKA DATA:" + _sentiments);
		//TODO: Hook in backend Service
		return "mytopic";
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