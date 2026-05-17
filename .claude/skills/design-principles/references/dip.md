# DIP — Dependency Inversion Principle

> **이 문서는 언제 읽나**: SKILL.md §3 결정 트리에서 "컨트롤러가 `JpaRepository` 직접 의존", "도메인이 Spring 의존", "`@Autowired` 필드 주입", "`LocalDateTime.now()` 직접 호출", "어댑터→어댑터 직접 호출" 같은 신호가 잡혔을 때, 또는 §5 신호 점검에서 DIP 위반이 의심될 때.
>
> **출처**: Robert C. Martin, *Agile Software Development: Principles, Patterns, and Practices* (2003).
>
> **SOLID 맥락**: SOLID의 5원칙은 "나쁜 설계의 3대 증상"(Rigidity / Fragility / Immobility)을 막기 위한 묶음이다. **DIP는 Immobility(부동성)를 막는다** — 저수준이 갈아치워질 때 고수준까지 함께 갈리는 것을 막는다.

---

**정전 정의** (Robert C. Martin):
> *"High-level modules should not depend on low-level modules. Both should depend on abstractions."*
> *"Abstractions should not depend on details. Details should depend on abstractions."*

**의도**: 고수준 정책(비즈니스 규칙·Use Case)이 저수준 세부(JPA, 외부 SDK, 메일 클라이언트, 시간)에 직접 의존하지 않게 한다. 둘 다 _도메인 추상화_에 의존시킨다.

## 헥사고날 해석 (이 프로젝트의 baseline — architecture/SKILL.md §1, §6)

```
adapter/inbound/web  (REST 컨트롤러 *Api)
       ↓ depends on
application/<ctx>/provided  (Use Case 인터페이스: *Register / *Modifier / ...)
       ↑ implements
application/<ctx>/<*Service>  (Use Case 구현)
       ↓ depends on
application/<ctx>/required   (포트: *Repository / *Issuer / *Sender / Clock)
       ↑ implements
adapter/outbound/<tech>      (JpaRepository 어댑터, JwtIssuer 어댑터, ...)

domain/<ctx>  (엔티티 / VO / 도메인 예외 — Spring 의존 0, JPA 어노테이션만 허용)
```

**역방향 절대 금지**. 어댑터→다른 도메인 어댑터 직접 호출도 금지 — 항상 application 포트를 거친다.

## 통과 신호

```kotlin
// ✅ 컨트롤러는 Use Case에만 의존
class MemberApi(private val register: MemberRegister) { ... }

// ✅ Use Case는 포트(추상)에만 의존
class MemberRegisterService(
    private val memberRepository: MemberRepository,  // 도메인 친화 포트
    private val clock: Clock,                        // 시간 추상
) : MemberRegister { ... }

// ✅ 포트는 application/required, 어댑터가 구현
package com.starter.application.member.required
interface MemberRepository {
    fun save(m: Member): Member
    fun findByEmail(e: Email): Member?
}

package com.starter.adapter.outbound.jpa
@Repository
class MemberRepositoryAdapter(
    private val jpa: MemberJpaRepository,
) : MemberRepository { ... }
```

## 위반 신호 — 거의 항상 🚨 높음

```kotlin
// ❌ 컨트롤러가 JpaRepository 직접 의존 — Use Case 우회
class MemberApi(private val jpa: MemberJpaRepository) { ... }

// ❌ Use Case가 JpaRepository에 직접 의존 (도메인 친화 포트 부재)
class MemberRegisterService(private val jpa: MemberJpaRepository) : MemberRegister { ... }

// ❌ 도메인이 Spring/인프라 의존
package com.starter.domain.member
@Component                                             // ❌ Spring 의존 (JPA 어노테이션만 허용)
class Member(...) {
    @Autowired lateinit var clock: Clock               // ❌ 도메인이 Spring 주입
    fun expireAt(): LocalDateTime = LocalDateTime.now() // ❌ Clock 미주입 (architecture/SKILL.md §6)
}

// ❌ 어댑터가 다른 도메인 어댑터 직접 호출 — application 우회
class MemberApi(private val orderJpa: OrderJpaRepository) { ... }

// ❌ @Autowired 필드/setter 주입
@Service
class MemberRegisterService : MemberRegister {
    @Autowired lateinit var memberRepository: MemberRepository  // 생성자 주입 우회 (architecture/SKILL.md §6)
}
```

이런 위반이 보이면: 적절한 application 포트를 통하도록 수정. 포트가 없다면 만들어야 한다 (단, 사용자 요청 범위 내일 때만). 도메인의 Spring 의존은 _즉시_ 제거 — ArchUnit `ArchitectureTest`가 막는다.

---

## 관련 원칙

- **OCP** (`references/ocp.md`) — DIP 포트 분리는 OCP의 "미리 만들기"와 혼동되기 쉽다. 포트는 _현재_ 테스트 가능성/교체 가능성 요구이므로 YAGNI 면제 대상 (SKILL.md §4).
- **YAGNI** (`references/yagni.md`) — "헥사고날 포트 분리는 YAGNI 위반 아닌가?" 자주 묻는다. 아니다 — 그것은 *현재* 요구이지 *추측* 기능이 아니다.
