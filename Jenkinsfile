pipeline {
    agent any

tools {
    jdk 'JDK 17'
}
    stages {
        stage('Checkout') {
            steps {
                git branch: 'develop', url: 'https://github.com/fijimf/uberfij.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}
