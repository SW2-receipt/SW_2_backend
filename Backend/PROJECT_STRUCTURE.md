# Project Structure

## Standard Spring Boot OAuth2 Social Login Structure

```
src/main/java/org/example/backend/
├── BackendApplication.java
├── config/
│   └── SecurityConfig.java          # Spring Security & OAuth2 configuration
├── auth/
│   ├── service/
│   │   └── OAuth2UserService.java   # OAuth2 user information processing service
│   ├── handler/
│   │   └── OAuth2SuccessHandler.java # OAuth2 login success handler
│   └── dto/
│       ├── OAuth2UserInfo.java      # Abstract class for OAuth2 user info
│       └── KakaoUserInfo.java       # Kakao OAuth2 user info implementation
└── User/
    ├── Controller/
    │   └── UserController.java      # User API controller
    ├── Domain/
    │   ├── User.java                # User entity
    │   └── Role.java                # User role enum
    ├── Dto/
    │   └── UserResponseDto.java     # User response DTO
    └── Repository/
        └── UserRepository.java      # User repository
```

## Key Components

### 1. OAuth2UserService (`auth/service/OAuth2UserService.java`)
- **Purpose**: Processes user information from social login providers
- **Responsibilities**:
  - Loads user information from OAuth2 provider
  - Saves/updates user information in database
  - Converts provider-specific user info to common format
  - Returns OAuth2User for Spring Security authentication

### 2. OAuth2SuccessHandler (`auth/handler/OAuth2SuccessHandler.java`)
- **Purpose**: Handles actions after successful OAuth2 login
- **Responsibilities**:
  - Redirects to frontend after successful authentication
  - Can be extended to generate JWT tokens

### 3. OAuth2UserInfo (`auth/dto/OAuth2UserInfo.java`)
- **Purpose**: Abstract class for provider-specific user information
- **Usage**: Provides unified interface for different OAuth2 providers

### 4. KakaoUserInfo (`auth/dto/KakaoUserInfo.java`)
- **Purpose**: Parses Kakao OAuth2 user information
- **Usage**: Extracts id, email, and nickname from Kakao API response

### 5. SecurityConfig (`config/SecurityConfig.java`)
- **Purpose**: Spring Security configuration
- **Responsibilities**:
  - Configures OAuth2 login
  - Sets up security filters
  - Configures public/private endpoints

## Why This Structure?

1. **Standard Naming**: Uses lowercase package names (`auth`, `config`) following Java conventions
2. **Clear Separation**: Separates concerns (service, handler, dto)
3. **Scalability**: Easy to add new OAuth2 providers (Naver, Google, etc.)
4. **Maintainability**: Clear folder structure makes it easy to find and modify code

## Adding New OAuth2 Providers

To add a new OAuth2 provider (e.g., Naver):

1. Create `NaverUserInfo.java` in `auth/dto/`
2. Extend `OAuth2UserInfo` abstract class
3. Implement `getId()`, `getEmail()`, `getName()` methods
4. Update `OAuth2UserService.getOAuth2UserInfo()` method
5. Add provider configuration in `application.properties`

## Files Removed

- `Oauth/` folder (replaced with `auth/`)
- Empty folders (Controller, Domain, Dto, Repository in Oauth)
- `CustomOAuth2UserService.java` (renamed to `OAuth2UserService.java`)

## Files Kept (All Required)

- `OAuth2UserService.java` - **Required** (processes OAuth2 user info)
- `OAuth2SuccessHandler.java` - **Required** (handles login success)
- `OAuth2UserInfo.java` - **Required** (abstract class for user info)
- `KakaoUserInfo.java` - **Required** (Kakao user info parser)

