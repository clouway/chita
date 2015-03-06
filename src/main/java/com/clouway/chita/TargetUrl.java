package com.clouway.chita;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TargetUrl {
  private final String url;
  private String suffix;
  private List<String> queryParametersNames = new ArrayList<String>();
  private List<String> queryParametersValues = new ArrayList<String>();

  public static Builder urlTemplate(String urlTemplate) {
    return new Builder(urlTemplate);
  }

  public static Builder urlTemplate(String endpointDomain, String urlTemplate) {
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

      if (Strings.isNullOrEmpty(urlTemplate)) {
        return new TargetUrl(urlTemplate);
      }

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

  public TargetUrl addParameter(String parameterName, String value) {
    queryParametersNames.add(parameterName);
    queryParametersValues.add(value);

    return this;
  }

  public Optional<String> getValue() {

    if (isAvailable()) {

      String value = url;
      if (!Strings.isNullOrEmpty(suffix)) {
        value = value + suffix;
      }

      return Optional.of(appendQueryParameters(value));
    }

    return Optional.absent();
  }

  public boolean isAvailable() {
    return !Strings.isNullOrEmpty(url);
  }

  private String appendQueryParameters(String urlString) {

    if (queryParametersNames.size() < 1) {
      return urlString;
    }

    StringBuffer parametersString = new StringBuffer();

    for (int i = 0; i < queryParametersNames.size(); i++) {
      parametersString.append("&");
      parametersString.append(String.format("%s=%s", queryParametersNames.get(i), queryParametersValues.get(i)));
    }

    // Delete first "&" from parameter sequence
    parametersString.deleteCharAt(0);

    return String.format("%s?%s", urlString, parametersString.toString());
  }
}
