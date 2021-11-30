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
    private HazelcastInstance hazelcastInstance;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (this.hazelcastInstance != null) {
            log.error("non-null HazelcastInstance found on ServletContext Initialized Event.");
        } else {
            hazelcastInstance = Hazelcast.newHazelcastInstance(); // use hazelcast.ini from Classpath
            sce.getServletContext().setAttribute(CLUSTER_MAPPER_KEY, hazelcastInstance);
        }
        log.trace("This web-app has initialized/joined auth-cluster");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(CLUSTER_MAPPER_KEY);
        try {
            this.hazelcastInstance.getLifecycleService().shutdown();
        } catch (Throwable t) {
            log.warn("Unable to cleanly shutdown HazelcastInstance. Ignoring (shutting down)...", t);
        } finally {
            this.hazelcastInstance = null;
        }
        log.trace("This web-app has left auth-cluster");
    }
}
