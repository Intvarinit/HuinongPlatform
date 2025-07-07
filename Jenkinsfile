pipeline {
    agent any

    environment {
        DOCKER_IMAGE_NAME = "huinong-platform"
        DOCKER_TAG = "latest"

        // 环境变量从Jenkins凭证或参数获取
        MYSQL_HOST = "http://localhost"
        MYSQL_USER = "root"
        MYSQL_PASS = "123456"
        REDIS_HOST = "http://localhost"
        REDIS_PORT = "6379"
        RABBITMQ_HOST = "your-rabbitmq-host"
        RABBITMQ_PORT = "5672"
        RABBITMQ_USER = "admin"
        RABBITMQ_PASS = "admin"
        MINIO_HOST = "http://localhost"
        MINIO_ACCESS_KEY = "root"
        MINIO_SECRET_KEY = "123456789"
        ALIYUN_SMS_KEY_ID = "your_access_key_id"
        ALIYUN_SMS_KEY_SECRET = "your_access_key_secret"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Intvarinit/HuinongPlatform.git'
            }
        }

        stage('Build Jar') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build & Run App') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up -d --build'
            }
        }

        stage('Clean Up Unused Images') {
            steps {
                sh 'docker images -f "dangling=true" -q | xargs --no-run-if-empty docker rmi'
            }
        }
    }

    post {
        success {
            echo "✅ 部署成功！访问地址：http://<服务器IP>:8080"
        }
        failure {
            echo "❌ 部署失败，请检查日志。"
        }
    }
}