package com.clouway.chita;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TextTransport implements Transport {


  @Override
  public <T> T in(InputStream in, Class<T> type) throws IOException {
    return type.cast(CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8)));
  }

  @Override
  public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
    return (T) CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
  }

  @Override
  public <T> void out(OutputStream out, Class<T> type, T data) throws IOException {
    try {
      out.write(data.toString().getBytes(Charsets.UTF_8));
      out.flush();
    } catch (IOException var5) {
      throw new RuntimeException(var5);
    }
  }

  @Override
  public String contentType() {
    return "text/plain";
  }
}
