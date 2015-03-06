package com.clouway.chita;

/**
 * @author Stefan Dimitrov (stefan.dimitrov@clouway.com).
 */
public class UrlTemplateNotResolvedError extends RuntimeException {
  public UrlTemplateNotResolvedError() {
    super();
  }

  public UrlTemplateNotResolvedError(String message) {
    super(message);
  }
}
