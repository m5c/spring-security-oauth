# Spring Security OAuth

A minimal sample setup that
reflects [OAuth2 servive access delecation](https://datatracker.ietf.org/doc/html/rfc6749) for
administrators and users.

## About

OAuth2 is a security protocol to delegate access for a secured REST API. This repository contains a
well documented and explained sample setup for access delegation on behalf of service administrators
and service users.

### Origins

The code in this repository is based on one
of [Baeldung's Spring-Security sample projects](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-authorization-server),
namely the "oauth-authorization-server" project. For more details on Baeldungs original tutorial,
see: [Baeldung.com](https://www.baeldung.com/spring-security-oauth-auth-server).

### OAuth2 Protocol

Purpose of OAuth2 is to authorize a third party service (named *Client*) to access secured
endpoints of an existing API (*Resource Server*).
The interest of OAuth2 is to enable a secure access, without requiring the original API user (*
*Resource Owner**) to share their credentials with the *Client*. This is achieved by means of
cryptographic tokens issued by an additional service (*Authorization Server*).

As such, the key entities in any OAuth2 interplay are the following three RESTful services:

* A **Resource Server**: Offers a resource, belonging to a *Resource Owner*. The latter is
  usually a biological or legal person.
* A **Client**: Requests to access a protected resource of the *Resource Server*, to perform actions
  on behalf of the *Resource Owner*.
* An **Authorization Server**: Center part of the OAuth2 protocol and provider of secure
  tokens to allow impersonation of the *Resource Owner* by the *Client*.

The *Authorization Server* is often a preexisting entity, e.g. a service provided by an established
tech company. For educative purposes and to maintain versatility this project brings it's own
off-the-shelf implementation.

#### Communication Layout

The effective OAuth2 communication layout varries, depending on how roles are sperated or fused:

* In essence, these variants differ in *how the granted authorization* is transferred back from
  **Authorization Server** to **Client**.
* The above process of transferring the authorization is called [**Authorization Grant
  **](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3) in protocol jargon.
* There are different **Authorization Grant** types, but here we only deal with the standard case:
    * Parties place minimal trust in one another.
    * Parties are fully separated executables (services).
* This standard type is called [**Authorization Code Grant
  **](https://datatracker.ietf.org/doc/html/rfc6749#section-4.1), in protocol jargon.

Below schema illustrates the communication flow for the standard **Authorization Code** type:

```
     +--------+                               +---------------+
     |        |--(A)- Authorization Request ->|   Resource    |
     |        |     (is a redirect to AS)     |     Owner     |
     |        |<------------------------------|               |
     |        | (B.3) RO forwards Auth. Code) +---------------+
     |        |                                  ^   ^
     |        |            (B.1) Page Forward    |   |  (B.2) RO grants auth.
     |        |             & grant form reply   v   v   & AS returns Auth. Code
     |        |                               +---------------+
     |        |--(C)-- Authorization Grant -->| Authorization |
     | Client |                               |     Server    |
     |        |<-(D)----- Access Token -------|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(E)----- Access Token ------>|    Resource   |
     |        |                               |     Server    |
     |        |<-(F)--- Protected Resource ---|               |
     +--------+                               +---------------+
```

> Note: The above layout is based on the official protocol specifiation. Additional arrows were
> added to better illustrate the *Request Reply* nature of the underlying HTTP protocol.
> Steps ```B.1-B.3``` reflect the **Authorization Code** communication layout.
> Some documentation also create an artificial separation between **Resource Owner** and **User
Agent**. It is a contrived splitup, because the latter then represents the browser sided JavaScript
> executions, interacted with by the **Resource Owner**. The interaction between **Resource Owner**
> and **User Agent** is then trivial, which is why it was excluded from above figure.


## BookStore Context

This repository reflects the [standard protocol entities](#about) as follows:

* A [simple **Resource Server**](resource-server): Sample users (
  resource owners) can access this protected service to retrieve a list of articles.
* A newly coded [Time Proxy Service](Client) as OAuth2 **Client**, which attempts to access the
  ResourceServer on behalf of the user and therefore needs to be granted access. Permission to
  access the protected time resource is obtained, using the OAuth2
  Protocol. This component likewise contains a minimal Web Frontent, to allow for interaction with
  the **Resource Owner** by means of a interpreting user-agent.
* An (almost) off-the-shelf [Authorization Service](AuthorizationServer) which keeps track of users,
  services, granted access and tokens, to allow for a secured resource access following the OAuth2
  dance.


## Interest

We use this reduced repo as sample project to illustrate the code changes introduced to secure a
standard spring boot REST service.
Long term goal is to replace the sample resource server by a modified, secured version
of [the BookStore](https://github.com/m5c/BookStoreInternals).

## Usage

Here is how to start up the service interplay and test secured resource access:

0) Add this entry to your `etc/hosts` file: `127.0.0.1 auth-server`
1) Start the Authorization Server:  
   `cd spring-authorization-server; mvn clean package spring-boot:run`
2) Start the Resource Server:  
   `cd resource-server; mvn clean package spring-boot:run`
3) Start the Client:  
   `cd client-server; mvn clean package spring-boot:run`
4) Access the client:  
   Open [http://127.0.0.1:8080/articles](http://127.0.0.1:8080/assortmentextensions)  
   Use the credentials "assortmentextender", "password"
5) Verify a new book is in the catalogue, and can be publicly accessed:  
   `curl -X GET http://127.0.0.1:8090/bookstore/isbns/3518368540`
