package com.clouway.chita;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class TargetUrlTest {

  @Test
  public void regularUrl() throws Exception {
    String urlString = "http://myTest/address";
    TargetUrl targetUrl = new TargetUrl(urlString);

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is(urlString));
  }

  @Test
  public void regularUrlWithSuffix() throws Exception {
    String urlString = "http://myTest";
    String urlSuffix = "/address";
    TargetUrl targetUrl = new TargetUrl(urlString, urlSuffix);

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is(urlString + urlSuffix));
  }

  @Test
  public void urlWithParameters() throws Exception {
    String urlTemplateString = "http://myTest/property/:value";
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlTemplateString).setValue("value", "test").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/property/test"));
  }

  @Test
  public void urlWithParametersWithEndpointPrefix() throws Exception {
    String urlEndpointPrefix = "http://myTest";
    String urlTemplateString = "/property/:value";
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlEndpointPrefix, urlTemplateString).setValue("value", "test").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/property/test"));
  }

  @Test
  public void urlWithNotAllParametersSet() throws Exception {
    String urlTemplateString = "http://myTest/a/:valueA/b/:valueB/end";

    TargetUrl targetUrl = null;
    try {
      targetUrl = TargetUrl.fromTemplate(urlTemplateString).setValue("valueA", "123").build();

      fail("Expected to throw a TemplateNotResolvedError, but was not thrown.");

    } catch (UrlTemplateNotResolvedError e) {
      String errorMessage = e.getMessage();

      assertThat(errorMessage, containsString("valueB"));
    }
  }

  @Test
  public void urlWithMoreThanAvailableParametersSet() throws Exception {
    String urlTemplateString = "http://myTest/a/:valueA/end";
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlTemplateString).setValue("valueA", "123").setValue("valueB", "678").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/a/123/end"));
  }

}