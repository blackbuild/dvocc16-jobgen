package com.blackbuild.udom.jobdsl

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.FileJobManagement
import javaposse.jobdsl.dsl.ScriptRequest
import spock.lang.*

import static groovy.io.FileType.FILES
/**
 * Tests that all dsl scripts in the jobs directory will compile.
 */
@Stepwise
class JobScriptsSpec extends Specification {

    @Shared File root

    // DSLFactory needs an instance of JobManagement. this "simulates" Jenkins
    FileJobManagement jm
    DslScriptLoader scriptLoader

    def setupSpec() {
        root = new File("build/tmp/jobs")
        root.deleteDir()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def setup() {
        jm = Spy(FileJobManagement, constructorArgs: [root])
        scriptLoader = new DslScriptLoader(jm)
    }

    @Unroll
    def 'test scripts in "#folder"'() {
        when:
        scriptLoader.runScripts(scriptRequestsFor(folder))

        then:
        noExceptionThrown()

        where:
        folder << ['folders', 'jobs']
    }


    static List<ScriptRequest> scriptRequestsFor(String folder, String prefix = "") {
        jobFiles(folder, prefix).collect { new ScriptRequest(null, it.text, new File('.').toURI().toURL(), false, it.path) }
    }

    static List<File> jobFiles(String basefolder, String prefix = "") {
        List<File> files = []
        new File(basefolder).eachFileRecurse(FILES) {
            if (it.name.startsWith(prefix))
                files << it
        }
        files
    }
}