# Spring Security Microservice using JWT and MySQL

### Background

This is an open source, production-ready application that provides role-based user
authentication. The application uses Spring Security in combination with JWT to
authenticate users.

The application is ready to be used for your security and can be extended easily.

### Pre-requisite

The application requires the below to tools installed to run:
- Java 8
- Maven
- MySQL

### Requests / Endpoints

The application exposes various endpoints. A postman collection is added in the project within `postman-collection.json` file.

Here is the list of all the endpoints:

1. Register User (POST)

URI: `/auth/register`
```
{
    "username" : "thegeekyasian",
    "password" : "password",
    "confirm_password" : "password"
}
```

2. Login User (POST)

URI: `/auth/login`
```
{
    "username" : "thegeekyasian",
    "password" : "password"
}
```

3. Refresh Token (POST)

URI: `/auth/refresh`
```
{
   "refresh_token" : "REFRESH_TOKEN_HERE"
}
```

4. Change Password (POST)

URI: `/users/change-password`

Headers: `Authorization: Bearer AUTH_TOKEN_HERE`
```
{
    "password" : "current_password",
    "new_password" : "updated_password",
    "confirm_new_password" : "updated_password"
}
```

5. Get Current User (GET)

URI: `/users/me`

Headers: `Authorization: Bearer AUTH_TOKEN_HERE`

### Help

For any questions, assistance or to report any issues, please reach out to hello@thegeekyasian.com