package com.clouway.chita;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TargetUrl {
  private final String url;
  private String suffix;

  public TargetUrl(String url, String suffix) {
    this.url = url;
    this.suffix = suffix;
  }

  public TargetUrl(String url) {
    this.url = url;
  }

  public Optional<String> getValue() {

    if (isAvailable()) {

      String value = url;
      if (!Strings.isNullOrEmpty(suffix)) {
        value = value + suffix;
      }
      return Optional.of(value);
    }

    return Optional.absent();
  }

  public boolean isAvailable() {
    return !Strings.isNullOrEmpty(url);
  }
}
