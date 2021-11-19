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

## step-2
added Hazelcast (with default built-in configuration)

Running the two servers above
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
the first window is also updated, listing the new `:5702` Member

When the second Member goes down (on ^C), the 1st log shows
return to the initial status `Members [1] {..}`

## step-3
adds `hazelcast.xml` configuration file to show Hazelcast cluster re-configuration
in step-4: renaming `<group>` "dev" to something else, and changing port allocation.

A kind of mystery is a reference to `hazelcast-config-2.5.xsd` here,
since shiro-hazelcast:1.3.x pulls hazelcast:2.4 (2.4.1)

## Errata
Hazelcast 2.4 configuration code produces this misleading error message:
```
[Fatal Error] :6:3: The element type "hr" must be terminated by the matching end-tag "</hr>".
```
it doesn't seem affecting Hazelcast or this webapp functioning.
