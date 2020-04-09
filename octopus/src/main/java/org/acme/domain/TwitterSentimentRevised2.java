package org.acme.domain;

public class TwitterSentimentRevised2 {

    public String hashtag;
    public int count; 
    public String sentiment;

    public TwitterSentimentRevised2(String hashtag, int count, String sentiment)    
    {
        this.hashtag = hashtag;
        this.count= count;
        this.sentiment = sentiment;
    }
    
    public String getHashtag(){
    	return this.hashtag;
    }
    
    public void setHashtag(String someHashTag) {
    	this.hashtag = someHashTag;
    }
    
    public String getSentiment(){
    	return this.sentiment;
    }
    
    public void setSentiment(String someSentiment) {
    	this.sentiment = someSentiment;
    }
 
    public void setCount(int someCount) {
    	this.count = someCount;
    }
    
    public int getCount(){
    	return this.count;
    }
    
   
    public String toString(){//overriding the toString() method  
    	  return  "\n" + "Hashtag: " + hashtag + "\n" + 
    				"Count: " +  count + "\n" + 
    				"Sentiment Value: " + sentiment + "\n";
    }  
    
}