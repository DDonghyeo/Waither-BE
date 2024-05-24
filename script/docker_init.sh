#!/bin/bash
#셔뱅(shebang) : 이 스크립트가 Bash Shell 에서 실행되어야 함을 나타냄.

# Docker install
#Docker 명령어가 존재하는지 확인함.
# -v 옵션 : 명령어 실행 전 명령어와 인자 출력.
# &> 명령어 출력과 오류 메세지를 리다이렉션. /dev/null 디바이스로 리다이렉션함.
# docker 명령어의 경로를 출력하고(stdout), 발생할 수 있는 오류 메세지(stderr)와 함께 /dev/null 디바이스로 리다이렉션.
# /dev/null 은 특수한 디바이스 파일. 쓰기 작업을 수행할 수 있지만 모든 데이터를 버림.
# 즉, 명령어의 출력과 오류 메세지를 모두 숨길 수 있음.
# 결론적으로 명령어의 존재 여부를 확인. 또는 명령어 실행 결괄르 사용하지 않고 성공/실패 여부만 확인하게 됨.
if ! command -v docker &> /dev/null; then
    # docker 명령어가 실패했을 경우 -> docker 가 설치되어 있지 않음. docker 설치를 진행함.
    echo "Docker is not installed..."
    echo "Docker install start..."
    # yum : Red Hat 기반의 Linux 배포판에서 사용되는 패키지 관리 도구. 업데이트.
    # 원래는 dnf 사용했지만 yum 으로 대체되고 있음.
    sudo yum update -y
    # docker 설치
    sudo yum install -y docker
    # docker 권한 설정
    sudo usermod -aG docker ec2-user
    sudo systemctl enable --now docker
    echo "Docker install complete"
else
    # Docker 명령어가 성공했을 경우. Docker 가 이미 설치됨을 출력
    echo "Docker is already installed"
fi

# Docker-compose install
# Docker-compose 설치
if ! command -v docker-compose &> /dev/null; then
    echo "Docker-compose is not installed..."
    echo "Docker-compose install start..."

    #Docker compose plugin 설치 - Amazon Linux 2023
    sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    sudo chown ec2-user:docker /var/run/docker.sock #docker-compose 접속 권한 설정 : docker socket


    docker-compose version

    echo "Docker-compose install complete!"
else
    echo "Docker-compose is already installed"
fi