import grails.plugin.appinfo.ActuatorConfig

// Place your Spring DSL code here
beans = {

    actuatorConfig(ActuatorConfig) {
        endpointsProperties = application.config.getProperty('endpoints', Map, [:])
        managementProperties = application.config.getProperty('management', Map, [:])
    }

}
