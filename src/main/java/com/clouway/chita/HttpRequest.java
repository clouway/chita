package com.clouway.chita;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Used for builder style http request construction and sending different objects by
 * POST/GET/PUT/DELETE method using sitebricks {@link Transport}
 *
 * <p/>
 * Examples:
 * <br>
 *
 * GET
 * <pre>
 *   HttpRequest request = httpRequest(new TargetUrl("http://abv.bg")).build();
 * </pre>
 *
 * POST
 * <pre>
 *   // TargetUrl targetUrl = ...
 *
 *   HttpRequest request = httpRequest(targetUrl).post(person).as(GsonTransport.class).build();
 * </pre>
 *
 * PUT
 * <pre>
 *   // TargetUrl targetUrl = ...
 *
 *   HttpRequest request = httpRequest(targetUrl).put(address).as(GsonTransport.class).build();
 * </pre>
 *
 * <p/>
 * This class maintains the order of the header fields within the HTTP message, so the developers are able
 * to specify correct header order depending of the situation.
 *
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class HttpRequest<T>{

  public static <T> Builder<T> httpRequest(TargetUrl url) {
    return new Builder<T>(url);
  }

  public static class Builder<T> {

    private TargetUrl url;

    private T body;

    private String methodType = "GET";

    private String contentType;

    private Map<String, String> properties = new LinkedHashMap<String, String>();

    private int connectTimeout = 10000;

    private int readTimeout = 10000;

    private Class<? extends Transport> transportClass;

    public Builder(TargetUrl url) {

      this.url = url;
    }

    public Builder connectTimeout(int seconds) {
      connectTimeout = seconds * 1000;
      return this;
    }

    public Builder readTimeout(int seconds) {
      readTimeout = seconds * 1000;
      return this;
    }

    public Builder post(T entity) {
      this.methodType = "POST";
      this.body = entity;
      return this;
    }

    public Builder put(T entity) {
      this.methodType = "PUT";
      this.body = entity;
      return this;
    }

    public Builder delete() {
      this.methodType = "DELETE";
      this.body = null;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder addProperty(String name, String value){
      this.properties.put(name, value);
      return this;
    }

    public Builder basicAuthorization(String username, String password){
      String passToken = username + ":" + password;
      String authorization = BaseEncoding.base64().encode(passToken.getBytes());
      this.properties.put("Authorization", "Basic " + authorization.trim());
      return this;
    }

    /**
     * Sets the max age for the request to be cached (the lower the age is the chance the response was cached is lower too)
     * @param seconds
     * @return
     */
    public Builder cacheControl(int seconds){
      this.properties.put("Cache-Control", "max-age=" + seconds);
      return this;
    }

    public HttpRequest build() {
      HttpRequest r = new HttpRequest();
      r.connectTimeout = connectTimeout;
      r.readTimeout = readTimeout;
      r.body = body;
      r.url = url;
      r.methodType = methodType;
      r.contentType = contentType;
      r.transportClass = transportClass;
      r.properties = properties;
      return r;
    }

    public Builder as(Class<? extends Transport> transportClass) {
      this.transportClass = transportClass;
      return this;
    }
  }

  private HttpRequest() {
  }

  private TargetUrl url;

  private String methodType = "GET";

  private String contentType;

  private Map<String, String> properties;

  private int connectTimeout = 10000;

  private int readTimeout = 10000;

  private T body;

  public TargetUrl getUrl() {
    return url;
  }

  public T getBody() {
    return body;
  }

  public String getMethodType() {
    return methodType;
  }

  public String getContentType() {
    return contentType;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public Class<? extends Transport> getTransportClass() {
    return transportClass;
  }

  public boolean hasContentType() {
    return !Strings.isNullOrEmpty(contentType);
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  private Class<? extends Transport> transportClass = TextTransport.class;

  public HttpRequest<T> as(Class<? extends Transport> transportClass) {
    this.transportClass = transportClass;
    return this;
  }
}