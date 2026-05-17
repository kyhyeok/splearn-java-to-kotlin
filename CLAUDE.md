# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

## 5. Minimal Comments

**Only when necessary. One line, two at most.**

- Don't restate what the code already says.
- Comment the *why*, not the *what* - and only when non-obvious.
- No section banners, no decorative dividers, no docstrings for self-explanatory functions.
- If a comment is needed to explain *what* the code does, rewrite the code instead.

## 6. Test Execution

**구현 완료 후 스스로 판단해서 테스트를 실행하고 결과를 보고한다.**

```
./gradlew test
```

- 소스 코드(도메인·애플리케이션·어댑터)를 추가하거나 수정한 경우 반드시 실행한다.
- 테스트가 실패하면 원인을 파악하고 수정한 뒤 재실행한다. 실패 상태로 작업 완료를 선언하지 않는다.
- 문서·설정 파일만 수정한 경우에는 생략 가능하다.
- `/test` 스킬은 테스트 실행 명령어가 아니라 테스트 작성 가이드라인 로드용이다.

## 7. Code Quality Checks

**소스 코드를 수정한 후에는 아래를 실행한다.**

```
./gradlew detekt   → 리포트를 읽고 주요 발견사항을 사용자에게 요약
```

- `ktlintCheck`는 Stop hook이 자동으로 실행한다. 위반이 있으면 hook이 exit 2를 반환해 Claude가 강제로 수정을 이어간다.
- `detekt`는 `ignoreFailures = true`라 빌드를 막지 않으므로 리포트를 직접 읽고 판단한다.
- 단순 문서·설정 파일만 수정한 경우에는 생략 가능하다.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.
