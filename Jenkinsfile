pipeline {
  agent any

  tools {
    jdk "Jdk17"
    gradle "Ga"
  }
  environment { 
    DOCKERHUB_CREDENTIALS = credentials('dockerhubToken') 
    REGION = "ap-northeast-2"
    AWS_CREDENTIAL_NAME = 'awsTeam4'
  }

  stages {
    stage('Git Clone') {
      steps {
        echo 'Git Clone'
        git url: 'https://github.com/design-view/springshop.git',
          branch: 'main', credentialsId: 'gitToken'
      }
    }
    stage('Gradle Build') {
      steps {
        echo 'Gradle Build'
        sh 'chmod +x ./gradlew' // 실행 권한 부여
        sh './gradlew build -x test'
      }
    }
    stage('Docker Image Build') {
      steps {
        echo 'Docker Image Build'                
        dir("${env.WORKSPACE}") {
          sh """
            docker build -t pinkcandy02/shopspring:$BUILD_NUMBER .
            docker tag pinkcandy02/shopspring:$BUILD_NUMBER pinkcandy02/shopspring:latest
            """
        }
      }
    }
    stage('Docker Login') {
      steps {
        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
      }
    }
    stage('Docker Image Push') {
      steps {
        echo 'Docker Image Push'  
        sh "docker push pinkcandy02/shopspring:latest"
      }
    }
    stage('Cleaning up') { 
      steps { 
        echo 'Cleaning up unused Docker images on Jenkins server'
        sh """
           docker rmi pinkcandy02/shopspring:$BUILD_NUMBER
           docker rmi pinkcandy02/shopspring:latest
           """
      }
    } 
    stage('Upload S3') {
      steps {
        echo "Upload to S3" 
        dir("${env.WORKSPACE}") {
          sh 'zip -r deploy.zip ./deploy appspec.yml'
          withAWS(region:"${REGION}", credentials: "${AWS_CREDENTIAL_NAME}"){
            s3Upload(file:"deploy.zip", bucket:"team4-min-test-s3")
          }
          sh 'rm -rf ./deploy.zip'
        }
      }
    }
  }
}
      
  
