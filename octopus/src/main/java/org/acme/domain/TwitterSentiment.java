package org.acme.domain;

public class TwitterSentiment {

    public String hashtag;
    public String sentiment;
    public int count;
    public String startTime;
    public String endTime;

    public TwitterSentiment(String hashtag, int count,String sentiment, String startTime, String endTime)    
    {
        this.hashtag = hashtag;
        this.count = count;
        this.sentiment = sentiment;
        this.startTime = startTime;
        this.endTime = endTime;

    }

}