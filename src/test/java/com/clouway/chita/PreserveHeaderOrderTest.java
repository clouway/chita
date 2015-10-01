package com.clouway.chita;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.clouway.chita.HttpRequest.httpRequest;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;


/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class PreserveHeaderOrderTest {

  @Test
  public void happyPath() {
    HttpRequest<?> request = httpRequest(new TargetUrl("http://hostofapp.com"))
            .addProperty("1", "1")
            .addProperty("3", "3")
            .addProperty("2", "2").build();

    Map<String, String> properties = request.getProperties();
    assertThat(properties.keySet(), contains("1", "3", "2"));
  }

  @Test
  public void anotherOrdering() {
    HttpRequest<?> request = httpRequest(new TargetUrl("http://hostofapp.com"))
            .addProperty("1", "1")
            .addProperty("2", "2")
            .addProperty("3", "3").build();

    Map<String, String> properties = request.getProperties();
    assertThat(properties.keySet(), contains("1", "2", "3"));
  }

}