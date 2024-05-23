#!/bin/bash
#셔뱅(shebang)


# 타겟 서비스 재배포 시작
#echo "$TARGET_SERVICE Deploy..."
# docker compose 를 실행함.
# -f 옵션 : docker-compose 명령에서 사용할 compose 파일의 경로를 지정. (file)
# up : docker-compose 컨테이너를 시작하는 명령. 지정된 서비스의 컨테이너를 빌드, 생성 및 시작 (버전 최신화)
# -d : detached 모드. 컨테이너를 백그라운드에서 실행

echo "api-gateway server start..."
docker compose -f /home/docker-compose.yml up -d api-gateway
echo "noti-service server start..."
docker compose -f /home/docker-compose.yml up -d noti-service
echo "user-service server start..."
docker compose -f /home/docker-compose.yml up -d user-service
echo "weather-service server start..."
docker compose -f /home/docker-compose.yml up -d weather-service
