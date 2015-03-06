package com.clouway.chita;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TargetUrl {
  private final String url;
  private String suffix;

  public static Builder fromTemplate(String urlTemplate) {
    return new Builder(urlTemplate);
  }

  public static Builder fromTemplate(String endpointDomain, String urlTemplate) {
    return new Builder(endpointDomain, urlTemplate);
  }

  public static class Builder {

    private String urlTemplate;
    private String endpointDomain;
    private Map<String, String> parameterValues = new Hashtable<String, String>();

    public Builder(String urlTemplate) {
      this.urlTemplate = urlTemplate;
    }

    public Builder(String endpointDomain, String urlTemplate) {
      this.endpointDomain = endpointDomain;
      this.urlTemplate = urlTemplate;
    }

    public Builder setValue(String parameterName, String value) {
      parameterValues.put(parameterName, value);
      return this;
    }

    public TargetUrl build() {

      Pattern parameterPattern = Pattern.compile("[:]([^/]+)");
      Matcher matcher = parameterPattern.matcher(urlTemplate);

      StringBuffer urlString = new StringBuffer();

      int beginPosition = 0;

      // Replace matched parameters with their values
      while (matcher.find()) {
        String tokenName = matcher.group(1);

        urlString.append(urlTemplate.substring(beginPosition, matcher.start()));

        if (parameterValues.containsKey(tokenName)) {
          urlString.append(parameterValues.get(tokenName));

        } else {
          throw new UrlTemplateNotResolvedError(String.format("Value for parameter %s was not set.", tokenName));
        }
        beginPosition = matcher.end();
      }

      urlString.append(urlTemplate.substring(beginPosition, urlTemplate.length()));

      if (Strings.isNullOrEmpty(endpointDomain)) {
        return new TargetUrl(urlString.toString());
      }

      return new TargetUrl(endpointDomain, urlString.toString());
    }
  }

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
