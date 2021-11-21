package com.stormpath.examples.shiro.test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ContainerIntegrationTest {

    @Test
    public void logoutThenLogin() throws Exception {
        Server server = new Server(0);
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();
        assertTrue(server.isStarted());

        /* www.eclipse.org/jetty/javadoc/jetty-10/org/eclipse/jetty/servlet/ServletTester.html

        ServletTester is not best practice and may be deprecated and eventually removed in future Jetty versions.

        *NB* moved to (<classifier>tests), first time in jetty-servlet-9.3.0.M1-tests.jar 2014-11-03

        ServletTester is a just a wrapper around a ServletContextHandler, with a LocalConnector
        to accept HTTP/1.1 requests, so there is no value that this class adds to already existing classes.

        Replace its usages with:

        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler(server, "/contextPath");
        // Configure the context here.
        server.start();

        You can configure the context by adding Servlets and Filters, attributes, etc. even after it has been started.
        Use HttpTester and LocalConnector to make HTTP/1.1 requests, in this way:

        // Generate the request.
        HttpTester.Request request = HttpTester.newRequest();
        request.setMethod("GET");
        request.setURI("/contextPath/servletPath");
        request.put(HttpHeader.HOST, "localhost");
        ByteBuffer requestBuffer = request.generate();

        // Send the request buffer and get the response buffer.
        ByteBuffer responseBuffer = connector.getResponse(requestBuffer);

        // Parse the response buffer.
        HttpTester.Response response = HttpTester.parseResponse(responseBuffer);
        assert response.getStatus() == HttpStatus.OK_200;
        */
        /* Below is the old "test" code
        // Make sure we are logged out
        try (WebClient webClient = new WebClient()) {
            HtmlPage homePage = webClient.getPage("/");
            homePage.getAnchorByHref("/logout").click();
        }

        try (WebClient webClient = new WebClient()) {
            HtmlPage page = webClient.getPage(BaseURI + "/login.jsp");
            HtmlForm form = page.getFormByName("loginform");
            form.getInputByName("username").setValueAttribute("root");
            form.getInputByName("password").setValueAttribute("secret");
            page = form.getInputByName("submit").click();
            // This will throw an exception if not logged in
            page.getAnchorByHref("/logout");
        }
        */
    }
}
