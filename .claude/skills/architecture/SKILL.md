---
name: architecture
description: splearn 프로젝트 전용 헥사고날 아키텍처 규칙. domain·application·adapter 패키지 코드를 작성·수정할 때, Use Case·포트·어댑터·도메인 모델·VO를 만들 때, 또는 architecture-reviewer 에이전트가 호출될 때 반드시 참조한다. design-principles/SKILL.md의 원칙을 이 프로젝트의 실제 패키지·클래스명에 매핑한다.
---

# 프로젝트 아키텍처 규칙 — splearn

> **이 문서가 무엇인가**: `design-principles/SKILL.md`의 SOLID·DRY·KISS·YAGNI를 이 프로젝트의 실제 패키지·네이밍에 매핑한 규칙서.
> **이 문서가 _아닌_ 것**: 원칙의 이유·충돌 해소 → `design-principles/SKILL.md`와 `references/*.md` 참조.
> **우선순위**: `CLAUDE.md` > 이 문서 > `design-principles/SKILL.md`

---

## §1 레이어 의존 방향

`HexagonalArchitectureTest` (Konsist)가 CI에서 검증한다:

```
adapter   (kimspring.splearn.adapter..)
    ↓ depends on
application (kimspring.splearn.application..)
    ↓ depends on
domain    (kimspring.splearn.domain..)
    ↑ depends on nothing
```

역방향 절대 금지. adapter → adapter 직접 호출 금지 — 반드시 application 포트 경유.

---

## §2 패키지 구조

```
kimspring.splearn
├── adapter
│   ├── webapi/              ← inbound: REST 컨트롤러(*Api), DTO
│   │   └── dto/
│   ├── out/persistence/     ← outbound: DB 어댑터(*JdbcEntity, *Adapter)
│   ├── integration/         ← outbound: 외부 서비스 어댑터
│   └── security/            ← outbound: 보안 어댑터
├── application
│   └── {ctx}/
│       ├── usecase/         ← Use Case 인터페이스 (provided 포트)
│       ├── port/            ← required 포트 인터페이스
│       └── command/         ← Command 객체
└── domain
    ├── {ctx}/               ← 엔티티, VO, 도메인 예외, 도메인 포트
    └── shared/              ← SplearnException, ErrorCode 등 공유 타입
```

---

## §3 네이밍 규칙

### Use Case 인터페이스 (`application.{ctx}.usecase`)

| 역할 | 패턴 | 예 |
|---|---|---|
| 등록/생성 | `{Ctx}Register` | `MemberRegister` |
| 수정 | `{Ctx}Modifier` | `MemberModifier` |
| 조회 | `{Ctx}Finder` | `MemberFinder` |
| 삭제 | `{Ctx}Remover` | `MemberRemover` |

### Use Case 구현체 (`application.{ctx}`)

`{Ctx}{Area}Service` — 읽기/쓰기를 분리:

- `MemberQueryService` → `@Transactional(readOnly = true)`, `MemberFinder` 구현
- `MemberModifyService` → `@Transactional @Validated`, `MemberRegister` 구현

**`*ServiceImpl` 네이밍 금지.**

### 그 외

| 역할 | 패턴 | 예 |
|---|---|---|
| REST 컨트롤러 | `*Api` | `MemberApi` |
| required 포트 | `*Repository`, `*Sender`, `*Encoder`, `*Issuer` | `MemberRepository`, `EmailSender` |
| 영속성 엔티티 | `*JdbcEntity` | `MemberJdbcEntity` |
| outbound 어댑터 | `*Adapter` | `MemberRepositoryAdapter` |
| 예외 핸들러 | `ApiControllerAdvice` | (고정) |

### `find` vs `get` 구분

- `find...` → `T?` 반환. 없으면 null, 호출자가 null 처리
- `get...` → `T` 반환. 없으면 도메인 예외(`*NotFoundException`) throw

혼용 금지 — nullable 여부가 시그니처만으로 명확해야 한다.

---

## §4 도메인 모델 패턴

```kotlin
// ✅ 불변 data class + companion object 팩토리 + 행위 메서드
data class Member(
    val id: Long,
    val email: Email,
    val status: MemberStatus,
) {
    companion object {
        fun register(cmd: RegisterMemberCommand, encoder: PasswordEncoder): Member { ... }
    }

    fun activate(): Member = copy(status = MemberStatus.ACTIVE)
    fun deactivate(): Member = copy(status = MemberStatus.DEACTIVATED)
}

// ✅ VO: init 블록에서 불변식 검증
data class Email(val value: String) {
    init { require(value.contains("@")) { "invalid email" } }
}

// ✅ 도메인 예외: SplearnException 상속, ErrorCode 보유
class MemberNotFoundException : SplearnException(ErrorCode.MEMBER_NOT_FOUND)
```

규칙:

- `var` 외부 노출 금지 — 모든 필드 `val`, 상태 변경은 `copy()` 반환
- `public constructor` 직접 노출 지양 — `companion object` 팩토리로 의미를 부여
- 도메인 클래스에 Spring 어노테이션 0 — `@Table`, `@Id` 등은 `*JdbcEntity`에만

---

## §5 Use Case 패턴

```kotlin
// ✅ 인터페이스 (application.member.usecase)
interface MemberRegister {
    fun register(cmd: RegisterMemberCommand): Member
    fun activate(memberId: Long): Member
}

// ✅ 구현체 (application.member)
@Service
@Transactional
@Validated
class MemberModifyService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailSender: EmailSender,
) : MemberRegister {
    override fun register(cmd: RegisterMemberCommand): Member {
        val member = Member.register(cmd, passwordEncoder)
        memberRepository.save(member)
        emailSender.send(member.email)
        return member
    }
}

// ✅ Command (application.member.command): Jakarta Validation 어노테이션 포함
data class RegisterMemberCommand(
    @field:Email val email: String,
    @field:Size(min = 8) val password: String,
)
```

---

## §6 포트 패턴

**required 포트** (`application.{ctx}.port`): Use Case가 의존하는 외부 추상화

```kotlin
interface MemberRepository {
    fun save(member: Member): Member
    fun findByEmail(email: Email): Member?    // nullable — 없으면 null
    fun getById(id: Long): Member            // non-null — 없으면 MemberNotFoundException
}
```

**도메인 포트** (`domain.{ctx}`): 도메인 규칙 수행에 필요한 기술 추상화

```kotlin
// domain.member — 도메인이 직접 정의, 어댑터가 구현
interface PasswordEncoder {
    fun encode(raw: String): String
    fun matches(raw: String, encoded: String): Boolean
}
```

**구현 원칙**:

- 생성자 주입 + `private val`만 사용 (`@Autowired`, `lateinit var` 금지)
- 폴백 구현체: `@Fallback @Component` (Spring 6.2+)

---

## §7 어댑터 패턴

### inbound (`adapter.webapi`)

```kotlin
// ✅ Use Case 인터페이스만 주입 — Repository·SDK 직접 주입 금지
@RestController
class MemberApi(private val register: MemberRegister, private val finder: MemberFinder) {
    // POST(생성): 201 Created + Location 헤더
    @PostMapping("/api/members")
    fun register(@RequestBody @Valid cmd: RegisterMemberCommand): ResponseEntity<MemberResponse> {
        val saved = register.register(cmd)
        val location = URI.create("/api/members/${requireNotNull(saved.id)}")
        return ResponseEntity.created(location).body(MemberResponse.from(saved))
    }

    // GET/PATCH(조회·수정): 200 OK
    @GetMapping("/api/members/{id}")
    fun find(@PathVariable id: Long): MemberResponse = MemberResponse.from(finder.find(id))
}
```

DTO: `companion object { fun from(domain): Dto }` 또는 `fun toDomain(): Domain` 변환 메서드.

### HTTP 상태 코드 기준 (ADR-0010)

| 코드 | 사용 상황 | 구현 위치 |
|---|---|---|
| **201 Created** | `POST` — 새 리소스 생성. `Location` 헤더 필수 | 컨트롤러 메서드 |
| **200 OK** | `GET`/`PATCH`/`DELETE` — 기존 리소스 조회·변경 | 컨트롤러 기본 반환 |
| **204 No Content** | 성공이지만 응답 본문 없음 | 컨트롤러 메서드 |
| **304 Not Modified** | 조건부 GET (ETag/If-None-Match). **현재 미구현 — GET 추가 시 적용** | — |
| **400 Bad Request** | Bean Validation 실패, 잘못된 입력값 | `ApiControllerAdvice` |
| **403 Forbidden** | 인증은 됐으나 권한 없음 (`AccessDeniedException`) | `ApiControllerAdvice` |
| **404 Not Found** | 리소스 없음 (`*NotFoundException`) | `ApiControllerAdvice` |
| **405 Method Not Allowed** | 지원하지 않는 HTTP 메서드 | `ApiControllerAdvice` (오버라이드) |
| **409 Conflict** | 중복 데이터 충돌 | `ApiControllerAdvice` |
| **500 Internal Server Error** | 예상치 못한 예외 | `ApiControllerAdvice` |
| **504 Gateway Timeout** | 외부 서비스 타임아웃. **현재 미구현 — 실제 HTTP 클라이언트 도입 시 적용** | — |

**Swagger 문서화 원칙:**
- 201은 해당 `@PostMapping`에 `@ApiResponse(responseCode = "201", ...)` 로 명시
- 400, 403, 405, 500은 `OpenApiConfig.globalResponseCustomizer`에서 전역 추가
- 409 등 비즈니스 충돌은 해당 엔드포인트의 `@Operation`에 `@ApiResponse`로 명시

### outbound (`adapter.out.persistence`)

```kotlin
// ✅ *Adapter: 포트 구현, Spring Data Repository 래핑
@Repository
class MemberRepositoryAdapter(private val spring: SpringMemberRepository) : MemberRepository {
    override fun save(member: Member): Member =
        spring.save(MemberJdbcEntity.from(member)).toDomain()

    override fun getById(id: Long): Member =
        spring.findById(id).orElseThrow { MemberNotFoundException() }.toDomain()
}

// ✅ *JdbcEntity: DB 표현 + 양방향 변환
@Table("member")
data class MemberJdbcEntity(@Id val id: Long?, val email: String, ...) {
    fun toDomain(): Member = Member(id!!, Email(email), ...)
    companion object { fun from(member: Member): MemberJdbcEntity = ... }
}

// Spring Data 인터페이스 — adapter 내부에만 노출, domain·application에 노출 금지
interface SpringMemberRepository : CrudRepository<MemberJdbcEntity, Long>
```

---

## §8 테스트 패턴

- **프레임워크**: Kotest `FunSpec` — JUnit5 `@Test` 방식 금지
- **Mocking**: MockK — Mockito 혼용 금지
- **픽스처**: `*Fixture` object 싱글턴, 팩토리 메서드 사용 — 하드코딩 PK 금지

```kotlin
// ✅ 픽스처
object MemberFixture {
    fun createMember(id: Long = 1L, email: String = "test@example.com"): Member = ...
    fun createRegisterMemberCommand(): RegisterMemberCommand = ...
}

// ✅ 테스트
class MemberRegisterTest : FunSpec({
    val memberRepository = mockk<MemberRepository>()
    val sut = MemberModifyService(memberRepository, ...)

    test("이메일 중복 시 DuplicateEmailException 발생") { ... }
})

// ✅ 빈 교체: @TestConfiguration
@TestConfiguration
class SplearnTestConfiguration {
    @Bean fun emailSender(): EmailSender = mockk(relaxed = true)
}
```

테스트 클래스명: `{TargetClass}Test`.

---

## §9 인프라 규칙

### Spring Data JDBC

- JPA 미사용 — `@Entity`, `@ManyToOne`, `@OneToMany`, `ddl-auto` 없음
- 어노테이션: `@Table`, `@Id`, `@Embedded(onEmpty = USE_EMPTY)` (Spring Data JDBC)
- `*JdbcEntity`는 `adapter.out.persistence`에만 — domain·application 노출 금지

### Flyway

- 마이그레이션 파일: `V{N}__{description}.sql`
- **이미 적용된 `V*` 파일 수정·삭제 절대 금지** — 변경은 새 `V{N+1}__*.sql`로
- 테스트: H2 + `MODE=MySQL;NON_KEYWORDS=VALUE` 모드로 동일 마이그레이션 실행

---

## §10 절대 금지

| 금지 항목 | 위반 시 결과 |
|---|---|
| 도메인에 Spring 어노테이션 (`@Component`, `@Service`, `@Autowired` 등) | Konsist 아키텍처 테스트 실패 |
| `@Autowired` 필드/setter 주입 (프로덕션 코드) | 생성자 주입 원칙 위반 |
| `*Api`(컨트롤러)가 `*Repository` 또는 외부 SDK 직접 주입 | Use Case 레이어 우회 |
| adapter → adapter 직접 호출 | application 포트 우회 |
| 적용된 Flyway `V*` 파일 수정·삭제 | 마이그레이션 불변 원칙 위반 |
| `*ServiceImpl` 네이밍 | 단일 행위 Use Case 분리 원칙 위반 |
| 도메인 엔티티 `var` 외부 노출 | 불변 도메인 모델 원칙 위반 |
| Mockito 사용 | Kotlin 2.x + MockK 전용 프로젝트 |
