#!/bin/bash
#셔뱅(shebang)

# 입력받은 서비스 이름
SERVICE_NAME=$1

# 서비스 이름에 따라 배포할 컨테이너 이름 설정
case "$SERVICE_NAME" in
  "api-gateway")
    TARGET_SERVICE="api-gateway"
    ;;
  "noti-service")
    TARGET_SERVICE="noti-service"
    ;;
  "user-service")
    TARGET_SERVICE="user-service"
    ;;
  "weather-service")
    TARGET_SERVICE="weather-service"
    ;;
  "all")
    TARGET_SERVICE="api-gateway noti-service user-service weather-service"
    ;;
  *)
    echo "Invalid service name: $SERVICE_NAME"
    exit 1
    ;;
esac

# 타겟 서비스 재배포 시작
echo "$TARGET_SERVICE Deploy..."
# docker compose 를 실행함.
# -f 옵션 : docker-compose 명령에서 사용할 compose 파일의 경로를 지정. (file)
# up : docker-compose 컨테이너를 시작하는 명령. 지정된 서비스의 컨테이너를 빌드, 생성 및 시작 (버전 최신화)
# -d : detached 모드. 컨테이너를 백그라운드에서 실행
docker compose -f /home/docker-compose.yml up -d $TARGET_SERVICE