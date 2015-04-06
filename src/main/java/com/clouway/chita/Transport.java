package com.clouway.chita;

import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Tsony Tsonev (tsony.tsonev@clouway.com)
 */
public interface Transport {
  <T> T in(InputStream in, Class<T> type) throws IOException;

  <T> T in(InputStream in, TypeLiteral<T> type) throws IOException;

  <T> void out(OutputStream out, Class<T> type, T data) throws IOException;

  String contentType();
}
