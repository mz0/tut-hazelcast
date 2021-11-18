See https://stormpath.com/blog/hazelcast-support-apache-shiro

by Brian Demers (2016-07-27)

test
```
mvn test
```
run
```
mvn jetty:run -Djetty.port=9091
# in another terminal, so the output is not intermixed
mvn jetty:run -Djetty.port=9092
```

We are at step-2 in this repo, running the two servers above
we can see in the first terminal window
```
com.hazelcast.cluster.ClusterManager
INFO: [10.0.0.10]:5701 [dev]

Members [1] {
	Member [10.77.132.112]:5701 this
}
```
and in the second terminal window, after a pause:
```
Members [2] {
	Member [10.0.0.10]:5701
	Member [10.0.0.10]:5702 this
}
```
the first window is also updated, listing `:5702` Member

When the second Member goes down (on ^C), the 1st log shows
initial status `Members [1] {..}`
