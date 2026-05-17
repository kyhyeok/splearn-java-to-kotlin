# SRP — Single Responsibility Principle

> **이 문서는 언제 읽나**: SKILL.md §3 결정 트리에서 "책임 분리", "한 서비스가 너무 많은 일", "`*ServiceImpl` 만능 서비스" 같은 신호가 잡혔을 때, 또는 §5 신호 점검에서 SRP 위반이 의심될 때.
>
> **출처**: Robert C. Martin, "Design Principles and Design Patterns" (2000), *Agile Software Development: Principles, Patterns, and Practices* (2003). SRP 자체는 Tom DeMarco *Structured Analysis and Systems Specification* (1979)에서 Martin이 "변경의 이유"로 재정의.
>
> **SOLID 맥락**: SOLID는 Martin이 "나쁜 설계의 3대 증상"(Rigidity / Fragility / Immobility)을 막기 위해 묶은 다섯 원칙이다. **SRP는 Rigidity(경직성)를 막는다** — 책임이 섞여 있으면 한 책임의 변경이 다른 책임의 코드를 흔든다.

---

**정전 정의** (Robert C. Martin):
> *"A class should have one, and only one, reason to change."*
> "클래스는 변경되어야 할 이유를 단 하나만 가져야 한다."

**핵심 오해 정정**: SRP는 "한 가지 일만 한다"가 아니다. **"변경의 이유가 하나"**다. 즉 _누가_ 그 모듈의 변경을 요청하는가의 관점에서 본다 (회계팀이 보내는 변경과 인사팀이 보내는 변경이 같은 클래스에 떨어진다면 SRP 위반).

## 통과 신호

- 모듈이 _하나의 액터(역할/요청자)_의 변경 요구만 받는다
- "이 파일은 무엇을 하는가?"에 한 문장으로 답할 때 "and"가 들어가지 않는다
- 변경 요구가 들어왔을 때 영향받는 파일이 1~2개로 좁혀진다
- Use Case 인터페이스가 단일 행위(`*Register` / `*Modifier` / `*Finder` / `*Remover`)로 분리되어 있다 (architecture/SKILL.md §3)

## 위반 신호

- 한 `*Service`가 fetch + 변환 + 검증 + 외부호출 + 영속화 + 이벤트 발행을 모두 한다
- `*ServiceImpl` 네이밍 — 단일 행위로 쪼개지지 않은 만능 서비스의 신호 (CLAUDE.md §3 명시 금지)
- 한 도메인 엔티티가 영속화 형식 + 비즈니스 규칙 + 직렬화 응답을 모두 책임진다 (도메인 ↔ DTO 분리 부재)
- 파일 설명에 "그리고", "또한"이 두 번 이상 등장한다
- "이 파일을 누가 바꾸나?"에 답할 때 서로 다른 팀/도메인이 두 개 이상 나온다

## 예 — Kotlin/Spring

**위반**:
```kotlin
@RestController
class MemberController(
    private val jpaRepository: MemberJpaRepository,
    private val mailSender: JavaMailSender,
    private val passwordEncoder: PasswordEncoder,
) {
    @PostMapping("/api/members")
    fun register(@RequestBody req: RegisterRequest): ResponseEntity<MemberResponse> {
        // 1) 검증
        require(req.email.contains("@")) { "invalid email" }
        // 2) 중복 확인
        if (jpaRepository.existsByEmail(req.email)) error("dup")
        // 3) 비밀번호 해싱
        val hashed = passwordEncoder.encode(req.password)
        // 4) 영속화
        val saved = jpaRepository.save(MemberEntity(req.email, hashed))
        // 5) 환영 메일
        mailSender.send(buildWelcomeMail(saved.email))
        return ResponseEntity.ok(MemberResponse.from(saved))
    }
}
```
→ HTTP 변환 + 검증 + 도메인 규칙 + 인프라(영속화) + 외부호출(메일) 5가지 액터.

**개선** (헥사고날):
```kotlin
// adapter/inbound/web
@RestController
class MemberApi(private val register: MemberRegister) {
    @PostMapping("/api/members")
    fun register(@RequestBody req: RegisterRequest): ResponseEntity<MemberResponse> =
        ResponseEntity.ok(MemberResponse.from(register(req.toCommand())))
}

// application/member/provided
interface MemberRegister { operator fun invoke(cmd: RegisterMemberCommand): Member }

// application/member
@Service
class MemberRegisterService(
    private val memberRepository: MemberRepository,    // required port
    private val passwordHasher: PasswordHasher,        // required port
    private val welcomeMailer: WelcomeMailer,          // required port
) : MemberRegister {
    override fun invoke(cmd: RegisterMemberCommand): Member {
        val member = Member.register(cmd, passwordHasher) // 도메인 규칙은 도메인 안
        memberRepository.save(member)
        welcomeMailer.send(member.email)
        return member
    }
}
```
컨트롤러는 HTTP 변환만, Use Case는 오케스트레이션만, 도메인은 규칙만, 어댑터는 인프라만 — 액터별 분리.

## 과도 적용 금지

SRP를 극단으로 밀면 _Use Case 하나당 클래스 다섯 개_가 생기는 안티패턴이 나온다. SRP는 **응집도(cohesion)**의 도구이지 _분해(decomposition)_의 강박이 아니다. 한 동사(`register`)의 책임을 다섯 클래스로 쪼개고 한 곳에서 모두 주입받아 합치고 있다면 — 분리 자체가 잘못된 추상화일 수 있다.

---

## 관련 원칙

- **ISP** (`references/isp.md`) — 같은 "분리" 개념이지만 _인터페이스 단위_. SRP는 모듈/클래스 단위.
- **KISS** (`references/kiss.md`) — 좁은 범위에서 SRP를 너무 밀면 KISS와 충돌. SKILL.md §4 충돌 표 참조.
