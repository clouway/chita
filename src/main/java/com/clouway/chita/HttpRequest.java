package com.clouway.chita;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class HttpRequest<T>{

  private static final Logger log = Logger.getLogger(HttpRequest.class.getName());


  public static Builder httpRequest(TargetUrl url) {
    return new Builder(url);
  }

  public static class Builder<T> {

    private TargetUrl url;

    private T body;

    private String methodType = "GET";

    private String contentType;

    private Map<String, String> properties = new HashMap<String, String>();

    private int connectTimeout = 10000;

    private Class<? extends Transport> transportClass;

    public Builder(TargetUrl url) {

      this.url = url;
    }

    public Builder connectTimeout(int seconds) {
      connectTimeout = seconds * 1000;
      return this;
    }

    public Builder post(T entity) {
      this.methodType = "POST";
      this.body = entity;
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
      byte[] authorizationBytes = Base64.encodeBase64(passToken.getBytes());
      this.properties.put("Authorization", "Basic " + new String(authorizationBytes).trim());
      return this;
    }

    public HttpRequest build() {
      HttpRequest r = new HttpRequest();
      r.connectTimeout = connectTimeout;
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

  private TargetUrl url;

  private String methodType = "GET";

  private String contentType;

  private Map<String, String> properties;

  private int connectTimeout = 10000;

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