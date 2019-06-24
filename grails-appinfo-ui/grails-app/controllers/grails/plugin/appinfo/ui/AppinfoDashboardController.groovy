package grails.plugin.appinfo.ui

import grails.converters.JSON

class AppinfoDashboardController {
    static namespace = "actuator"

    AppinfoDashboardService appinfoDashboardService
    ActuatorConfig actuatorConfig

    def index() {
        log.debug "index called from ${controllerName}"

        def health  = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.HEALTH))
        def info    = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.INFO))
        def metrics = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.METRICS))
        def env     = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.ENV))

        metrics = appinfoDashboardService.metricsUtility(metrics)

        [ health: health, info: info, metrics: metrics, env: env ]
    }

    def traceInfo() {
        def trace   = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.TRACE))
        def traceMap = appinfoDashboardService.traceUtility(trace)

        render view: "trace", model: [traceMap: traceMap]
    }

    def beanInfo() {
        def allBeans = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.BEANS))
        def beansMap = appinfoDashboardService.beansUtility(allBeans)

        render view: "beans", model: [beansMap: beansMap]
    }

    def mappingInfo() {
        def allMappings = parsedEndpointResponse(actuatorConfig.resolveUrl(ActuatorEndpoints.MAPPINGS))
        Map mappingsMap = appinfoDashboardService.mappingsUtility(allMappings)

        render view: "mappings", model: [mappingsMap: mappingsMap]
    }

    private def parsedEndpointResponse(String endpointId) {
        JSON.parse(hitEndpoint(endpointId))
    }

    String hitEndpoint(String endpointId) {
        g.include(view: "/$endpointId") as String
    }
}
