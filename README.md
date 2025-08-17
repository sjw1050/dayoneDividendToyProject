## 실전배당금 토이프로젝트

## 회원가입 API ##

#### POST http://localhost:8080/auth/signup

    ```
    Content-Type: application/json
    
    {
      "username" : "test1",
      "password" : "test123!@#",
      "roles"    : ["ROLE_READ", "ROLE_WRITE"]
    }
    ```

    ```
    response
    {
      "id": 유저번호,
      "username": "사용자 id",
      "password": "사용자의 비밀번호",
      "roles": [
        "ROLE_READ",
        "ROLE_WRITE"
      ],
      "enabled": false,
      "authorities": [
        {
          "authority": "ROLE_READ"
        },
        {
          "authority": "ROLE_WRITE"
        }
      ],
      "accountNonLocked": false,
      "credentialsNonExpired": false,
      "accountNonExpired": false
    }
    ```

## 회원 로그인 API ## 

#### POST http://localhost:8080/auth/signin

```
Content-Type: application/json

{
"username" : "로그인할 유저 ID",
"password" : "유저 비밓번호"
}
```

```
response
    loginUserKey 
```

