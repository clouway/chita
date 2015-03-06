package com.clouway.chita;

import org.junit.Test;

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
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlTemplateString).set("value", "test").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/property/test"));
  }

  @Test
  public void urlWithParametersWithEndpointPrefix() throws Exception {
    String urlEndpointPrefix = "http://myTest";
    String urlTemplateString = "/property/:value";
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlEndpointPrefix, urlTemplateString).set("value", "test").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/property/test"));
  }

  @Test
  public void urlWithNotAllParametersSet() throws Exception {
    String urlTemplateString = "http://myTest/a/:valueA/b/:valueB/end";
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlTemplateString).set("valueA", "123").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/a/123/b/:valueB/end"));
  }

  @Test
  public void urlWithMoreThanAvailableParametersSet() throws Exception {
    String urlTemplateString = "http://myTest/a/:valueA/end";
    TargetUrl targetUrl = TargetUrl.fromTemplate(urlTemplateString).set("valueA", "123").set("valueB", "678").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/a/123/end"));
  }

}