#!/bin/sh
cd "$(dirname "$0")/../.."

# 데몬 JVM은 gradle/gradle-daemon-jvm.properties가 JDK 25로 고정한다.
output=$(./gradlew ktlintCheck --daemon -q 2>&1)
status=$?
if [ $status -ne 0 ]; then
    echo "ktlintCheck 실패. 위반 내용 또는 빌드 오류를 확인하고 수정 후 완료하세요:" >&2
    printf '%s\n' "$output" >&2
    exit 2
fi
