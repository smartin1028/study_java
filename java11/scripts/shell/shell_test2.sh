#!/bin/bash

# 고급 환경 변수 관리 스크립트

function add_env_var {
    clear
    echo "환경 변수 추가"
    echo "--------------"

    while true; do
        read -p "추가할 환경 변수 이름을 입력하세요 (또는 'exit'로 종료): " var_name

        if [ "$var_name" = "exit" ]; then
            return
        fi

        if [ -z "$var_name" ]; then
            echo "변수 이름은 비워둘 수 없습니다."
            continue
        fi

        read -p "$var_name의 값을 입력하세요: " var_value

        # 현재 세션에 설정
        export $var_name="$var_value"
        echo "현재 세션에 $var_name=$var_value 설정 완료"

        # 영구 설정 확인
        read -p "영구적으로 설정하시겠습니까? (y/n): " permanent

        if [[ "$permanent" =~ ^[Yy]$ ]]; then
            select_config_file

            if [ -f "$config_file" ]; then
                # 기존 설정 제거 (이미 존재하는 경우)
                sed -i "/export $var_name=/d" "$config_file"

                # 새 설정 추가
                echo "export $var_name=\\"$var_value\\"" >> "$config_file"
                echo "$config_file 파일에 환경 변수가 추가되었습니다."
                echo "변경 사항을 적용하려면 'source $config_file'을 실행하세요."
            else
                echo "선택한 설정 파일을 찾을 수 없습니다."
            fi
        fi

        read -p "계속해서 다른 변수를 추가하시겠습니까? (y/n): " continue_add
        if [[ ! "$continue_add" =~ ^[Yy]$ ]]; then
            break
        fi
    done
}

function select_config_file {
    echo "설정 파일 선택:"
    echo "1) ~/.bashrc (기본)"
    echo "2) ~/.bash_profile"
    echo "3) ~/.profile"
    echo "4) ~/.zshrc (zsh 사용자)"
    echo "5) 직접 입력"

    read -p "선택 (1-5): " choice

    case $choice in
        1) config_file=~/.bashrc ;;
        2) config_file=~/.bash_profile ;;
        3) config_file=~/.profile ;;
        4) config_file=~/.zshrc ;;
        5) read -p "설정 파일 경로를 입력하세요: " config_file ;;
        *) config_file=~/.bashrc ;;
    esac
}

function show_current_vars {
    clear
    echo "현재 설정된 환경 변수:"
    echo "---------------------"
    env | sort | less
}

function main_menu {
    clear
    echo "환경 변수 관리 프로그램"
    echo "----------------------"
    echo "1. 환경 변수 추가"
    echo "2. 현재 환경 변수 보기"
    echo "3. 종료"
    echo ""

    read -p "메뉴 선택 (1-3): " choice

    case $choice in
        1) add_env_var ;;
        2) show_current_vars ;;
        3) exit 0 ;;
        *) echo "잘못된 선택입니다. 다시 시도하세요." ;;
    esac

    read -p "계속하려면 아무 키나 누르세요..."
    main_menu
}

# 프로그램 시작
main_menu
