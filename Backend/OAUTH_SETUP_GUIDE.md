# OAuth2 카카오 로그인 설정 가이드

## 변경된 플로우

### 1. 로그인 시작
```
GET http://localhost:8080/oauth/kakao/login
→ 302 리다이렉트 → 카카오 인증 페이지
```

### 2. 카카오 콜백
```
GET http://localhost:8080/oauth/kakao/callback?code=...
→ 코드 수신, 토큰 교환, JWT 발급
→ 302 리다이렉트 → 프론트엔드
```

### 3. 프론트엔드 리다이렉트
```
GET http://localhost:8081/auth/kakao/callback?token=<JWT>
→ 프론트엔드에서 토큰 저장 후 /main으로 이동
```

## 설정 방법

### 1. 카카오 개발자 콘솔 설정

1. [카카오 개발자 콘솔](https://developers.kakao.com) 접속
2. 내 애플리케이션 > 앱 설정
3. **플랫폼 설정**:
   - Web 플랫폼 추가
   - 사이트 도메인: `http://localhost:8080`
4. **카카오 로그인 활성화**:
   - Redirect URI 등록: `http://localhost:8080/oauth/kakao/callback` ⚠️ **중요: 이 URL로 변경**
5. **동의항목 설정**:
   - 닉네임 (필수)
   - 카카오계정(이메일) (선택 또는 필수)

### 2. application.properties 설정

현재 설정된 값:
```properties
# 프론트엔드 기본 URL (환경변수 FRONTEND_BASE로 오버라이드 가능)
app.frontend.base=http://localhost:8081

# 카카오 OAuth2 콜백 URL
spring.security.oauth2.client.registration.kakao.redirect-uri=http://localhost:8080/oauth/kakao/callback

# JWT 설정
app.jwt.secret=your-secret-key-change-this-in-production-minimum-256-bits-required-for-security
app.jwt.expiration=86400000  # 24시간 (밀리초)
```

### 3. 환경변수 설정 (배포 시)

운영 환경에서는 환경변수로 설정:
```bash
# 프론트엔드 도메인
export FRONTEND_BASE=https://yourdomain.com

# JWT Secret (최소 32자 이상)
export APP_JWT_SECRET=your-production-secret-key-minimum-256-bits
```

또는 `application.properties`에서:
```properties
app.frontend.base=${FRONTEND_BASE:http://localhost:8081}
app.jwt.secret=${APP_JWT_SECRET:your-secret-key-change-this-in-production-minimum-256-bits-required-for-security}
```

## 엔드포인트 정리

### 공개 엔드포인트 (인증 불필요)
- `GET /oauth/kakao/login` - 카카오 로그인 시작
- `GET /oauth/kakao/callback` - 카카오 콜백 (Spring Security가 자동 처리)
- `GET /oauth2/**` - Spring Security OAuth2 내부 엔드포인트
- `GET /login/oauth2/**` - Spring Security OAuth2 내부 엔드포인트

### 인증 필요한 엔드포인트
- `GET /api/**` - 모든 API 엔드포인트

## JWT 토큰 구조

JWT 토큰에는 다음 정보가 포함됩니다:
- `sub`: User ID (데이터베이스의 사용자 ID)
- `email`: 사용자 이메일
- `provider`: OAuth2 제공자 (kakao, naver, google 등)
- `iat`: 발급 시간
- `exp`: 만료 시간

## 테스트 방법

1. **로그인 시작**:
   ```bash
   curl -L http://localhost:8080/oauth/kakao/login
   # 또는 브라우저에서 접속
   ```

2. **카카오 로그인 완료 후**:
   - 자동으로 `http://localhost:8081/auth/kakao/callback?token=<JWT>`로 리다이렉트됨
   - 프론트엔드에서 토큰을 추출하여 저장

3. **API 호출** (JWT 사용 시):
   ```bash
   curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/users/me
   ```

## 주의사항

1. **카카오 개발자 콘솔 Redirect URI**: 반드시 `http://localhost:8080/oauth/kakao/callback`로 설정
2. **JWT Secret**: 운영 환경에서는 반드시 강력한 시크릿 키 사용 (최소 32자)
3. **프론트엔드 URL**: 환경변수 `FRONTEND_BASE`로 관리하여 배포 시 쉽게 변경 가능
4. **HTTPS**: 운영 환경에서는 반드시 HTTPS 사용

## 문제 해결

### Redirect URI 불일치 오류
- 카카오 개발자 콘솔의 Redirect URI와 `application.properties`의 `redirect-uri`가 일치하는지 확인
- 정확히 `http://localhost:8080/oauth/kakao/callback`로 설정

### JWT 토큰이 생성되지 않는 경우
- `app.jwt.secret`이 설정되어 있는지 확인
- 로그에서 JWT 생성 관련 오류 확인

### 프론트엔드로 리다이렉트되지 않는 경우
- `FRONTEND_BASE` 환경변수 또는 `app.frontend.base` 설정 확인
- 로그에서 리다이렉트 URL 확인

