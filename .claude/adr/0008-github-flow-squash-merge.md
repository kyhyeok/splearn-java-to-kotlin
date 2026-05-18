# ADR-0008: GitHub Flow + Squash Merge 전략

## 상태

승인됨

## 컨텍스트

프로젝트 협업 방식과 main 브랜치 이력 관리 전략이 필요하다. 장기 브랜치(Gitflow)는 머지 충돌이 잦고, 직접 main 커밋은 히스토리가 혼잡해진다.

## 결정

**GitHub Flow** + **Squash Merge** 를 채택한다.

### 브랜치 규칙

- 모든 작업은 `origin/main`에서 단기 브랜치를 분기하여 진행한다.
- 브랜치명: `<type>/<topic>` 또는 `<type>/<topic>-#<issue>`
  - type: `feature` / `fix` / `hotfix` / `refactor` / `chore` / `docs` / `test`
  - topic: 영어 kebab-case
- main 브랜치에 직접 커밋하지 않는다.

### 머지 규칙

- PR → Squash Merge. 한 PR = main에 하나의 커밋.
- PR 제목이 squash 커밋 메시지가 되므로 Conventional Commits 형식을 따른다.
- 로컬 `git merge` 금지. 머지는 GitHub UI 또는 `gh pr merge --squash` 로만.

### 예외: Starter-kit 초기화 단계

프로젝트 초기화(skeleton, 기반 설정, 마이그레이션 파일 세팅) 작업은 main에 직접 커밋할 수 있다. 기능 개발이 시작되면 브랜치 전략을 따른다.

## 결과

- main 이력: PR 단위의 squash 커밋만 쌓여 가독성이 높다.
- `git log --oneline`으로 기능 단위 추적 가능.
- rebase 없이 충돌 최소화.
