pipeline {
    agent any

    environment {
        IMAGE_NAME = "meikocn-api"
        TARGET_SERVER_PATH = "meikocn"
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        AWS_REGION = credentials('AWS_REGION')
        S3_BUCKET_NAME = credentials('S3_BUCKET_NAME')
        DOCKER_USERNAME = credentials('DOCKER_USERNAME')
        DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Get short SHA') {
            steps {
                script {
                    SHORT_SHA = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.IMAGE_TAG = "sha-${SHORT_SHA}"
                    echo "Image Tag: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Configure AWS CLI') {
            steps {
                sh '''
                    aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
                    aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
                    aws configure set default.region $AWS_REGION
                '''
            }
        }

        stage('Download Config from S3') {
            steps {
                sh '''
                    mkdir -p src/main/resources
                    aws s3 cp s3://$S3_BUCKET_NAME/config/application.yaml src/main/resources/application.yaml
                '''
            }
        }

        stage('Docker Login') {
            steps {
                sh '''
                    echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                '''
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def imageFullName = "${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Building and pushing ${imageFullName}"
                    sh """
                        docker build -t ${imageFullName} .
                        docker push ${imageFullName}
                        docker logout
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed (success or failure)."
        }
        success {
            echo "✅ Build and push succeeded!"
        }
        failure {
            echo "❌ Build failed!"
        }
    }
}
