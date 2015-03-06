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
  public void urlWithTemplateParameters() throws Exception {
    String urlTemplateString = "http://myTest/property/:value";
    TargetUrl targetUrl = TargetUrl.urlTemplate(urlTemplateString).setValue("value", "test").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/property/test"));
  }

  @Test
  public void urlWithTemplateParametersWithEndpointPrefix() throws Exception {
    String urlEndpointPrefix = "http://myTest";
    String urlTemplateString = "/property/:value";
    TargetUrl targetUrl = TargetUrl.urlTemplate(urlEndpointPrefix, urlTemplateString).setValue("value", "test").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/property/test"));
  }

  @Test
  public void urlWithNotAllTemplateParametersSet() throws Exception {
    String urlTemplateString = "http://myTest/a/:valueA/b/:valueB/end";

    TargetUrl targetUrl = null;
    try {
      targetUrl = TargetUrl.urlTemplate(urlTemplateString).setValue("valueA", "123").build();

      fail("Expected to throw a TemplateNotResolvedError, but was not thrown.");

    } catch (UrlTemplateNotResolvedError e) {
      String errorMessage = e.getMessage();

      assertThat(errorMessage, containsString("valueB"));
    }
  }

  @Test
  public void urlWithMoreThanAvailableTemplateParametersSet() throws Exception {
    String urlTemplateString = "http://myTest/a/:valueA/end";
    TargetUrl targetUrl = TargetUrl.urlTemplate(urlTemplateString).setValue("valueA", "123").setValue("valueB", "678").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/a/123/end"));
  }

  @Test
  public void urlWithEmptyTemplate() throws Exception {
    // Empty template string
    TargetUrl targetUrl = TargetUrl.urlTemplate("").setValue("value", "12345").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(false));

    // Null template string
    targetUrl = TargetUrl.urlTemplate(null).setValue("value", "12345").build();

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(false));
  }

  @Test
  public void urlWithAQueryParameter() throws Exception {
    String urlString = "http://myTest/page";
    TargetUrl targetUrl = new TargetUrl(urlString).addParameter("param1", "value1");

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/page?param1=value1"));
  }

  @Test
  public void urlWithManyQueryParameters() throws Exception {
    String urlString = "http://myTest/page";
    TargetUrl targetUrl = new TargetUrl(urlString)
            .addParameter("paramA", "valueA")
            .addParameter("paramB", "valueB");

    assertThat(targetUrl, is(notNullValue(TargetUrl.class)));
    assertThat(targetUrl.getValue().isPresent(), is(true));
    assertThat(targetUrl.getValue().get(), is("http://myTest/page?paramA=valueA&paramB=valueB"));
  }

}