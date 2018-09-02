### Spring session with Redis/Postgres

- Spring Boot 1.5.10 / 2.0.4
- Redis/Postgres-based spring-session-2.0.5
- Liquibase migrations
- TestContainers integ tests

#### Example

0. Prepare
 
Set docker-compose with redis/postgres
```bash
docker-compose up
```

Run app with redis/jdbc (postgres) profile:
```bash
mvn install
# redis
java -Dspring.profiles.active=redis -jar target/sessioner.jar
# postgres
java -Dspring.profiles.active=jdbc -jar target/sessioner.jar
```

1. Rq with basic auth

Use creds: admin/qwerty
```bash
curl http://localhost:8080/hello -H 'authorization:Basic YWRtaW46cXdlcnR5' -v
```

Rsp:
```bash
Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /hello HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.47.0
> Accept: */*
> authorization:Basic YWRtaW46cXdlcnR5
> 
< HTTP/1.1 200 
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Set-Cookie: SESSION=628648dd-c771-42ee-a572-27dda4183edf; Path=/; HttpOnly
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 12
< Date: Thu, 30 Aug 2018 15:42:37 GMT
< 
* Connection #0 to host localhost left intact
duck
```

Check that session is created in redis or postgres

2. Take session cookie: `SESSION=628648dd-c771-42ee-a572-27dda4183edf` and add it to request instead of basic auth:
```bash
curl http://localhost:8080/hello -H 'cookie:SESSION=80e5805a-0b83-4d9b-a472-2ef4a0a350a3' -v
```

Rsp:
```bash
*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /hello HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.47.0
> Accept: */*
> cookie:SESSION=80e5805a-0b83-4d9b-a472-2ef4a0a350a3
> 
< HTTP/1.1 200 
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 12
< Date: Thu, 30 Aug 2018 15:45:08 GMT
< 
* Connection #0 to host localhost left intact
duck
```

---

Rq with no cookie no auth (just to make sure endpoint is secured): `curl http://localhost:8080/hello`

Rsp:
```json
{
    "status":401,
    "error":"Unauthorized",
    "message":"Full authentication is required to access this resource",
    "path":"/hello"
}
```


### Notes

- spring_session table should be created manually in spring boot prior to 2.0
- table's structure is different across different spring-session versions
