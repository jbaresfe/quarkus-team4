## Data format for Kafka topics
### Topic: All hashtags with metadata
```json
{
  "query": "person/event/location to query from",
  "handle": "poster's twitter handle",
  "timestamp": "timestamp of post in UTC format",
  "post": "String containing full contents of post",
  "hashtags": ["list", "of", "hashtags", "in", "post"]
}
```

### Topic: windowed data
```json
{
  "startWindow": "timestamp of start of window data",
  "endWindow": "timestamp of end of window data",
  "metrics": [
    {
      "hashtag": "String value of hashtag",
      "count": Number representing the number of times the hashtag has been seen in the window
    },
    ...
  ]
}
```

## Deploy Octopus in OpenShift Cluster
1. Login into Openshift Cluster
2. Execute following maven command
````
mvn clean package -Dquarkus.kubernetes.deploy=true -DskipTests
````
