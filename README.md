# Splearn

> 인프런 강의 [토비의 클린스프링 - 도메인모델패턴 헥사고날 part1](https://www.inflearn.com/course/%ED%86%A0%EB%B9%84-%ED%81%B4%EB%A6%B0%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8F%84%EB%A9%94%EC%9D%B8%EB%AA%A8%EB%8D%B8%ED%8C%A8%ED%84%B4-%ED%97%A5%EC%82%AC%EA%B3%A0%EB%82%A0-part1)을 기반으로 **Kotlin**으로 변환한 학습 프로젝트입니다.

**Spring + Learner = Splearn**  
스프링 프레임워크의 철학을 바탕으로 개발·운영·발전하는 온라인 학습 생태계를 목표로 하는 서비스입니다.

---

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Kotlin 2.3 / JDK 25 |
| Framework | Spring Boot 4.0 |
| Persistence | Spring Data JDBC, Flyway |
| Database | MySQL (운영), H2 (테스트) |
| Test | Kotest 6, MockK, Konsist |
| Code Quality | ktlint, detekt |
| Infra | Docker Compose |

---

## 아키텍처

**헥사고날 아키텍처(Hexagonal Architecture)** + **도메인 모델 패턴(Domain Model Pattern)**

```
외부(Actor) → Adapter → Application → Domain
```

### 계층 구조

```
kimspring.splearn
├── domain          # 핵심 비즈니스 규칙 (순수 도메인 객체)
├── application     # 유스케이스 조율
│   └── {ctx}/
│       ├── usecase/    # Use Case 인터페이스 (MemberRegister, MemberLifecycle 등)
│       ├── port/       # Required 포트 인터페이스 (MemberRepository, EmailSender 등)
│       └── command/    # 커맨드 객체 (RegisterMemberCommand 등)
└── adapter         # 외부 시스템 연동
    ├── webapi      # REST API 컨트롤러
    ├── persistence # Spring Data JDBC 리포지토리
    ├── integration # 외부 연동 (이메일 등)
    └── security    # 보안 (PasswordEncoder 등)
```

---

## 도메인 모델

현재 구현된 도메인은 **회원(Member) 애그리거트**입니다.

### 용어 사전

| 한국어 | 영어 | 설명 |
|---|---|---|
| 회원 | Member | 스프런 서비스를 이용하는 사용자 |
| 강사 | Instructor | 강의를 만들고 제공하는 회원 |
| 강의 | Course | 여러 수업으로 구성된 교육과정 |
| 수업 | Lesson | 강의를 구성하는 학습 단위 |
| 섹션 | Section | 강의를 세부 주제별로 나눈 중간 단위 |
| 수강 | Enrollment | 회원이 강의를 학습하는 것 |
| 진도 | Progress | 강의 내 학습 완료 상태 정보 |

### 회원 상태 흐름

```
PENDING(등록 대기) → ACTIVE(등록 완료) → DEACTIVATED(탈퇴)
```

### 회원 애그리거트 구성

- **Member** (Aggregate Root) — 이메일, 닉네임, 비밀번호 해시, 상태
- **MemberDetail** (Entity) — 프로필 주소, 자기 소개, 각종 일시
- **Profile** (Value Object) — 프로필 주소 (알파벳·숫자, 15자 이내, 중복 불가)
- **Email** (Value Object) — 이메일 주소
- **MemberStatus** (Enum) — `PENDING` / `ACTIVE` / `DEACTIVATED`
- **PasswordEncoder** (Domain Service) — 비밀번호 암호화·검증

---

## API

| Method | URI | 설명 | 성공 응답 |
|---|---|---|---|
| POST | `/api/members` | 회원 등록 | `201 Created` + `Location` 헤더 |
| PATCH | `/api/members/{id}/activate` | 등록 완료 | `200 OK` |
| PATCH | `/api/members/{id}/deactivate` | 탈퇴 | `200 OK` |
| PATCH | `/api/members/{id}` | 회원 정보 수정 | `200 OK` |

### 공통 오류 응답

| 코드 | 설명 |
|---|---|
| `400 Bad Request` | 입력값 오류 |
| `403 Forbidden` | 접근 권한 없음 |
| `404 Not Found` | 리소스 없음 |
| `405 Method Not Allowed` | 지원하지 않는 HTTP 메서드 |
| `409 Conflict` | 이메일/프로필 중복 |
| `500 Internal Server Error` | 서버 내부 오류 |

---

## 실행 방법

### 사전 요구 사항

- JDK 25
- Docker (Docker Compose)

### 로컬 실행

```bash
# MySQL 컨테이너 시작 (Spring Boot 실행 시 자동 시작)
docker compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

Spring Boot Docker Compose 통합을 사용하므로 `bootRun` 실행 시 MySQL 컨테이너가 자동으로 기동됩니다.

### 테스트 실행

```bash
./gradlew test
```

테스트 환경에서는 H2 인메모리 데이터베이스를 사용합니다.

### 코드 품질 검사

```bash
# ktlint 검사
./gradlew ktlintCheck

# detekt 검사
./gradlew detekt
```

---

## DB 마이그레이션

Flyway로 스키마를 관리합니다. 마이그레이션 파일은 `src/main/resources/db/migration/`에 위치합니다.

| 버전 | 내용 |
|---|---|
| V1 | 초기 스키마 생성 (`member`, `member_detail`) |
| V2 | Spring Data JDBC 집계 모델에 맞게 FK 방향 전환 |
| V3 | FK 컬럼명을 Spring Data JDBC 명명 규칙으로 변경 |
| V4 | `profile_address` 컬럼 크기 조정 (VARCHAR 15) |

---

## 원본 강의 대비 변경 사항 (Java → Kotlin)

- 도메인 객체를 `data class`로 구현하여 불변성 확보 (`copy()` 패턴 적용)
- `JPA` → `Spring Data JDBC` 로 전환
- 테스트 프레임워크를 JUnit5 + Mockito → **Kotest + MockK** 로 교체
- 아키텍처 규칙 검증을 ArchUnit → **Konsist** 로 교체
- 코드 품질 도구를 SpotBugs → **ktlint + detekt** 로 교체
