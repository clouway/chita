package com.clouway.chita;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class HttpResponse {

  public interface ResponseRead<E> {

    E as(Class<? extends Transport> var1);

  }


  public static HttpResponse dummyResponse() {
    return new HttpResponse(0, "");
  }

  private int code;

  private  String message;

  private InputStream inputStream;


  HttpResponse(int code, String message) {

    this.code = code;
    this.message = message;
  }

  HttpResponse(HttpURLConnection connection) {

    try {
      this.code = connection.getResponseCode();
      this.message = connection.getResponseMessage();
      this.inputStream = connection.getInputStream();
    } catch (IOException e) {
    }
  }

  /**
   * Returns the HTTP status code.
   */
  public int code() {
    return code;
  }

  /**
   * Returns true if the code is in [200..300), which means the request was
   * successfully received, understood, and accepted.
   */
  public boolean isSuccessful() {
    return code() == 200;
  }

  /**
   * the response is dummy when the request was not sent because the
   * given url(destination) was empty or NULL
   *
   * @return
   */
  public boolean isDummy() {
    if (code == 0) {
      return true;
    }
    return false;
  }

  /**
   * Returns the HTTP status message or null if it is unknown.
   */
  public String statusMessage() {
    return message;
  }

  public <E> ResponseRead<E> read(final Class<E> entityClazz) {
    return new ResponseRead<E>() {
      @Override
      public E as(Class<? extends Transport> clazz) {
        E result = null;
        try {
          Transport transport = clazz.newInstance();
          result = transport.in(inputStream, entityClazz);
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return result;
      }
    };
  }

  /**
   * Returns the response as byte array
   */
  public byte[] readBytes(){
    try {
      return IOUtils.toByteArray(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
