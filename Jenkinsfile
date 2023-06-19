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
                withMaven {
                    sh 'mvn clean package -DskipTests'
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
            when { branch 'master' }
            steps {
                withMaven {
                    sh 'mvn release:prepare release:perform'
                }
            }
        }
    }
//    post {
//        always {
//            cleanWs()
//        }
//    }
    //This is a change
    //// New change
}
