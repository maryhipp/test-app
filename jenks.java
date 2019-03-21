pipeline {
  agent any
  stages {
    stage('Set pending on push') {
      when {
        expression {
          return params.x_github_event == "push"
        }
      }
      steps {
        setBuildStatus("tests have not yet run - waiting for deployment", "PENDING")
      }
    }
    stage('Set pending on deployment') {
      when {
        expression {
          return params.x_github_event == "deployment_status" && params.deployment_state == "success"
        }
      }
      steps {
        setBuildStatus("starting tests", "PENDING")
      }
    }
    stage('Install dependencies') {
      when {
        expression {
          return params.x_github_event == "deployment_status" && params.deployment_state == "success"
        }
      }
      steps {
        sh "yarn install --frozen-lockfile --ignore-scripts --ignore-engines"
      }
    }
    stage('Run tests') {
      when {
        expression {
          return params.x_github_event == "deployment_status" && params.deployment_state == "success"
        }
      }
      steps {
        sh "cd ./interface/venue && $(yarn bin)/cypress run --record"
      }
    }
    stage('Set check to pass on deployment') {
      when {
        expression {
          return params.x_github_event == "deployment_status" && params.deployment_state == "success"
        }
      }
      steps {
        setBuildStatus("e2e tests have passed", "SUCCESS")
      }
      }
    }
  }
}

void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/maryhipp/test-app"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}
