package com.exactpro.web;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AuthClusterBuilder implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(AuthClusterBuilder.class);
	public final static String CLUSTER_MAPPER_KEY = "shared.cluster.map";
	public final static String JNDI_DS_RESOURCE_NAME = "java:/comp/env/jdbc/AAData";
	private HazelcastInstance hazelcastInstance;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		hazelcastInstance = Hazelcast.newHazelcastInstance(); // use hazelcast.ini from Classpath
		sce.getServletContext().setAttribute(CLUSTER_MAPPER_KEY, hazelcastInstance);
		log.trace("This web-app has initialized/joined auth-cluster");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		sce.getServletContext().removeAttribute(CLUSTER_MAPPER_KEY);
		this.hazelcastInstance = null;
		Hazelcast.shutdownAll();
		log.trace("This web-app has left auth-cluster");
	}
}
