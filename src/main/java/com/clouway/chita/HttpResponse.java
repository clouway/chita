package com.clouway.chita;

import com.google.inject.TypeLiteral;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

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

  private final int code;

  private final String statusMessage;

  private InputStream inputStream;

  HttpResponse(int responseCode, String responseMessage, InputStream inputStream) {

    code = responseCode;
    statusMessage = responseMessage;
    this.inputStream = inputStream;
  }

  HttpResponse(int code, String statusMessage) {

    this.code = code;
    this.statusMessage = statusMessage;
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
    return code == 200;
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
    return statusMessage;
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


  public <E> ResponseRead<E> read(final TypeLiteral<E> entityClazz) {
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
