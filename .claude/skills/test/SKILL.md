---
name: test
description: splearn 프로젝트 테스트 작성 규칙. 테스트 코드를 새로 작성하거나 수정할 때, 어떤 계층의 테스트를 어떤 방식으로 짜야 할지 결정할 때 참조한다. Kotest FunSpec + MockK + MockMvcTester 패턴을 이 프로젝트의 실제 파일 기준으로 정의한다. "테스트", "Test", "FunSpec", "MockK", "MockMvcTester", "단위 테스트", "통합 테스트", "WebMvcTest", "Fixture", "Konsist", "SplearnTestConfiguration" 같은 키워드가 등장하거나, 새 테스트 파일·Fixture·아키텍처 테스트를 작성하거나, 소스 코드 변경 후 테스트를 추가하거나, 기존 테스트를 계층 패턴에 맞게 수정하는 작업이라면 적용 대상이다. 사용자가 명시적으로 부르지 않더라도 위 작업 중에는 이 스킬을 자동으로 적용하라.
---

# 테스트 작성 규칙 — splearn

> **이 문서가 무엇인가**: 이 프로젝트에서 테스트를 작성할 때 따라야 할 계층별 패턴, 프레임워크 사용법, 금지 사항.
> **우선순위**: `CLAUDE.md` > 이 문서 > `architecture/SKILL.md`

---

## §1 기술 스택

| 역할 | 라이브러리 | 버전 |
|---|---|---|
| 테스트 프레임워크 | Kotest `FunSpec` | 6.1.11 |
| Spring 연동 | `kotest-extensions-spring` (`SpringExtension`) | 6.1.11 |
| 모킹 | MockK | 1.14.9 |
| 아키텍처 검증 | Konsist | 0.17.3 |
| 포맷 | ktlint | 14.0.1 |
| 정적 분석 | detekt (`ignoreFailures = true`) | 2.0.0-alpha.3 |
| 테스트 DB | H2 (MySQL 호환 모드) | — |

**Testcontainers 없음** — 통합 테스트는 H2로 돌린다.

---

## §2 FunSpec 스타일 — 두 가지를 구분한다

### Spring 없는 순수 테스트: 생성자 람다

```kotlin
class ProfileTest : FunSpec({
    test("profile") { ... }
    test("profileFail") { ... }
})
```

적용 대상: domain 객체, VO, 순수 adapter (DB·HTTP 없는 것).
예: `MemberTest`, `ProfileTest`, `EmailTest`, `SecurePasswordEncoderTest`, `DummyEmailSenderTest`.

### Spring 통합 테스트: `init {}` 블록

```kotlin
class MemberRegisterTest : FunSpec() {
    @Autowired private lateinit var memberRegister: MemberRegister

    init {
        extension(SpringExtension())

        test("register") { ... }
    }
}
```

`@Autowired lateinit var`가 필요할 때만 이 형태를 쓴다.
`extension(SpringExtension())`은 `init {}` 첫 줄에 항상 선언한다.

---

## §3 테스트 계층별 패턴

### 3-1. 순수 단위 — domain / 순수 adapter

```kotlin
// 어노테이션 없음. Spring 컨텍스트 없음.
class MemberTest : FunSpec() {
    private lateinit var member: Member

    init {
        beforeEach {
            member = MemberFixture.createMember()
        }

        test("activate") {
            member = member.activate()
            member.status shouldBe MemberStatus.ACTIVE
        }
    }
}
```

- `@SpringBootTest` 절대 붙이지 않는다.
- MockK가 필요하면 `val foo = mockk<Foo>()` 지역 변수로 선언한다.

### 3-2. Repository 통합 — port 인터페이스를 실제 DB로 검증

```kotlin
@SpringBootTest
@Transactional
class MemberRepositoryTest : FunSpec() {
    @Autowired private lateinit var memberRepository: MemberRepository

    init {
        extension(SpringExtension())

        test("registerMember") { ... }
    }
}
```

- `SplearnTestConfiguration` import 불필요 — PasswordEncoder·EmailSender 없이도 동작.
- H2로 실행되므로 MySQL 전용 DDL은 쓰지 않는다.

### 3-3. Use Case 통합 — 전체 Spring 컨텍스트, 실제 DB

```kotlin
@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration::class)
class MemberRegisterTest : FunSpec() {
    @Autowired private lateinit var memberRegister: MemberRegister

    init {
        extension(SpringExtension())
        ...
    }
}
```

- `SplearnTestConfiguration`이 EmailSender·PasswordEncoder를 테스트용으로 교체한다.
- Use Case 인터페이스 타입으로 주입 (`MemberRegister`), 구현체 타입 아님.

### 3-4. Web Slice — 컨트롤러 단독, 포트는 MockK

```kotlin
@WebMvcTest(MemberApi::class)
class MemberApiWebMvcTest : FunSpec() {
    @TestConfiguration
    class Config {
        @Bean fun memberRegister(): MemberRegister = mockk()
    }

    @Autowired private lateinit var mvcTester: MockMvcTester
    @Autowired private lateinit var memberRegister: MemberRegister

    init {
        extension(SpringExtension())
        afterEach { clearAllMocks() }

        test("register") {
            every { memberRegister.register(any()) } returns MemberFixture.createMember(1L)
            assertThat(mvcTester.post().uri("/api/members")...).hasStatusOk()
            verify { memberRegister.register(any()) }
        }
    }
}
```

- `@MockBean` 금지 — `@TestConfiguration` 내부 `@Bean fun foo(): Port = mockk()` 사용.
- `afterEach { clearAllMocks() }` 항상 선언한다.
- MockMvcTester: `assertThat(mvcTester.method().uri(...)...)` — `exchange()` 없이 바로 assertThat에 전달.

### 3-5. API 통합 — 전체 Spring 컨텍스트 + MockMvcTester

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SplearnTestConfiguration::class)
class MemberApiTest : FunSpec() {
    @Autowired private lateinit var mvcTester: MockMvcTester

    init {
        extension(SpringExtension())

        test("register") {
            val result = mvcTester
                .post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .exchange()  // ← SpringBootTest에서는 exchange() 필수

            assertThat(result).hasStatusOk().bodyJson().hasPathSatisfying("$.memberId") { ... }
        }
    }
}
```

- Web Slice(`@WebMvcTest`)와 달리 `.exchange()` 호출이 필요하다.

---

## §4 MockK 패턴

```kotlin
// 기본 모킹
val memberRegister = mockk<MemberRegister>()
every { memberRegister.register(any()) } returns member
verify { memberRegister.register(request) }

// static 모킹
mockkStatic(SpringApplication::class)
every { SpringApplication.run(SplearnApplication::class.java, *anyVararg<String>()) } returns context
unmockkStatic(SpringApplication::class)

// 정리 — afterEach에서
afterEach { clearAllMocks() }
```

- Mockito, `@MockBean`, `springmockk` 사용 금지.
- `every`/`verify`는 쌍으로 선언한다 — 동작을 stubs하고 호출도 검증한다.

---

## §5 SplearnTestConfiguration

`src/test/kotlin/kimspring/splearn/SplearnTestConfiguration.kt`

Spring 컨텍스트가 필요한 테스트에서 외부 포트(EmailSender, PasswordEncoder)를 테스트용으로 교체한다.

```kotlin
@TestConfiguration
class SplearnTestConfiguration {
    @Bean fun emailSender(): EmailSender = object : EmailSender {
        override fun send(email: Email, subject: String, body: String) { /* no-op */ }
    }
    @Bean fun passwordEncoder(): PasswordEncoder = MemberFixture.createPasswordEncoder()
}
```

- Use Case 통합 / API 통합 테스트에서 `@Import(SplearnTestConfiguration::class)` 추가.
- Repository 통합 테스트에는 불필요 (EmailSender·PasswordEncoder를 직접 쓰지 않으므로).

---

## §6 Fixture 패턴

`src/test/kotlin/kimspring/splearn/domain/member/MemberFixture.kt`

```kotlin
object MemberFixture {
    fun createRegisterMemberCommand(): RegisterMemberCommand = ...
    fun createRegisterMemberCommand(email: String): RegisterMemberCommand = ...
    fun createMember(): Member = ...
    fun createMember(id: Long): Member = createMember().copy(id = id)
    fun createMember(email: String): Member = ...
    fun createPasswordEncoder(): PasswordEncoder = object : PasswordEncoder { ... }
}
```

- `object`로 선언 — state 없음, factory method만.
- 새 도메인 컨텍스트가 생기면 `{Context}Fixture.kt`를 같은 위치에 추가한다.
- Fixture의 기본값은 실제 유효한 값으로 — 검증 우회용 magic value 금지.

---

## §7 아키텍처 테스트 — Konsist

`src/test/kotlin/kimspring/splearn/HexagonalArchitectureTest.kt`

```kotlin
class HexagonalArchitectureTest : FunSpec({
    test("헥사고날 아키텍처 레이어 의존성 규칙을 준수한다") {
        Konsist.scopeFromProduction().assertArchitecture {
            val domain      = Layer("domain",      "kimspring.splearn.domain..")
            val application = Layer("application", "kimspring.splearn.application..")
            val adapter     = Layer("adapter",     "kimspring.splearn.adapter..")

            domain.dependsOnNothing()
            application.dependsOn(domain)
            adapter.dependsOn(domain, application)
        }
    }
})
```

- Spring 컨텍스트 없음, `@Transactional` 없음.
- 이 파일은 직접 수정하지 않는다 — 레이어 구조가 변하면 Layer 선언만 갱신.

---

## §8 테스트 네이밍

| 항목 | 규칙 | 예시 |
|---|---|---|
| 파일명 | `{TargetClass}Test.kt` | `MemberRegisterTest.kt` |
| 함수명 | camelCase, 동작 서술 | `register`, `duplicateEmailFail`, `activate` |
| 실패 케이스 | `{동작}Fail` 접미사 | `activateFail`, `registerFail` |
| 한국어 가능 | 복잡한 비즈니스 규칙 | `"헥사고날 아키텍처 레이어 의존성 규칙을 준수한다"` |

`*_specs.kt` 파일명 금지.

---

## §9 금지 사항

| 금지 | 대신 사용 |
|---|---|
| `@MockBean` | `@TestConfiguration` + `@Bean fun foo() = mockk()` |
| `@ExtendWith(MockitoExtension::class)` | Kotest FunSpec + MockK |
| `import org.mockito.*` | `import io.mockk.*` |
| `@AutoConfigureMockMvc` 없이 MockMvcTester 주입 | `@AutoConfigureMockMvc` 또는 `@WebMvcTest` 필수 |
| SpringBootTest에서 `.exchange()` 생략 | `.exchange()` 호출 후 `assertThat(result)` |
| `Testcontainers` 도입 | H2 사용 |
| `JUnit5 @Test`, `@BeforeEach` 어노테이션 | Kotest `test {}`, `beforeEach {}` |

---

## §10 Assertion 스타일

두 라이브러리를 **역할에 따라** 구분한다. 혼용은 허용되지만 대상이 달라야 한다.

### Kotest — 도메인 값, DB 결과, Kotlin 객체

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.assertions.throwables.shouldThrow

member.status shouldBe MemberStatus.PENDING
member.detail.registeredAt.shouldNotBeNull()
member.detail.activatedAt.shouldBeNull()

// 예외 검증 — 모든 계층에서 사용
shouldThrow<MemberNotFoundException> { memberFinder.find(9999L) }
shouldThrow<InvalidMemberStateException> { member.deactivate() }
```

### AssertJ — MockMvcTester HTTP 응답 전용

```kotlin
import org.assertj.core.api.Assertions.assertThat

// WebMvcTest: exchange() 없이 바로 assertThat에 전달
assertThat(mvcTester.post().uri("/api/members").contentType(APPLICATION_JSON).content(body))
    .hasStatus(HttpStatus.OK)

// SpringBootTest: exchange() 후 assertThat
val result = mvcTester.post().uri("/api/members").contentType(APPLICATION_JSON).content(body).exchange()
assertThat(result)
    .hasStatusOk()
    .bodyJson()
    .hasPathSatisfying("$.memberId") { assertThat(it).isNotNull() }
    .hasPathSatisfying("$.email") { assertThat(it).isEqualTo(request.email) }
```

### 두 라이브러리를 함께 쓰는 경우 — MemberApiTest 패턴

HTTP 응답은 AssertJ, 이후 DB에서 조회한 도메인 값은 Kotest.

```kotlin
// HTTP 응답 검증 → AssertJ
assertThat(result).hasStatusOk()

// DB 조회 결과 검증 → Kotest
val member = memberRepository.findByEmail(email)!!
member.status shouldBe MemberStatus.PENDING
member.detail.registeredAt.shouldNotBeNull()
```
