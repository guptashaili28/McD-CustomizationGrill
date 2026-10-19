// McDelivery India - Location module automation (Selenium WebDriver + TestNG + JUnit 5)
// Works on a Windows or Linux agent that has a JDK (17+), Maven and a browser installed.
pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['edge', 'chrome', 'firefox'], description: 'Browser to run the tests in')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run the browser without a window')
    }

    options {
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    triggers {
        // Nightly regression run
        cron('H 2 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    runCommand('mvn -B clean test-compile')
                }
            }
        }

        stage('Test - Location module') {
            steps {
                script {
                    runCommand("mvn -B test -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}")
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/**/*.xml'
            archiveArtifacts allowEmptyArchive: true,
                    artifacts: 'Reports/*.html, Screenshots/*.png, target/surefire-reports/**/*.html, target/surefire-reports/**/*.xml'
        }
        success {
            echo 'All Location module tests passed.'
        }
        unstable {
            echo 'Some Location module tests failed - see Test Result and the archived Extent report.'
        }
    }
}

def runCommand(String cmd) {
    // Test failures should mark the build UNSTABLE (yellow), not stop the pipeline,
    // so the reports are still published.
    def status = isUnix() ? sh(script: cmd, returnStatus: true) : bat(script: cmd, returnStatus: true)
    if (status != 0) {
        if (cmd.contains(' test ') || cmd.endsWith(' test')) {
            unstable("Maven test run exited with ${status}")
        } else {
            error("Command failed (${status}): ${cmd}")
        }
    }
}
