run
```
mvn jetty:run -Djetty.port=9091
# in another terminal, so the output is not intermixed
mvn jetty:run -Djetty.port=9092
```

TODO: Running the two servers above
should show in the first terminal window
```
com.hazelcast.cluster.ClusterManager
INFO: [127.0.0.1]:5701 [dev]

Members [1] {
	Member [127.0.0.1]:5701 this
}
```
and in the second terminal window, after a pause:
```
Members [2] {
	Member [127.0.0.1]:5701
	Member [127.0.0.1]:5702 this
}
```
the first window is also updated, listing the new `:5702` Member

When the second Member goes down (on ^C), the 1st log shows
return to the initial status `Members [1] {..}`

## Errata
TBD
