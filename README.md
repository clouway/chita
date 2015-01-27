# CHITA
####Clouway Http Intelligent Transport Api

The library represent a wrapper of the java.net.HttpURLConnection class. The main goal is to represent a builder style for construction of http requests and sending different objects by POST method using sitebricks Transport.

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

Also there is a way to read an object from the response or the whole response as byte array.

```java
Result reply = response.read(Result.class).as(GsonTransport.class);
...
byte[] reply = response.readBytes();
```
Here the response is using sitebricks Transport for deserializing the result object.


##### The api works successfully with http and https, also is fully compatible with the Google App Engine platform.