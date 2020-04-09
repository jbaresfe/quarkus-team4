package com.github.octopus.aggregator.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "twitter-api")
public interface TwitterPropertiesConfig {

    @ConfigProperty(name = "consumer.key") 
    String consumerKey();

    @ConfigProperty(name = "consumer.secret") 
    String consumerSecret();

    @ConfigProperty(name = "accesstoken") 
    String accessToken();

    @ConfigProperty(name = "accesstoken.secret") 
    String accessTokenSecret();
}