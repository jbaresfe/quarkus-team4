package org.acme;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.acme.*;
import org.acme.domain.TwitterPost;
import org.acme.domain.TwitterSentiment;
import org.acme.domain.TwitterSentimentRevised;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
/*
 * Very Negative: 0 
 * Negative: 1
 * Neutral: 2
 * Positive: 3
 * Very Positive: 4
 */
public class SentimentAnalysis {

	public static String calculateSentiment(String post) {
		
		Properties props = new Properties();

        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        int mainSentiment = 0;
        
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "Very Negative");
        map.put(1, "Negative");
        map.put(2, "Neutral");
        map.put(3, "Positive");
        map.put(4, "Very Positive");
        
        String result = "";
        if (post != null && post.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(post);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();

                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        
        if (mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }
        
        result = mainSentiment + " " + map.get(mainSentiment);
        
        return result;	
	}

	// To use for analyzing TwitterPost Object
	public static TwitterSentimentRevised calculatePostSentiment(String query, TwitterPost post) {
			
			Properties props = new Properties();
	
	        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
	
	        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	        
	        int mainSentiment = 0;
	        
	        Map<Integer, String> map = new HashMap<Integer, String>();
	        map.put(0, "Very Negative");
	        map.put(1, "Negative");
	        map.put(2, "Neutral");
	        map.put(3, "Positive");
	        map.put(4, "Very Positive");
	        
	        
	        String result = "";
	        if (post.getPost() != null &&  post.getPost().length() > 0) {
	            int longest = 0;
	            Annotation annotation = pipeline.process(post.getPost());
	
	            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
	                Tree tree = sentence.get(SentimentAnnotatedTree.class);
	                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	                String partText = sentence.toString();
	
	                if (partText.length() > longest) {
	                    mainSentiment = sentiment;
	                    longest = partText.length();
	                }
	            }
	        }
	        
	        if (mainSentiment > 4 || mainSentiment < 0) {
	            return null;
	        }
	        
	        result = mainSentiment + " " + map.get(mainSentiment);
	        
	        TwitterSentimentRevised sentimentObj = new TwitterSentimentRevised(query, post.getPost(), result);
	        
	        return sentimentObj;	
		}
	
		// To use for analyzing List of TwitterPosts
		public static TwitterSentiment aggregateSentimentAnalyzer(String query, List<TwitterPost> twitterPosts) {
				
				Properties props = new Properties();
		
		        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		
		        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		        
		        int mainSentiment = 0;
		        
		        Map<Integer, String> map = new HashMap<Integer, String>();
		        map.put(0, "Very Negative");
		        map.put(1, "Negative");
		        map.put(2, "Neutral");
		        map.put(3, "Positive");
		        map.put(4, "Very Positive");
		       
		        int sum = 0;
		        List<Integer> sentList = new ArrayList<Integer>();
		       
		        
		        for(TwitterPost post: twitterPosts) {
		        	if (post.getPost() != null &&  post.getPost().length() > 0) {
			            int longest = 0;
			            Annotation annotation = pipeline.process(post.getPost());
			
			            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			                Tree tree = sentence.get(SentimentAnnotatedTree.class);
			                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			                String partText = sentence.toString();
			
			                if (partText.length() > longest) {
			                    mainSentiment = sentiment;
			                    longest = partText.length();
			                }
			            }
			        }
			        
			        if (mainSentiment > 4 || mainSentiment < 0) {
			            return null;
			        }
			        else {
			        	sentList.add(mainSentiment);
			        	sum += mainSentiment;
			        }
		        }
		        
		        int avg = sum / sentList.size();
		        TwitterSentiment sentimentresult = new TwitterSentiment(query, sentList.size(), map.get(avg));
		        
		        return sentimentresult;	
			}
		
		/*public static void main(String[] args) {
			TwitterPost newPost = new TwitterPost("user1", "edeandrea", Instant.now(), "Man I really hope the #sentimentanalyzer works", List.of("sentimentanalyzer")),
			new TwitterPost("user2", "realDonaldTrump", Instant.now(), "The #economy is doing #tremendous!", List.of("economy", "tremendous"))
		}*/
		
}
