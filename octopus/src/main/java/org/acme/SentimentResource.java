
package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.ArrayList;
import org.acme.domain.TwitterSentiment;

@Path("/sentiments")
public class SentimentResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String  hello() {
        return "hello world";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{query}")
    public List<TwitterSentiment> sentiments(@PathParam("query") String query) {

        List<TwitterSentiment> data = new ArrayList<TwitterSentiment>();

        TwitterSentiment val = new TwitterSentiment("tag1","HAPPY");
        TwitterSentiment val2 = new TwitterSentiment("tag2","SAD");
        data.add(val);
        data.add(val2);

        return data;
    }
}