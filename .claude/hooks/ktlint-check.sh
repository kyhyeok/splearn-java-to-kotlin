#!/bin/sh
cd "$(dirname "$0")/../.."
output=$(./gradlew ktlintCheck --daemon -q 2>&1)
status=$?
if [ $status -ne 0 ]; then
    echo "ktlint 위반이 있습니다. 수정 후 완료하세요:" >&2
    printf '%s\n' "$output" >&2
    exit 2
fi
