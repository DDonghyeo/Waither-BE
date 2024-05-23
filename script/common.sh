# 현재 실행중인 도커 컨테이너중 단어가 포함 컨테이너를 검색
RUNNING_EUREKA=$(docker ps | grep eureka)
RUNNING_CONFIG=$(docker ps | grep config)

# Eureka 검색
if [ -z "$RUNNING_EUREKA" ]; then
    echo "Starting Eureka ..."
    docker compose -f /home/docker-compose.yml up -d eureka
    sleep 5 #Eureka 실행 5초 대기
else
    echo "Eureka is already running"
fi

# Config 검색
if [ -z "$RUNNING_CONFIG" ]; then
    echo "Starting Config Service ..."
    docker compose -f /home/docker-compose.yml up -d config
    sleep 5 #Config 실행 5초 대기
else
    echo "Config Service is already running"
fi