# 현재 실행중인 도커 컨테이너중 단어가 포함 컨테이너를 검색
RUNNING_EUREKA=$(docker ps | grep eureka)
RUNNING_CONFIG=$(docker ps | grep config)
RUNNING_ZOOKEEPER=$(docker ps | grep zookeeper)
RUNNING_KAFKA=$(docker ps | grep kafka)

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

# Zookeeper 검색
if [ -z "$RUNNING_ZOOKEEPER" ]; then
    echo "Starting Zookeeper ..."
    docker compose -f /home/docker-compose.yml up -d zookeeper
else
    echo "Zookeeper is already running"
fi

# Kafka 검색
if [ -z "$RUNNING_ZOOKEEPER" ]; then
    echo "Starting Kafka ..."
    docker compose -f /home/docker-compose.yml up -d kafka
else
    echo "Kafka is already running"
fi