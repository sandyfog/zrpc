pipeline {
  agent {
    docker {
      image 'maven:3-alpine'
    }

  }
  stages {
    stage('Bulid') {
      steps {
        sh 'mvn clean install'
      }
    }
  }
}