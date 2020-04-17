## Scope
This app analyses the hashtags in the tweets over the last 10 sec and 60 sec windows while users tweet about a certain person/event/location. The application then finds the popular hashtags based on the Twitter stream.

## Prerequites
* OpenShift 4.X cluster
* Admin user access for installing AMQ Stream Operator

## Install AMQ Stream Operator
To get started AMQ Stream Operator must be installed. Please follow the guidelines to install it from OperatorHub. You should have cluster admin permissions.

[Adding Operators to cluster](https://docs.openshift.com/container-platform/4.3/operators/olm-adding-operators-to-cluster.html)

## Deploy Kafka Cluster and Kafka Topics
Login in to OCP cluster and access to the namespace where the Kafka cluster will be installed.
```bash
oc create -f k8s/kafka.yml
```

## Data format for Kafka topics
### Topic: All hashtags with metadata
**Topic Name:** twitter-posts
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
**Topic Name:** hashtag-counts
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