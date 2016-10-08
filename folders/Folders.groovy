import model.Config

Config config = Config.createFromScript(Model)


println "Creating folders.."

config.projects.keySet().each { project ->
    println "  $project"
    folder(project) {}
}
