# OCP — Open/Closed Principle

> **이 문서는 언제 읽나**: SKILL.md §3 결정 트리에서 "확장 포인트", "Strategy/sealed 추가", "이미 적용된 마이그레이션 수정" 같은 신호가 잡혔을 때, 또는 §5 신호 점검에서 OCP 위반이 의심될 때.
>
> **출처**: Bertrand Meyer, *Object-Oriented Software Construction* (1988). Robert C. Martin이 SOLID로 묶으며 다듬음.
>
> **SOLID 맥락**: SOLID의 5원칙은 "나쁜 설계의 3대 증상"(Rigidity / Fragility / Immobility)을 막기 위한 묶음이다. **OCP는 Fragility(취약성)를 막는다** — 잘 돌아가던 곳을 건드려야 새 기능이 들어가는 구조라면 매번 새 버그가 생긴다.

---

**정전 정의** (Bertrand Meyer 1988, Martin이 다듬음):
> *"Software entities should be open for extension, but closed for modification."*
> "소프트웨어 개체는 확장에는 열려 있어야 하고, 수정에는 닫혀 있어야 한다."

**의도**: 새 동작을 추가할 때 _이미 잘 동작하는 코드_를 건드리지 않고 새 코드를 _추가_하는 방식으로 구현하라. 기존 코드 수정은 회귀(regression) 위험을 만든다.

## 통과 신호

- 새 동작 추가가 _기존 코드 수정_ 없이 _새 파일/클래스 추가_만으로 가능
- Spring 확장 포인트(`BeanPostProcessor`, `@Order`, `HandlerInterceptor`, `ControllerAdvice`, OAuth2 `JwtAuthenticationConverter` 등)를 사용한다
- 새 마이그레이션은 `V*.sql`을 _추가_만 한다 — 이미 적용된 파일은 절대 건드리지 않는다 (architecture/SKILL.md §9, ADR-0009)
- 횡단 관심사(캐싱·로깅·재시도)는 같은 포트의 _데코레이터로 합성_한다 (CLAUDE.md §4)
- 분기 3개 이상은 `sealed interface` + `data class`로 강제한다 — `else` 없이 컴파일러가 누락을 막는다

## 위반 신호

- 새 케이스마다 기존 함수의 `when`/`if-else`에 분기를 추가하고 `else`에 throw를 넣어둔다 → sealed로 강제하지 않은 코드
- 이미 적용된 Flyway `V*.sql`을 _수정·삭제_한다 (ADR-0009 위반)
- openapi-generator 등 생성 코드 산출물을 직접 편집한다
- 한 포트 구현을 직접 수정해서 캐싱을 끼워 넣는다 (데코레이터로 합성하지 않고)

## YAGNI와의 함정 — 가장 자주 잘못 적용되는 부분

OCP는 _확장 포인트를 미리 만들어두라_는 뜻이 **아니다**. "미래에 다른 PG/SMS 게이트웨이가 올 수 있으니 Strategy 패턴으로 추상화하자" 같은 추측은 YAGNI 위반이다.

**규칙**: 확장 포인트는 _두 번째 사용처가 등장할 때_ 만든다. 한 가지 동작만 있는데 추상 인터페이스부터 짜지 마라. (단, 외부 자원에 대한 _포트 분리_는 다르다 — 그건 DIP 차원의 _현재_ 요구이지 OCP 차원의 미래 확장이 아니다.)

---

## 관련 원칙

- **YAGNI** (`references/yagni.md`) — OCP의 가장 흔한 오용은 "미리 확장 포인트 만들기". 충돌 시 YAGNI 우선 (SKILL.md §4).
- **DIP** (`references/dip.md`) — 포트 분리는 OCP가 아닌 DIP로 정당화된다.
