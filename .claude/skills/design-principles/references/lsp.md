# LSP — Liskov Substitution Principle

> **이 문서는 언제 읽나**: SKILL.md §3 결정 트리에서 "다운캐스트", "`!!` 남발", "sealed 우회", "`find`/`get` 혼용" 같은 신호가 잡혔을 때, 또는 §5 신호 점검에서 LSP 위반이 의심될 때.
>
> **출처**: Barbara Liskov, OOPSLA 1987 keynote "Data abstraction and hierarchy". 후일 Liskov & Wing이 1994년 *ACM TOPLAS* 논문에서 "behavioral subtyping"으로 형식화. 아래 인용은 Robert C. Martin이 SOLID로 묶으며 단순화한 표현.
>
> **SOLID 맥락**: SOLID의 5원칙은 "나쁜 설계의 3대 증상"(Rigidity / Fragility / Immobility)을 막기 위한 묶음이다. **LSP는 Fragility(취약성)를 막는다** — 다형성을 쓰는 호출자가 _구체 타입에 따라_ 분기해야 한다면 다형성이 깨진 것이다.

---

**정전 정의** (Liskov 1987 원전을 Martin이 단순화):
> *"Subtypes must be substitutable for their base types without altering the correctness of the program."*
> "하위 타입은 상위 타입의 자리에 들어가도 프로그램의 정확성을 깨지 않아야 한다."

**의도**: 상속/구현 계약을 _문법_ 수준이 아니라 _의미·행동_ 수준에서 지키라. 시그니처가 같다고 같은 타입이 아니다.

## Kotlin/Spring 해석

- 포트 인터페이스(`*Repository` / `*Issuer` / `*Sender`)를 구현할 때, 구현체가 _상위 계약을 깨면서_ 던지는 예외 종류·전제 조건을 바꾸지 않아야 한다
- `JpaRepository`를 도메인 친화 인터페이스로 좁힐 때, 구현이 도메인 계약(`save`가 신규/갱신 모두 받는 의미)과 일치해야 한다
- Kotlin `sealed` 계층 — 호출자가 `when`으로 분기할 때 모든 분기가 _일관된 의미_를 가져야 한다 (한 분기만 throw로 막지 마라)
- 조회 메서드 네이밍은 의미와 일치해야 한다 — `find...`는 nullable, `get...`은 없으면 예외 (architecture/SKILL.md §3). 이 둘을 섞으면 호출자가 구현마다 다른 동작을 가정하게 된다

## 위반 신호

- `as` 다운캐스트 / `as? Foo ?: throw`로 sealed 계층 우회
- `!!` 남발로 nullable 계약을 무시
- 하위 타입이 상위 메서드를 `throw UnsupportedOperationException()`으로 막아버림
- 호출자가 `is` / `when (x) { is A -> ...; is B -> ... }`로 _특정 구체 타입에만_ 다른 동작을 강요당함
- `JpaRepository<T, ID>`를 인터페이스로 노출했는데 어떤 구현은 `delete`가 soft-delete이고 어떤 구현은 hard-delete (계약 모호)
- `find`/`get` 혼용 — 같은 인터페이스에서 어떤 메서드는 nullable, 어떤 메서드는 throw인데 명명 규칙이 어긋남

## 예 — Kotlin

**위반**:
```kotlin
interface PaymentGateway { fun charge(amount: Money): PaymentResult }

class PointGateway : PaymentGateway {
    override fun charge(amount: Money): PaymentResult =
        throw UnsupportedOperationException("포인트는 charge 불가") // ❌ 계약 파괴
}
```

**개선**:
```kotlin
interface PaymentGateway { fun charge(amount: Money): PaymentResult }
interface RewardGateway  { fun redeem(amount: Money): PaymentResult }

class PointGateway : RewardGateway { /* charge 계약을 약속하지 않는다 */ }
```

---

## 관련 원칙

- **ISP** (`references/isp.md`) — 잘못 쪼갠 인터페이스가 LSP 위반을 강요하는 경우가 많다. "이 메서드는 이 구현에서 의미가 없어서 throw" 패턴은 인터페이스를 더 잘게 쪼개면 사라진다.
