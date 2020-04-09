package org.acme.domain;

public class TwitterSentiment {

    public String hashtag;
    public String sentiment;
    public int count;

    public TwitterSentiment(String hashtag, int count,String sentiment)    
    {
        this.hashtag = hashtag;
        this.count = count;
        this.sentiment = sentiment;
    }
    
    public String toString(){//overriding the toString() method  
  	  return    "Hashtag: " + hashtag + "\n" + 
  				"Count: " +  count + "\n" + 
  				"Sentiment Value: " + sentiment + "\n";
  } 
 
}