package org.acme.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "octopus")
public interface OctopusTwitterPropertiesConfig {

    @ConfigProperty(name = "twitter-api.oauth.consumer.key") 
    String twitterapiOauthConsumerKey();

    @ConfigProperty(name = "twitter-api.oauth.consumer.secret") 
    String twitterapiOauthConsumerSecret();

    @ConfigProperty(name = "twitter-api.oauth.accesstoken") 
    String twitterapiOauthAccessToken();

    @ConfigProperty(name = "twitter-api.oauth.accesstoken.secret") 
    String twitterapiOauthAccessTokenSecret();
}