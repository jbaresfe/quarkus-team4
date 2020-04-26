package com.github.octopus.aggregator.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class TwitterSentimentAnalyzer {
	
public static int calculateSentiment(String post) {
		
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
        
        //String result = "";
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
            return -1;
        }
        
        //result = mainSentiment + " " + map.get(mainSentiment);
        
        return mainSentiment;	
	}

}
