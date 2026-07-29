#!/bin/sh
cd "$(dirname "$0")/../.."

# Gradle 8.14.3은 JDK 25에서 build.gradle.kts 컴파일에 실패한다. 데몬 JVM만 21로 낮춘다 (컴파일은 jvmToolchain(25) 사용).
if [ -z "$JAVA_HOME" ] && [ -x /usr/libexec/java_home ]; then
    JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null) && export JAVA_HOME
fi

output=$(./gradlew ktlintCheck --daemon -q 2>&1)
status=$?
if [ $status -ne 0 ]; then
    echo "ktlintCheck 실패. 위반 내용 또는 빌드 오류를 확인하고 수정 후 완료하세요:" >&2
    printf '%s\n' "$output" >&2
    exit 2
fi
