pipeline {
    agent any

    tools {
        jdk 'JDK 22'
        mvn '3.9.9'
    }


    stages {
        stage('Checkout') {
            steps {
                // Checkout code from the current branch
                checkout scm
            }
        }

        stage('Build') {
            steps {
                withMaven {
                    sh 'which mvn'
                    sh 'ls -ltr `which mvn`'
                    sh 'mvn --version'
                    sh 'mvn clean compile -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                withMaven {
                    sh 'mvn test'
                }
            }
        }

        stage('Release') {
            when { branch 'release-*' }
            steps {
                sh 'echo $BRANCH_NAME'
                sh "docker build -t uberfij:latest -t deepfij:${BRANCH_NAME} ."
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
