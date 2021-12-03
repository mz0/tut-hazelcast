run
```
mvn jetty:run -Djetty.port=9091
# in another terminal, so the output is not intermixed
mvn jetty:run -Djetty.port=9092
```

TODO: Running the two servers above
should show in the first terminal window
```
com.hazelcast.internal.cluster.ClusterService
INFO: [127.0.0.1]:5701 [tut-filter] [3.7.8]

Members [1] {
	Member [127.0.0.1]:5701 - abc12345-def6-7777-a89b-ccccc123abc4 this
}
```
and in the second terminal window, after a pause:
```
Members [2] {
	Member [127.0.0.1]:5701 - abc12345-def6-7777-a89b-ccccc123abc4
	Member [127.0.0.1]:5702 - de876543-21ab-cdef-9876-543210abcdef this
}
```
the first window is also updated, listing the new `:5702` Member

When the second Member goes down (on ^C), the 1st log shows
return to the initial status `Members [1] {..}`

## Errata
(to make a working WAR please do `mvn package`)

* this is not a well-behaving web-app, on Tomcat shutdown:
```
SEVERE [main]
org.apache.catalina.loader.WebappClassLoaderBase.checkThreadLocalMapForLeaks
The web application [sample-web-login-0.1] created a ThreadLocal ...
but failed to remove it when the web application was stopped.
```

### On Maven plugin configuration
This command was helpful for figuring out jetty-plugin options:
```
mvn help:describe -Dplugin=org.eclipse.jetty:jetty-maven-plugin -Ddetail
```
