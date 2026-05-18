# ADR-0010: HTTP 상태 코드 사용 기준

## 상태

승인됨

## 컨텍스트

REST API에서 HTTP 상태 코드를 무분별하게 200으로 통일하면 클라이언트가 응답의 의미를 추론해야 한다.
RFC 7231(HTTP/1.1 Semantics)과 Roy Fielding의 REST 논문은 상태 코드를 응답 의미의 일부로 명시한다.

## 결정

아래 기준에 따라 HTTP 상태 코드를 사용한다.

### 성공 계열 (2xx)

| 코드 | 사용 상황 | 예시 |
|---|---|---|
| **200 OK** | 조회, 수정, 삭제 성공 (기존 리소스 변경) | GET/PATCH/DELETE 전반 |
| **201 Created** | 새 리소스 생성 성공 + `Location` 헤더 필수 | `POST /api/members` |
| **204 No Content** | 성공이지만 응답 본문 없음 | 삭제 후 반환할 데이터 없을 때 |

**201 규칙:** `ResponseEntity.created(URI.create("/api/{resource}/${saved.id}")).body(...)` 형태로 반환한다.

### 리다이렉션 계열 (3xx)

| 코드 | 사용 상황 |
|---|---|
| **304 Not Modified** | GET에 `ETag`/`If-None-Match` 또는 `If-Modified-Since` 조건부 요청 시 캐시가 유효한 경우. **현재 GET 엔드포인트 없음 — GET 추가 시 조건부 GET 패턴을 함께 구현한다.** |

### 클라이언트 오류 (4xx)

| 코드 | ErrorCode | 사용 상황 |
|---|---|---|
| **400 Bad Request** | `INVALID_INPUT` | Bean Validation 실패, 잘못된 입력 |
| **403 Forbidden** | `FORBIDDEN` | 인증은 됐으나 권한 없음. Spring Security `AccessDeniedException` |
| **404 Not Found** | `MEMBER_NOT_FOUND` 등 | 리소스 없음 (`*NotFoundException`) |
| **405 Method Not Allowed** | `METHOD_NOT_ALLOWED` | 지원하지 않는 HTTP 메서드 |
| **409 Conflict** | `DUPLICATE_EMAIL` 등 | 중복 데이터 충돌 |

**401 vs 403 구분:**
- 401 Unauthorized: 인증 정보 없음/만료 → Spring Security `BearerTokenAuthenticationEntryPoint`가 처리
- 403 Forbidden: 인증은 됐으나 권한 없음 → `ApiControllerAdvice.handleAccessDenied` 또는 `BearerTokenAccessDeniedHandler`가 처리

> **주의:** Spring Security 필터 체인에서 발생하는 401/403은 `@ControllerAdvice`에 도달하지 않는다.
> 인가 엔드포인트 추가 시 `SecurityConfig`에 `exceptionHandling { accessDeniedHandler = ... }` 를 구성한다.

### 서버 오류 (5xx)

| 코드 | ErrorCode | 사용 상황 |
|---|---|---|
| **500 Internal Server Error** | `INTERNAL_ERROR` | 예상치 못한 예외 |
| **504 Gateway Timeout** | `GATEWAY_TIMEOUT` | 외부 서비스(이메일, 결제 등) 응답 시간 초과. **현재 DummyEmailSender라 미발생 — 실제 외부 HTTP 클라이언트 도입 시 `SocketTimeoutException` 핸들러를 추가한다.** |

## 구현 방식

```kotlin
// 201 Created + Location 헤더
@PostMapping("/api/members")
fun register(...): ResponseEntity<MemberRegisterResponse> {
    val member = memberRegister.register(request)
    val location = URI.create("/api/members/${requireNotNull(member.id)}")
    return ResponseEntity.created(location).body(MemberRegisterResponse.of(member))
}

// 405 — ApiControllerAdvice에서 ResponseEntityExceptionHandler 오버라이드
override fun handleHttpRequestMethodNotSupported(...): ResponseEntity<Any> {
    return ResponseEntity(ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED)
}

// 403 — 컨트롤러 내부에서 명시적으로 던진 AccessDeniedException 처리
@ExceptionHandler(AccessDeniedException::class)
fun handleAccessDenied(...): ResponseEntity<ErrorResponse> { ... }
```

## Swagger 문서화 원칙

- **201**은 해당 `@PostMapping` 메서드에 `@ApiResponse(responseCode = "201", ...)` 로 명시한다.
- **400, 403, 405, 500**은 `OpenApiConfig.globalResponseCustomizer`에서 전역으로 추가한다.
- **409** 등 비즈니스 규칙 충돌은 해당 엔드포인트의 `@Operation` 내 `@ApiResponse`로 명시한다.

## 결과

- 클라이언트가 상태 코드만으로 응답 의미를 파악할 수 있다.
- 새 리소스 생성 시 `Location` 헤더로 바로 조회 URI를 얻을 수 있다.
- 추후 GET 엔드포인트 추가 시 304, 504 대응 패턴이 문서화되어 있다.
