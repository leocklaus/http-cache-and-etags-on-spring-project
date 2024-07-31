# HOW TO IMPLEMENT HTTP CACHE AND ETAGS WITH SPRING

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) 

Simple Spring project with a practical example of implementing HTTP Cache on Spring.

## About this project

This project aims to build a simple API, with only a list of products and payment types, where we can apply HTTP Cache and eTags (Shallow and Deep).

### The main goals for this project are:

* Building and API with product and payment type entities
* Creating a POST endpoint for adding new products
* Creating a POST endpoint for adding new payment types
* Creating a GET endpoint to get all products paged
* Creating a GET endpoint to get all payment types
* Adding HTTP cache to both GET endpoints
* Adding Shallow eTag to get payment types endpoint
* Adding Deep eTag to get all products paged endpoint


## HTTP Cache

### Overview

HTTP caching is a mechanism used to temporarily store (cache) copies of resources to reduce server load, bandwidth usage, and perceived lag. By caching resources, web applications can improve performance and provide a better user experience.

### Benefits of HTTP Caching

- **Reduced Latency**: Cached content can be delivered more quickly than fetching from the server.
- **Lower Bandwidth Consumption**: Reusing cached content reduces the amount of data transferred.
- **Decreased Server Load**: Caching offloads the request processing from the server, which can handle more simultaneous users.
- **Improved User Experience**: Faster load times lead to a better experience for users.

### Fresh and Stale

Stored HTTP responses have two states: fresh and stale. The fresh state usually indicates that the response is still valid and can be reused, while the stale state means that the cached response has already expired.

The criterion for determining when a response is fresh and when it is stale is age. In HTTP, age is the time elapsed since the response was generated.

### Key HTTP Cache Headers

#### Cache-Control

The `Cache-Control` header is the primary mechanism for controlling HTTP caching behavior. It can be used in both request and response headers.

- **max-age**: Specifies the maximum amount of time a resource is considered fresh. For example, `Cache-Control: max-age=3600` means the resource is fresh for 1 hour.
- **no-cache**: Forces caches to submit the request to the origin server for validation before releasing a cached copy.
- **no-store**: Prevents caching of any version of the resource.
- **public**: Indicates the response may be cached by any cache, for example reverse proxy's.
- **private**: Indicates the response is intended for a single user and should not be stored by shared caches, which mens it will be a local cache. Useful when we have an information that is related to a single user or that should not be shared with other users.

The difference between `no-cache` and `no-store` can be confusing. Basically, when we have a `no-store`, it means that we can never store the cache. With `no-cache`, we can store the cache, but we have to revalidate it on every new request, using eTags for example. 

#### Expires

The `Expires` header specifies an absolute date/time after which the response is considered stale. This header is deprecated in favor of the `Cache-Control: max-age` directive but is still widely used for backward compatibility.

Example:

`Expires: Wed, 21 Oct 2021 07:28:00 GMT`

#### ETag

The `ETag` (Entity Tag) header provides a way to validate cached responses. It is a unique identifier for a specific version of a resource.

Example:

ETag: "abc123"

#### Last-Modified

The `Last-Modified` header indicates the date and time at which the server believes the resource was last modified. It can be used in conjunction with the `If-Modified-Since` request header to make conditional requests.

Example:

Last-Modified: Tue, 15 Nov 2022 12:45:26 GMT

### Cache Validation

HTTP provides mechanisms for validating cached responses to ensure that clients receive the most current data. The two main methods are:

1. **ETag-based Validation**: The client sends an `If-None-Match` header with the ETag value. If the server's resource ETag matches, it responds with a `304 Not Modified` status, indicating the cached version is still valid.

   
Example:

    If-None-Match: "abc123"


2. **Last-Modified-based Validation**: The client sends an `If-Modified-Since` header with the last modification date. If the resource has not been modified since that date, the server responds with a `304 Not Modified` status.

Example:

    If-Modified-Since: Tue, 15 Nov 2022 12:45:26 GMT

### Implementing HTTP Cache with Spring

There are a couple ways of implementing HTTP Cache on a Spring project, but the easiest one is using the `ResponseEntity` with `cacheControl` on controllers:
Example:

    ResponseEntity
        .ok()
        .cacheControl(
            CacheControl
                .maxAge(1, TimeUnit.MINUTES)
                .cachePublic())
        .body(products);

On this example, we are setting the max age as 1 minute and also the cache as public. That means that during one minute after the first request, browser won´t need to make another request to the server, since it can use the cached version. After that minute, the cached version state will be `stale`, than we have to make a new request to server, to get a fresher version of the resource. We can also use eTags to revalidate the resource version we already have cached. If it is still valid, the server won´t need to send us a new one.

You can find the full example on ProductsController.java file on this project.

## ETags

ETags (Entity Tags) are a mechanism used by HTTP to validate the content of a resource and determine whether it has changed. They are part of the HTTP headers and play a crucial role in caching and optimizing web performance. Understanding ETags and how to use them effectively can significantly improve the efficiency of web applications.

### Benefits of ETags

- **Efficient Caching**: ETags help browsers and proxies cache content more effectively, reducing unnecessary data transfer.
- **Reduced Bandwidth Usage**: By validating cached content, ETags prevent the need to download unchanged resources.
- **Optimized Server Load**: ETags allow servers to handle conditional requests, reducing the need to generate and send full responses.
- **Enhanced User Experience**: Faster load times and reduced data usage improve the overall experience for users.

### How ETags Work

ETags are unique identifiers assigned to specific versions of a resource. When a resource is requested, the server generates an ETag for the current version of that resource and sends it in the response headers. The client then stores this ETag along with the cached resource.

For subsequent requests, the client includes the stored ETag in the `If-None-Match` header. The server compares the client's ETag with the current version:

- **If the ETag matches**: The resource has not changed, and the server responds with a `304 Not Modified` status, indicating that the cached version can be used.
- **If the ETag does not match**: The resource has changed, and the server sends the new version along with the new ETag.

### Key HTTP Headers for ETags

#### ETag

The `ETag` header contains the entity tag for the requested resource.

Example:
    
    ETag: "abc123"


#### If-None-Match

The `If-None-Match` header is sent by the client to validate its cached version of the resource against the server's current version using the ETag.

Example:

    If-None-Match: "abc123"


ETags are a powerful tool for optimizing web performance through efficient caching and content validation. By leveraging ETags, developers can reduce bandwidth usage, decrease server load, and enhance the user experience. Understanding and implementing ETags correctly is essential for building high-performance web applications.

### Shallow or Deep ETags on Spring

With Shallow ETags, the application calculates the ETag based on the response, which will save bandwidth but not server performance.

So, a request that will benefit from the Shallow ETag support will still be processed as a standard request, consume any resource that it would normally consume (database connections, etc.) and only before having its response returned back to the client will the ETag support kick in.

The difference is that we wouldn't need to return any payload in case the ETags matches and nothing have changed, we could return only the 304 status without body, saving bandwidth.

At that point the ETag will be calculated out of the Response body and set on the Resource itself; also, if the If-None-Match header was set on the Request, it will be handled as well.


A deeper implementation of the ETag mechanism could potentially provide much greater benefits – such as serving some requests from the cache and not having to perform the computation at all – but the implementation would most definitely not be as simple, nor as pluggable as the shallow approach described here. 

### How to implement Shallow ETags on Spring

As almost everything on Spring, there are a couple ways we can archive that. The simple one is just defining a @Bean `ShallowEtagHeaderFilter`, as the example bellow:

    @Bean
    ShallowEtagHeaderFilter shallowEtagHeaderFilter(){
    return new ShallowEtagHeaderFilter();
    }

Simple like that. But if you wish, you can also customize a few thing, defining another @Bean:

    @Bean
    FilterRegistrationBean filterRegistrationBean(){
    FilterRegistrationBean<ShallowEtagHeaderFilter> filter
    = new FilterRegistrationBean<>(shallowEtagHeaderFilter());
    filter.addUrlPatterns("/api/v1/**");
    filter.setName("etagFilter");
    return filter;
    }

For a full example, check WebConfig.java and PaymentController.java on this project. We are using Shallow ETags on getPaymentTypes controller. We have a 30 minutes cache defined for that. After the 30 minutes, the cache will become `stale`, as explained above, then on the next request we can use the ETags to validate fn the cached payment types are still valid. If they are, we send back a 304 response with no body. 

### How to implement Deep ETags on Spring

Deep ETags implementation is more complicated, since may need to modify the project, and find ways of validating the data without the need of processing everything just to check if nothing has changed.

On this project, we decided to apply Deep ETag on the getAllProductsPaged method (ProductsController.java file). To avoid getting all products from DB just to check if the ETag matches, we decided to create an ETag based on the last any of the products have been created. If this date changes, that means a new product has been added and the ETag is not valid anymore.

Let's check the implementation:

First we star disabling the Shallow ETags Filet on this specific controller:

    ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());

Then, we have a method to create the ETag:

    private String getETag() {
        String ETag = "0";

        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        ZoneOffset zoneOffset = zdt.getOffset();

        LocalDateTime lasCreatedProductDate = productService.getLastCreatedProduct();

        if(lasCreatedProductDate != null){
            ETag = String.valueOf(
                    lasCreatedProductDate.toEpochSecond(zoneOffset));
        }
        return ETag;
    }

That method makes a request on DB to get the last time a product was updated. Yes, we are still making a DB request, but it is a lot better than getting all the products.

After that, we can check if the ETag has changed or not:

    if(request.checkNotModified(ETag)){
            return null;
        }

If it has changed, we return null, nothing more will be processed and controller method will return status 304. If something has changed, we proceed our request normally and return the products.

For a better understanding, check ProductsController.java file.

## How to run this project locally

1. Clone this project
2. Open the folder you just created on your favorite IDE
3. Install dependencies with Maven
4. Start the project
5. Project will run on `http://localhost:8080/api/v1`
6. H2 Console is available on `http://localhost:8080/api/v1/h2-console`

## Documentation

This project base path is `/api/v1`, which means it will run on `http://localhost:8080/api/v1`

### Endpoints

#### /products
* GET: return all products paged
* POST: add a new product

```
{
    "name": "product name",
    "price": 150
}
```

#### /payment
* GET: get all payment types
* POST: add a new payment type

```
{
    "name": "payment type name"
}
```

## Technologies

* Java 21 and Spring Boot 3.3.2
* Spring WEB and Spring Data JPA
* Lombok
* H2 for the DB

## Found a bug or want to contribute to this project?

If you've found a bug, make sure you open an Issue on this project repository. Also, all users are welcome to submit pull requests, but remember to mention on the PR which Issue are you fixing on it.



