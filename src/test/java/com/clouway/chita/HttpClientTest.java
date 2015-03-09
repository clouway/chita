package com.clouway.chita;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.clouway.chita.HttpRequest.httpRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class HttpClientTest {

  private String serverUrl = "http://localhost:9999";
  private static String serviceUrl = "/r/provision/service";
  private HttpClient httpClient = new HttpClient();
  private static String errorServiceUrl = "/r/access";
  private static int errorCode = 400;

  static class TestingServer {
    private Server server;

    private String lastReceivedRequest;
    private String lastReceivedContentType;
    private boolean receivedGETRequest;
    private Map<String, String> requestHeaders;

    public TestingServer(int port) {

      server = new Server(port);

      Context root = new Context(server, "/", Context.ALL);

      root.addServlet(DefaultServlet.class, "/");
      root.addServlet(new ServletHolder(new HttpServlet() {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          lastReceivedRequest = new String(ByteStreams.toByteArray(req.getInputStream()));
          lastReceivedContentType = req.getContentType();
          requestHeaders = new HashMap<String, String>();

          Enumeration headerNames = req.getHeaderNames();
          while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = req.getHeader(key);
            requestHeaders.put(key, value);
          }

          PrintWriter writer = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), "UTF8"), true);
          writer.print(lastReceivedRequest);
          writer.flush();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          receivedGETRequest = true;
        }
      }), serviceUrl);

      // Error servlet
      root.addServlet(new ServletHolder(new HttpServlet() {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          resp.sendError(errorCode);
        }
      }), errorServiceUrl);


    }

    public void start() {
      try {
        server.start();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public void stop() {
      try {
        server.stop();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public void hasReceivedGetRequest() {
      assertThat(receivedGETRequest, is(equalTo(true)));
    }

    public void hasNotReceivedGetRequest() {
      assertThat(receivedGETRequest, is(equalTo(false)));
    }

    public void hasReceivedRequestThatContains(String expected) {
      assertThat(lastReceivedRequest, containsString(expected));
    }

    public void hasReceivedRequestWithContentType(String expectedContentType) {
      assertThat("request was send with different content type?", lastReceivedContentType, is(expectedContentType));
    }

    public Map<String, String> getRequestHeaders() {
      return requestHeaders;
    }
  }

  private TestingServer server = new TestingServer(9999);


  @Before
  public void startServer() {
    server.start();
  }

  @After
  public void stopServer() {
    server.stop();
  }

  @Test
  public void provisionSingleService() {
    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl)).post(services(
            new DummyService("1", "test@test.com", "ACTIVE"))).as(CustomTransport.class).build();
    HttpResponse response = httpClient.execute(request);

    server.hasReceivedRequestThatContains("test@test.com");
    server.hasReceivedRequestWithContentType("application/json");
    server.hasNotReceivedGetRequest();

    assertThat(response.isSuccessful(), is(equalTo(true)));
    assertThat(response.code(), is(equalTo(200)));
  }

  @Test
  public void theHostsIsDown() {
    HttpRequest request = httpRequest(new TargetUrl("http://localhost:1919")).post(services(
            new DummyService("1", "test@test.com", "ACTIVE"))).as(CustomTransport.class).build();
    HttpResponse response = httpClient.execute(request);

    assertThat(response.isSuccessful(), is(equalTo(false)));
    assertThat(response.code(), is(equalTo(-1)));
  }

  @Test
  public void whenEmptyUrl() {
    HttpRequest request = httpRequest(new TargetUrl("")).post(services(
            new DummyService("1", "test@test.com", "ACTIVE"))).as(CustomTransport.class).build();
    HttpResponse response = httpClient.execute(request);
    assertNull("hm, request was received", server.lastReceivedRequest);
    assertThat(response.isSuccessful(), is(equalTo(false)));
    assertThat(response.code(), is(equalTo(0)));

    HttpRequest anotherRequest = httpRequest(new TargetUrl(null)).post(services(
            new DummyService("1", "test@test.com", "ACTIVE"))).as(CustomTransport.class).build();
    response = httpClient.execute(anotherRequest);
    assertNull("hm, request was received", server.lastReceivedRequest);
    assertThat(response.isSuccessful(), is(equalTo(false)));
    assertThat(response.code(), is(equalTo(0)));
  }

  @Test
  public void whenEmptyUrlWithSuffix() {
    HttpRequest request = httpRequest(new TargetUrl("", "suffix")).post(services(
            new DummyService("1", "test@test.com", "ACTIVE"))).as(CustomTransport.class).build();
    httpClient.execute(request);
    assertNull("hm, request was received", server.lastReceivedRequest);

    HttpRequest anotherRequest = httpRequest(new TargetUrl(null, "suffix")).post(services(
            new DummyService("1", "test@test.com", "ACTIVE"))).as(CustomTransport.class).build();
    httpClient.execute(anotherRequest);
    assertNull("hm, request was received", server.lastReceivedRequest);
  }


  @Test
  public void sendMultipleServices() {
    ArrayList<DummyService> services = services(
            new DummyService("1", "firstService", "ACTIVE"),
            new DummyService("2", "secondService", "ACTIVE")
    );

    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl)).post(services).as(CustomTransport.class).build();
    httpClient.execute(request);

    server.hasReceivedRequestThatContains("firstService");
    server.hasReceivedRequestThatContains("secondService");
  }


  @Test
  public void executeSimpleGet() throws Exception {

    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl)).build();
    HttpResponse response = httpClient.execute(request);

    server.hasReceivedGetRequest();
    assertThat(response.isSuccessful(), is(equalTo(true)));
    assertThat(response.code(), is(equalTo(200)));
  }

  @Test
  public void fetchServiceFromTheResponse() throws Exception {

    DummyService dummyService = new DummyService("1", "test@test.com", "ACTIVE");
    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl)).post(dummyService).as(CustomTransport.class).build();
    HttpResponse response = httpClient.execute(request);

    DummyService reply = response.read(DummyService.class).as(CustomTransport.class);

    assertThat(reply, is(equalTo(dummyService)));
  }

  @Test
  public void providedRequestParameters() throws Exception {
    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl))
            .addProperty("Referer", "/localhost")
            .addProperty("User-Agent", "chrome")
            .post("")
            .build();
    httpClient.execute(request);

    assertThat(server.getRequestHeaders().get("Referer"), is("/localhost"));
    assertThat(server.getRequestHeaders().get("User-Agent"), is("chrome"));
  }

  @Test
  public void fetchBytesFromTheResponse() throws Exception {

    Integer data = 123456;
    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl)).post(data).as(CustomTransport.class).build();
    HttpResponse response = httpClient.execute(request);

    byte[] reply = response.readBytes();

    assertThat(reply, is(equalTo(data.toString().getBytes())));
  }

  @Test
  public void useBasicAuthorization() throws Exception {
    HttpRequest request = httpRequest(new TargetUrl(serverUrl, serviceUrl))
            .basicAuthorization("John","pass123")
            .post("")
            .build();
    httpClient.execute(request);

    assertThat(server.getRequestHeaders().get("Authorization").startsWith("Basic"), is(true));
  }

  private ArrayList<DummyService> services(DummyService... dummyService) {
    return Lists.newArrayList(dummyService);
  }

  @Test
  public void serverReturnsBadRequestError() throws Exception {
    errorCode = 400;
    HttpRequest request = httpRequest(new TargetUrl(serverUrl, errorServiceUrl)).build();

    HttpResponse response = httpClient.execute(request);

    assertThat(response.code(), is(errorCode));
  }

}
