#!/bin/bash

# 현재 세션에만 적용
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8
# 환경 변수 추가 스크립트
locale

echo "환경 변수 추가 프로그램"
echo "------------------------"

# 변수 이름 입력 받기
read -p "추가할 환경 변수 이름을 입력하세요: " var_name

# 변수 값 입력 받기
read -p "$var_name의 값을 입력하세요: " var_value

# 현재 세션에 환경 변수 설정
export $var_name="$var_value"
echo "현재 세션에 $var_name=$var_value 설정 완료"

# 영구적으로 설정할지 확인
read -p "이 환경 변수를 영구적으로 설정하시겠습니까? (y/n): " permanent

if [ "$permanent" = "y" ] || [ "$permanent" = "Y" ]; then
    # 사용자의 쉘 설정 파일에 추가
    if [ -f ~/.bashrc ]; then
        echo "export $var_name=\\"$var_value\\"" >> ~/.bashrc
        echo "~/.bashrc 파일에 환경 변수가 추가되었습니다."
        echo "변경 사항을 적용하려면 새로운 터미널을 열거나 'source ~/.bashrc'를 실행하세요."
    else
        echo "~/.bashrc 파일을 찾을 수 없습니다."
    fi
fi

echo "프로그램 종료"
