package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class CredentialsBindingContext implements Context {
    private final JobManagement jobManagement

    final List<Node> nodes = []

    CredentialsBindingContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void file(String variable, String credentials) {
        addSimpleBinding('File', variable, credentials)
    }

    void string(String variable, String credentials) {
        addSimpleBinding('String', variable, credentials)
    }

    void usernamePassword(String variable, String credentials) {
        addSimpleBinding('UsernamePassword', variable, credentials)
    }

    /**
     * @since 1.31
     */
    @RequiresPlugin(id = 'credentials-binding', minimumVersion = '1.3')
    void usernamePassword(String userVariableName, String passwordVariableName, String credentials) {
        nodes << new NodeBuilder().'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordMultiBinding' {
            credentialsId(jobManagement.getCredentialsId(credentials))
            usernameVariable(userVariableName)
            passwordVariable(passwordVariableName)
        }
    }

    void zipFile(String variable, String credentials) {
        addSimpleBinding('ZipFile', variable, credentials)
    }

    private void addSimpleBinding(String type, String variableName, String credentials) {
        nodes << new NodeBuilder()."org.jenkinsci.plugins.credentialsbinding.impl.${type}Binding" {
            variable(variableName)
            credentialsId(jobManagement.getCredentialsId(credentials))
        }
    }
}
