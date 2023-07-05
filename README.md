# Spring Security OAuth

A minimal sample setup that actually [adheres to the protocol](https://datatracker.ietf.org/doc/html/rfc6749).

## About

This is a fork of [Baeldungs official Spring-Security template](https://github.com/Baeldung/spring-security-oauth/tree/master/oauth-authorization-server), reduced to the "oauth-authorization-server" project.  
For the full instructions, see: [Baeldung.com](https://www.baeldung.com/spring-security-oauth-auth-server)

### The OAuth2 Protocol 

The OAuth2 protocol is an industrial standard to allow access of REST resources through a third
party service, on behalf of their owner. The purpose is to strip the need for credential sharing, by
means of impersonation via cryptographic tokens.
The key entities in any OAuth2 interplay are three RESTful services:

* A **Resource Server**: It offers a resource, belonging to a *Resource Owner*. The latter is
  usually a biological or legal person, not an actually service.
* A **Client**: It needs to access a protected resource of the *Resource Server*, on behalf of the
  *Resource Owner*.
* An **Authorization Server**: It is the center part of the OAuth2 protocol and provides secure
  tokens that allow impersonation of the *Resource Owner* by the *Proxy Service*, without credential
  sharing.

> Note that depending on the protocol variant, the **Authorization Server** may be replaced by an
> existing entitiy e.g. an Authorization server provider by Google, Spotify, etc...*

This repository hosts sample implementations of the three services, that allow a play-though of the
protocol. There is no implementation of the **Rersource Owner**, which is the human player in the
OAuth2 dance.

### Services

This repository reflects the [standard protocol entities](#about) as follows:

* A [simple **Resource Server**](resource-server): Sample users (
  resource owners) can access this protected service to retrieve a list of articles.
* A newly coded [Time Proxy Service](Client) as OAuth2 **Client**, which attempts to access the
  ResourceServer on behalf of the user and therefore needs to be granted access. Permission to access the protected time resource is obtained, using the OAuth2
  Protocol. This component likewise contains a minimal Web Frontent, to allow for interaction with
  the **Resource Owner** by means of a interpreting user-agent.
* An (almost) off-the-shelf [Authorization Service](AuthorizationServer) which keeps track of users,
  services, granted access and tokens, to allow for a secured resource access following the OAuth2
  dance.

### Communication Layout

The effective OAuth2 communication layout varries, depending on how roles are sperated or fused:

* In essence, these variants differ in *how the granted authorization* is transferred back from
  **Authorization Server** to **Client**.
* The above process of transferring the authorization is called [**Authorization Grant**](https://datatracker.ietf.org/doc/html/rfc6749#section-1.3) in protocol jargon.
* There are different **Authorization Grant** types, but here we only deal with the standard case:
    * Parties place minimal trust in one another.
    * Parties are fully separated executables (services).
* This standard type is called [**Authorization Code Grant**](https://datatracker.ietf.org/doc/html/rfc6749#section-4.1), in protocol jargon.

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
> Some documentation also create an artificial separation between **Resource Owner** and **User Agent**. It is a contrived splitup, because the latter then represents the browser sided JavaScript executions, interacted with by the **Resource Owner**. The interaction between **Resource Owner** and **User Agent** is then trivial, which is why it was excluded from above figure.


## Interest

We use this reduced repo as sample project to illustrate the code changes introduced to secure a standard spring boot REST service.
Long term goal is to replace the sample resource server by a modified, secured version of [the BookStore](https://github.com/m5c/BookStoreInternals).

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
Open [http://127.0.0.1:8080/articles](http://127.0.0.1:8080/articles)  
Use the credentials "admin", "password"