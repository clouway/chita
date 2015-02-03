package com.clouway.chita;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Tsony Tsonev (tsony.tsonev@clouway.com)
 */
public class CustomTransport implements ChitaTransport {
  @Override
  public <T> T in(InputStream in, Class<T> type) throws IOException {
    return new Gson().fromJson(new InputStreamReader(in), type);
  }

  @Override
  public <T> void out(OutputStream out, Class<T> type, T data) {
    String json = new Gson().toJson(data);
    try {
      ByteStreams.copy(new ByteArrayInputStream(json.getBytes("UTF-8")), out);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String contentType() {
    return "application/json";
  }
}
