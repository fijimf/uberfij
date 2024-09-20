pipeline {
    agent any

    tools {
        jdk 'JDK 22'
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
