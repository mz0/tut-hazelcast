/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.stormpath.examples.shiro.test;

import com.gargoylesoftware.htmlunit.WebClient;
import static org.junit.Assert.assertTrue;

import org.eclipse.jetty.server.ServerConnector;
import org.junit.Before;
import org.junit.BeforeClass;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.BindException;

public abstract class AbstractContainerTest {
    protected static int port = 0;

    protected static PauseableServer server;

    protected final WebClient webClient = new WebClient();

    @BeforeClass
    public static void startContainer() throws Exception {
            try {
                server = createAndStartServer();
            } catch (BindException e) {
            }
        assertTrue(server.isStarted());
    }

    private static PauseableServer createAndStartServer() throws Exception {
        PauseableServer server = new PauseableServer();
        ServerConnector connector = new ServerConnector(server);
        AbstractContainerTest.port = connector.getPort();
        server.setConnectors(new Connector[]{connector});
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();
        return server;
    }

    protected static String getBaseUri() {
        return "http://localhost:" + port + "/";
    }

//  @Before public void beforeTest() {webClient.setThrowExceptionOnFailingStatusCode(true);}

    public void pauseServer(boolean paused) {
        if (server != null) server.pause(paused);
    }

    public static class PauseableServer extends Server {
        public synchronized void pause(boolean paused) {
            try {
                if (paused) for (Connector connector : getConnectors())
                    connector.stop();
                else for (Connector connector : getConnectors())
                    connector.start();
            } catch (Exception e) {
            }
        }
    }
}
