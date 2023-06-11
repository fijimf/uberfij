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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Release'){
          when {
            expression {
               env.BRANCH_NAME == 'master'
            }
            steps {
                            sh 'mvn release:prepare release:perform'
                    }
          }
    }
}