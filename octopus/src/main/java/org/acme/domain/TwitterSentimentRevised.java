package org.acme.domain;

public class TwitterSentimentRevised {

    public String hashtag;
    public String post; 
    public String sentiment;

    public TwitterSentimentRevised(String hashtag, String post, String sentiment)    
    {
        this.hashtag = hashtag;
        this.post = post;
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
 
    public void setPost(String somePost) {
    	this.post = somePost;
    }
    
    public String getPost(){
    	return this.post;
    }
    
   
    public String toString(){//overriding the toString() method  
    	  return  "\n" + "Hashtag: " + hashtag + "\n" + 
    				"Post: " +  post + "\n" + 
    				"Sentiment Value: " + sentiment + "\n";
    }  
    
}