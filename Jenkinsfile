pipeline {
  agent any
  triggers {
    GenericTrigger(
     genericHeaderVariables: [
      [key: 'X-GitHub-Event']
     ],
     
     causeString: 'Triggered by $x_github_event',
     
     token: '8jcLaiEmcF',
     
     printContributedVariables: true,
     printPostContent: true,
     
     silentResponse: false
    )
  }
  stages {
    stage('Set pending') {
      when {
        expression {
          return params.x_github_event === "push"
        }
      }
      steps {
        setBuildStatus("tests have not yet run with these changes", "pending")
      }
    }
  }
}

void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/my-org/my-repo"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}
