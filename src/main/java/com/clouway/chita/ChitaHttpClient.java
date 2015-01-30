package com.clouway.chita;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import static com.clouway.chita.HttpResponse.dummyResponse;

/**
 * CHITA -  clouway http intelligent transport api
 *
 * Represent a wrapper of a {@link java.net.HttpURLConnection}. The main goal is to represent a builder style
 * http request construction and sending different objects by POST method using sitebricks {@link Transport}
 * <p/>
 * Example:
 * <p/>
 * GET
 * <p/>
 * HttpRequest request = httpRequest(new TargetUrl("abv.bg")).build();
 * ChitaHttp chitaClient = new ChitaHttpClient();// in most cases chitaClient should be injected
 * HttpResponse response = chitaClient.execute(request);
 * <p/>
 * POST
 * <p/>
 * TargetUrl targetUrl = new TargetUrl("http://telcong.com", "/test/address");
 * HttpRequest request = httpRequest(targetUrl).post(person).as(GsonTransport.class).build();
 * ChitaHttp chitaClient = new ChitaHttpClient();// in most cases chitaClient should be injected
 * HttpResponse response = chitaClient.execute(request);
 * <p/>
 * also there is a way to read an object from the response
 * <p/>
 * Result reply = response.read(Result.class).as(GsonTransport.class);
 * <p/>
 * here the response is using sitebricks {@link Transport} for
 * deserializing the result object
 *
 * @author Tsony Tsonev (tsony.tsonev@clouway.com)
 */
public class ChitaHttpClient<T> implements ChitaHttp{

  private static final Logger log = Logger.getLogger(HttpRequest.class.getName());

  @Override
  public HttpResponse execute(HttpRequest request) {
    OutputStream out = null;
    InputStream inputStream = null;

    if (request.getUrl().isAvailable()) {

      try {
        log.info("url: " + request.getUrl().getValue().get());
        String url = request.getUrl().getValue().get();
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(request.getMethodType());
        conn.setConnectTimeout(request.getConnectTimeout());
        conn.setDoOutput(true);

        //if properties are added to the request
        Map<String, String> requestProperties = request.getProperties();
        if(!requestProperties.isEmpty()){
          for(String key : requestProperties.keySet()){
            conn.setRequestProperty(key, requestProperties.get(key));
          }
        }


        //if no transport or no body
        Object body = request.getBody();
        if (request.getTransportClass() != null && body != null) {

          Transport transport = (Transport) request.getTransportClass().newInstance();

          conn.setRequestProperty("Content-Type", transport.contentType());

          out = conn.getOutputStream();

          transport.out(out, (Class<T>) body.getClass(), (T) body);
          out.flush();
          out.close();

        } else if (!Strings.isNullOrEmpty(request.getContentType())) {

          conn.setRequestProperty("Content-Type", request.getContentType());

        }

        inputStream = conn.getInputStream();

        return new HttpResponse(conn.getResponseCode(), conn.getResponseMessage(), inputStream);

      } catch (ProtocolException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      return new HttpResponse(-1, "");
    }

    return dummyResponse();
  }
}
