
package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.ArrayList;
import org.acme.domain.TwitterSentiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Path("/sentiments")
public class SentimentResource {

    public static final Logger log = LoggerFactory.getLogger(SentimentResource.class);	

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String hello() {
		return "hello world";
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{query}")
	public String query(@PathParam("query") String query) {

        log.info("Starting Query:" + query);
		return "mytopic";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/refresh/{topic}")
	public List<TwitterSentiment> refresh(@PathParam("topic") String query) {

		List<TwitterSentiment> data = new ArrayList<TwitterSentiment>();

		TwitterSentiment val = new TwitterSentiment("tag1", 22,"happy");
		TwitterSentiment val2 = new TwitterSentiment("tag2",15, "sad");
		data.add(val);
		data.add(val2);

		return data;
	}


}