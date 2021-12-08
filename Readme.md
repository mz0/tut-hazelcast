run
```
# see user.sql for MySQL-specific advice on setting-up a user and a DB
mysql -uzyx -p888 < user.sql
mvn jetty:run
```
to see cluster login in action, either
* make a WAR (`mvn package`) and deploy it to Tomcat (ensure `$CATALINA_BASE/lib/mysql-connector-java-8.0.27.jar` is present there)
* (in another terminal window) clone this repo to another dir, and run Jetty on a different port: `mvn jetty:run -Djetty.port=9092`

Running the two servers above
shows in the first terminal window
```
2021-12-08 16:19:39,930 INFO
com.hazelcast.internal.cluster.ClusterService: [127.0.0.1]:5702 [tut-filter] [3.12.12]

Members {size:1, ver:1} [
	Member [127.0.0.1]:5701 - abc12345-def6-7777-a89b-ccccc123abc4 this
]

```
and in the second terminal window, after a pause:
```
Members {size:2, ver:2} [
	Member [127.0.0.1]:5701 - abc12345-def6-7777-a89b-ccccc123abc4
	Member [127.0.0.1]:5702 - de876543-21ab-cdef-9876-543210abcdef this
}
```
the first window is also updated, listing the new `:5702` Member

When the second Member goes down (on ^C), the 1st log shows
return to the initial status `Members  {size:1, ver:3} {..}`

## Errata
already authenticated user may see strange errors when navigating to  `login.xhtml`

### On Maven plugin configuration
This command was helpful for figuring out jetty-plugin options:
```
mvn help:describe -Dplugin=org.eclipse.jetty:jetty-maven-plugin -Ddetail
```
