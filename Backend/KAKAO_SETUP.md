# 카카오 소셜 로그인 설정 가이드 (간단 버전)

## 필수 설정 사항

### 1. 카카오 개발자 콘솔 설정

1. [카카오 개발자 콘솔](https://developers.kakao.com) 접속
2. 애플리케이션 추가
3. **플랫폼 설정**:
   - Web 플랫폼 추가
   - 사이트 도메인: `http://localhost:8080`
4. **카카오 로그인 활성화**:
   - Redirect URI: `http://localhost:8080/login/oauth2/code/kakao`
5. **동의항목 설정**:
   - 닉네임 (필수)
   - 카카오계정(이메일) (선택 또는 필수)
6. **REST API 키 확인**:
   - 앱 설정 > 앱 키에서 REST API 키 확인
   - 보안 > Client Secret 코드 발급

### 2. application.properties 설정

`src/main/resources/application.properties` 파일에서 다음 두 값을 반드시 변경하세요:

```properties
# 이 부분을 카카오 개발자 콘솔에서 발급받은 값으로 변경하세요
spring.security.oauth2.client.registration.kakao.client-id=YOUR_KAKAO_CLIENT_ID
spring.security.oauth2.client.registration.kakao.client-secret=YOUR_KAKAO_CLIENT_SECRET

# 프론트엔드 URL로 변경하세요 (예: http://localhost:3000)
app.oauth2.redirect-uri=http://localhost:3000
```

### 3. 로그인 테스트

1. 애플리케이션 실행: `./gradlew bootRun`
2. 브라우저에서 접속: `http://localhost:8080/oauth2/authorization/kakao`
3. 카카오 로그인 진행
4. 로그인 성공 후 프론트엔드로 리다이렉트됨

### 4. 사용자 정보 조회 API

로그인 후 다음 API로 현재 사용자 정보 조회:
```
GET http://localhost:8080/api/users/me
```

## 중요 사항

- **client-id**: 카카오 개발자 콘솔의 REST API 키
- **client-secret**: 카카오 개발자 콘솔에서 발급받은 Client Secret
- **redirect-uri**: 로그인 성공 후 이동할 프론트엔드 URL

## 트러블슈팅

- **"지원하지 않는 OAuth2 제공자" 오류**: application.properties의 설정 확인
- **리다이렉트 안 됨**: 카카오 개발자 콘솔의 Redirect URI 확인
- **사용자 정보 null**: 카카오 개발자 콘솔의 동의항목 설정 확인

