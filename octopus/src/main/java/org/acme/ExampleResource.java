package org.acme;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Path("/tweets")
public class ExampleResource {
	
	 public static ConfigurationBuilder getConfigurationBuilder() {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			return cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("ry4rxObtDITrDl3iCDMh2sLdf")
			  .setOAuthConsumerSecret("5EVDpqL8dvk96F7vYrPgl8kzAquWG4oXbi6MihqPO89Z49CAoz")
			  .setOAuthAccessToken("578677046-sWfVlpJ9Pd8bWvIFrky4qlZ2wxdysi2mbOPA27jP")
			  .setOAuthAccessTokenSecret("8CeYONHNdoFLYiF1HRpFaQiSgvviIZ59uJJGPQSptleDn");
		}
	 
	 @GET
	 @Path("/{topic}")
	 @Transactional
	 @Produces(MediaType.TEXT_PLAIN)
	 public List<String> streamTweets(@PathParam String topic) throws TwitterException {		
			
		TwitterStream twitterStream = new TwitterStreamFactory(getConfigurationBuilder().build()).getInstance();
		List<String> tweets = new ArrayList<String>();
		
		StatusListener statusListener = new StatusListener() {
			
	        public void onStatus(Status status) {
	        	String tweetString = "@" + status.getUser().getScreenName() + ":" + status.getText();
	        	System.out.println(tweetString);
	        	tweets.add(tweetString);
	        }

			public void onException(Exception ex) {
				// TODO Auto-generated method stub
				
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// TODO Auto-generated method stub
				
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// TODO Auto-generated method stub
				
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				// TODO Auto-generated method stub
				
			}

			public void onStallWarning(StallWarning warning) {
				// TODO Auto-generated method stub
				
			} 
		};
			
		FilterQuery filter = new FilterQuery();
		String hashtag = "#" + topic;
	    String keywords[] = {hashtag};
		filter.track(keywords);
		twitterStream.addListener(statusListener);
		twitterStream.filter(filter);
			
		return tweets;
		
	}

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}