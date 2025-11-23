# 카카오 소셜 로그인 설정 가이드

## 1. 카카오 개발자 콘솔 설정

### 1.1 애플리케이션 등록
1. [카카오 개발자 콘솔](https://developers.kakao.com)에 접속
2. 내 애플리케이션 > 애플리케이션 추가하기
3. 앱 이름 입력 후 저장

### 1.2 플랫폼 설정
1. 앱 설정 > 플랫폼에서 플랫폼 추가
2. Web 플랫폼 추가
   - 사이트 도메인: `http://localhost:8080` (개발용)
   - 운영 환경에서는 실제 도메인으로 변경

### 1.3 카카오 로그인 활성화
1. 제품 설정 > 카카오 로그인 활성화
2. Redirect URI 등록
   - `http://localhost:8080/login/oauth2/code/kakao` (개발용)
   - 운영 환경에서는 실제 도메인으로 변경

### 1.4 동의항목 설정
1. 제품 설정 > 카카오 로그인 > 동의항목
2. 필수 동의 항목:
   - 닉네임 (필수)
   - 카카오계정(이메일) (선택 또는 필수로 설정)

### 1.5 REST API 키 발급
1. 앱 설정 > 앱 키에서 확인
   - REST API 키: 이것이 `client-id` 입니다
   - Client Secret: 보안 > Client Secret 코드 발급에서 발급받습니다

## 2. application.properties 설정

`src/main/resources/application.properties` 파일에서 다음 값들을 설정하세요:

```properties
# 카카오 OAuth2 설정
spring.security.oauth2.client.registration.kakao.client-id=YOUR_KAKAO_CLIENT_ID
spring.security.oauth2.client.registration.kakao.client-secret=YOUR_KAKAO_CLIENT_SECRET

# 로그인 성공 후 리다이렉트 URL (프론트엔드 URL)
app.oauth2.redirect-uri=http://localhost:3000
```

### 설정 값 설명:
- `YOUR_KAKAO_CLIENT_ID`: 카카오 개발자 콘솔에서 발급받은 REST API 키
- `YOUR_KAKAO_CLIENT_SECRET`: 카카오 개발자 콘솔에서 발급받은 Client Secret
- `app.oauth2.redirect-uri`: 로그인 성공 후 이동할 프론트엔드 URL

## 3. 프로젝트 실행

### 3.1 의존성 설치
```bash
./gradlew build
```

### 3.2 애플리케이션 실행
```bash
./gradlew bootRun
```

또는 IDE에서 `BackendApplication.java`를 실행

## 4. 로그인 테스트

### 4.1 로그인 URL
브라우저에서 다음 URL로 접속:
```
http://localhost:8080/oauth2/authorization/kakao
```

또는 프론트엔드에서 이 URL로 리다이렉트

### 4.2 로그인 성공 후
- 로그인 성공 시 `app.oauth2.redirect-uri`로 리다이렉트됩니다
- 사용자 정보는 DB에 자동으로 저장됩니다

### 4.3 현재 사용자 정보 조회
로그인 후 다음 API로 현재 사용자 정보를 조회할 수 있습니다:
```
GET http://localhost:8080/api/users/me
```

## 5. 데이터베이스

### 5.1 개발 환경 (H2)
- H2 인메모리 데이터베이스 사용
- H2 콘솔: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (비어있음)

### 5.2 운영 환경 (MySQL)
`application.properties`에서 MySQL 설정으로 변경:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 6. 주요 파일 구조

```
src/main/java/org/example/backend/
├── Config/
│   └── SecurityConfig.java          # Spring Security 설정
├── Oauth/
│   ├── CustomOAuth2UserService.java # OAuth2 사용자 정보 처리
│   ├── OAuth2SuccessHandler.java    # 로그인 성공 핸들러
│   └── userinfo/
│       ├── OAuth2UserInfo.java      # OAuth2 사용자 정보 추상 클래스
│       └── KakaoUserInfo.java       # 카카오 사용자 정보 처리
└── User/
    ├── Controller/
    │   └── UserController.java      # 사용자 API 컨트롤러
    ├── Domain/
    │   ├── User.java                # 사용자 엔티티
    │   └── Role.java                # 사용자 권한 enum
    ├── Dto/
    │   └── UserResponseDto.java     # 사용자 정보 응답 DTO
    └── Repository/
        └── UserRepository.java      # 사용자 리포지토리
```

## 7. 트러블슈팅

### 7.1 "지원하지 않는 OAuth2 제공자" 오류
- `application.properties`의 `registration.kakao` 설정 확인
- 카카오 개발자 콘솔에서 Redirect URI 확인

### 7.2 로그인 후 리다이렉트가 안 됨
- `app.oauth2.redirect-uri` 설정 확인
- CORS 설정 확인 (프론트엔드와 백엔드 도메인이 다른 경우)

### 7.3 사용자 정보가 null
- 카카오 개발자 콘솔에서 동의항목 설정 확인
- 사용자가 동의하지 않은 항목은 null일 수 있음

## 8. 다음 단계

### 8.1 JWT 토큰 추가
현재는 세션 기반 인증을 사용하고 있습니다. JWT 토큰을 추가하려면:
1. JWT 라이브러리 추가 (예: `jjwt`)
2. `OAuth2SuccessHandler`에서 JWT 토큰 생성
3. 토큰을 쿠키나 응답에 포함

### 8.2 다른 소셜 로그인 추가
네이버, 구글 등 다른 소셜 로그인을 추가하려면:
1. `OAuth2UserInfo`를 상속받는 새로운 클래스 생성 (예: `NaverUserInfo`)
2. `CustomOAuth2UserService`의 `getOAuth2UserInfo` 메서드에 추가
3. `application.properties`에 해당 소셜 로그인 설정 추가

### 8.3 로그아웃 기능 추가
- 로그아웃 엔드포인트 추가
- 세션 무효화
- 카카오 로그아웃 API 호출 (선택사항)




