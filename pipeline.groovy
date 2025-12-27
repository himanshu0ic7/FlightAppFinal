pipeline {
    agent any
    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:$PATH"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/himanshu0ic7/FlightAppFinal.git'
            }
        }

        stage('Build All Microservices') {
            steps {
                script {
                    def services = [
                        'applicationGateway',
                        'bookingService',
                        'configServer',
                        'eurekaServer',
                        'flightService',
                        'notificationService',
                        'securityService'
                    ]
                    services.each { service ->
                        echo " BUILDING: ${service}"
                        dir(service) {
                            sh 'mvn package'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'All Services Built Successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: false
        }
        failure {
            echo 'Build Failed. Please check the logs.'
        }
    }
}