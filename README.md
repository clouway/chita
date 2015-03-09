# CHITA
####Clouway Http Intelligent Transport Api

The library represent a wrapper of the java.net.HttpURLConnection class. The main goal is to represent a builder style for construction of http requests and sending different objects by POST/GET/PUT/DELETE method using Sitebricks-like Transport.

Example:

GET
```java
HttpRequest request = httpRequest(new TargetUrl("abv.bg")).build();
ChitaHttp chitaClient = new ChitaHttpClient();// in most cases chitaClient should be injected
HttpResponse response = chitaClient.execute(request);
```

POST
```java
TargetUrl targetUrl = new TargetUrl("http://telcong.com", "/test/address");
HttpRequest request = httpRequest(targetUrl).post(person).as(GsonTransport.class).build();
ChitaHttp chitaClient = new ChitaHttpClient();// in most cases chitaClient should be injected
HttpResponse response = chitaClient.execute(request);
```

PUT with parameterized url
```java
TargetUrl targetUrl = TargetUrl.urlTemplate("http://telcong.com", "/test/address/:address").setValue("address", "Veliko Turnovo").build();
HttpRequest request = httpRequest(targetUrl).put(person).as(GsonTransport.class).build();
ChitaHttp chitaClient = new ChitaHttpClient();// in most cases chitaClient should be injected
HttpResponse response = chitaClient.execute(request);
```

DELETE with parameterized url
```java
TargetUrl targetUrl = TargetUrl.urlTemplate("http://telcong.com", "/test/device/:deviceId").setValue("deviceId", "12345").build();
HttpRequest request = httpRequest(targetUrl).delete().build();
ChitaHttp chitaClient = new ChitaHttpClient();// in most cases chitaClient should be injected
HttpResponse response = chitaClient.execute(request);
```


Also there is a way to read an object from the response or the whole response as byte array.

```java
Result reply = response.read(Result.class).as(GsonTransport.class);
...
byte[] reply = response.readBytes();
```


##### The api works successfully with http and https, also is fully compatible with the Google App Engine platform.