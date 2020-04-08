
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

@Path("/sentiments")
public class SentimentResource {

    public static final Logger log = LoggerFactory.getLogger(SentimentResource.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String  hello() {
        return "hello world";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/refresh/{topic}")
    public List<TwitterSentiment> sentiments(@PathParam("topic") String topic) {

        log.info("Using Topic:" + topic);

        List<TwitterSentiment> data = new ArrayList<TwitterSentiment>();

        TwitterSentiment val = new TwitterSentiment("tag1",22,"happy");
        TwitterSentiment val2 = new TwitterSentiment("tag2",33,"sad");
        data.add(val);
        data.add(val2);

        return data;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{query}")
    public String query(@PathParam("query") String query) {

        //TODO : Start knative twitter querries
        log.info("Using Query:" + query);
        return "topica";
    }
}