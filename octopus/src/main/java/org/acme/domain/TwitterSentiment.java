package org.acme.domain;

public class TwitterSentiment {

    public String hashtag;
    public String sentiment;

    public TwitterSentiment(String hashtag, String sentiment)    
    {
        this.hashtag = hashtag;
        this.sentiment = sentiment;

    }

}