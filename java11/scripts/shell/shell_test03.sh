#!/bin/bash
test_var="테스트변수"
echo "변수값 출력 테스트: [$test_var]"
read -p "$test_var의 값을 입력하세요: " user_input
echo "입력값: $user_input"
