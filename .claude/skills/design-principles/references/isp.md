# ISP — Interface Segregation Principle

> **이 문서는 언제 읽나**: SKILL.md §3 결정 트리에서 "`*Facade` 만능 Use Case", "`JpaRepository` 그대로 노출", "한 컨트롤러가 의존성 5개" 같은 신호가 잡혔을 때, 또는 §5 신호 점검에서 ISP 위반이 의심될 때.
>
> **출처**: Robert C. Martin, *Agile Software Development: Principles, Patterns, and Practices* (2003).
>
> **SOLID 맥락**: SOLID의 5원칙은 "나쁜 설계의 3대 증상"(Rigidity / Fragility / Immobility)을 막기 위한 묶음이다. **ISP는 Rigidity(경직성)를 막는다** — 만능 인터페이스의 한 메서드만 바뀌어도 그것을 안 쓰는 클라이언트까지 재컴파일/재배포된다.

---

**정전 정의** (Robert C. Martin):
> *"Clients should not be forced to depend upon interfaces they do not use."*
> "클라이언트는 자신이 사용하지 않는 인터페이스에 의존하도록 강요받아서는 안 된다."

**의도**: 거대한 만능 인터페이스 하나보다 _역할 단위_로 작은 인터페이스 여러 개로 쪼갠다.

## Kotlin/Spring 해석

- Use Case는 단일 행위로 쪼갠다 — `MemberFacade`(register/modify/find/remove 모두 노출) 대신 `MemberRegister` / `MemberModifier` / `MemberFinder` / `MemberRemover` (architecture/SKILL.md §3)
- 포트(`*Repository`)는 _도메인이 실제 쓰는 메서드_만 노출한다. `CrudRepository`의 모든 메서드(`findAll`, `deleteAll`, ...)가 도메인에 새지 않게 한다 (architecture/SKILL.md §6)
- 도메인 친화 인터페이스 + `JpaRepository` 구현 분리 — 도메인은 `MemberRepository.findByEmail(Email)`만 알고, `JpaRepository<MemberEntity, Long>`은 어댑터에만 있다

## 위반 신호

- 한 Use Case가 register/modify/find/remove 메서드를 모두 노출한다 (`*Facade` 안티패턴)
- 도메인 인터페이스가 `JpaRepository<T, ID>`를 그대로 상속/노출 → 호출자가 `findAll()` 같은 메서드까지 보게 됨
- 한 컨트롤러가 받는 Use Case 의존성이 5개 이상이고 핸들러마다 그중 1개만 쓴다

## 예 — Kotlin

**위반**:
```kotlin
interface MemberFacade {
    fun register(cmd: RegisterMemberCommand): Member
    fun modify(cmd: ModifyMemberCommand): Member
    fun find(id: MemberId): Member
    fun remove(id: MemberId)
    fun listAll(page: Pageable): Page<Member>
}
// 등록만 처리하는 가입 API가 modify/remove까지 의존하게 됨
```

**개선**:
```kotlin
interface MemberRegister  { operator fun invoke(cmd: RegisterMemberCommand): Member }
interface MemberModifier  { operator fun invoke(cmd: ModifyMemberCommand): Member }
interface MemberFinder    { operator fun invoke(id: MemberId): Member }
interface MemberRemover   { operator fun invoke(id: MemberId) }
```

## YAGNI와의 균형

인터페이스를 _미리_ 잘게 쪼개지 마라. 두 번째 클라이언트가 _덜 쓴다는 사실_이 드러났을 때 쪼갠다. (충돌 시 YAGNI 우선)
단, _액터별로 다른 행위_라는 사실이 처음부터 자명하면 처음부터 분리하는 것이 맞다 (CLAUDE.md의 단일 행위 규칙은 처음부터 적용 — 미래 확장이 아니라 _현재_ 액터 분리).

---

## 관련 원칙

- **SRP** (`references/srp.md`) — 같은 "분리" 개념이지만 _모듈/클래스 단위_. ISP는 인터페이스 단위.
- **LSP** (`references/lsp.md`) — 잘못 쪼갠 인터페이스가 LSP 위반(메서드 throw)을 강요한다.
- **YAGNI** (`references/yagni.md`) — 미리 쪼개지 말 것. SKILL.md §4 충돌 표 참조.
