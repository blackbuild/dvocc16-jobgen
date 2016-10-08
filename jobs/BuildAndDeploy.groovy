import model.Config

Config config = Config.createFromScript(Model)

println "Creating System Master build Jobs"
config.projects.each { projectName, project ->

    println "  $projectName"

    pipelineJob("$projectName/CI") {
        logRotator(14, -1, -1, 1)

        triggers {
            scm("@hourly")
        }

        definition {
            cps {
                script """
stage("checkout") {
    checkout ("$project.repositoryUrl")
}

stage("build") {
    sh "mvn clean deploy -Pci"
}
"""
            }
        }
   }

    pipelineJob("$projectName/Release") {
        logRotator(14, -1, -1, 1)

        definition {
            cps {
                script """
stage("checkout") {
    checkout ("$project.repositoryUrl")
}

stage("build") {
    sh "mvn release:prepare release:perform"
}
"""
            }
        }
    }

    project.servers.each { serverName, server ->
        pipelineJob("$projectName/Deploy-To-$serverName") {
            logRotator(14, -1, -1, 1)

            definition {
                cps {
                    script """
stage("fetch from Jenkins") {
    // download artifact $project.artifact
}

stage("deploy") {
    copyToServer("${project.artifact.artifactId}.war", "$server.hostname:$server.basedir")
}
"""
                }
            }
        }
    }



}