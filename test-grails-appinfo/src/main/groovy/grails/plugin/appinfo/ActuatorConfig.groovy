package grails.plugin.appinfo


class ActuatorConfig {
    def endpointsProperties  // to catch spring-boot endpoints properties
    def managementProperties // to catch spring-boot management properties

    boolean isMonitoringEnabled() {
        // If there is no configuration for endpoint or endpoints config does not have 'enabled' config or when enabled
        // is true then monitoring is enabled, otherwise disabled
        !endpointsProperties || !('enabled' in endpointsProperties.keySet()) || endpointsProperties.enabled.toBoolean()
    }

    String getContextPath() {
        managementProperties?."context-path" ?: ''
    }

    String getPort() {
        managementProperties?.port ?: ''
    }

    String getAddress() {
        managementProperties?.address ?: ''
    }

    boolean isHttpDisabled() {
        port == '-1'
    }

    /**
     * resolve actuator endpoint urls that might be customized via spring boot properties
     */
    String resolveUrl(ActuatorEndpoints endpointEnum) {
        if(!endpointEnum) { return '' }

        String endpointName = findEndpointId(endpointEnum)
        contextPath ? "/$contextPath/$endpointName" : "/$endpointName"
    }

    boolean isDashboardItemsEnabled() {
        monitoringEnabled && [
                ActuatorEndpoints.HEALTH,
                ActuatorEndpoints.METRICS,
                ActuatorEndpoints.INFO,
                ActuatorEndpoints.ENV,
                ActuatorEndpoints.TRACE,
                ActuatorEndpoints.BEANS,
                ActuatorEndpoints.MAPPINGS
        ].every { isEnabled(it) }
    }

    private boolean isEnabled(ActuatorEndpoints endpointEnum) {
        String value = endpointsProperties?."$endpointEnum.value"?.enabled
        value != null ? value.toBoolean() : true
    }

    private String findEndpointId(ActuatorEndpoints endpointEnum) {
        endpointsProperties?."$endpointEnum.value"?.id ?: endpointEnum.value
    }

    private boolean isSensitive(ActuatorEndpoints endpointEnum) {
        String value = endpointsProperties?."$endpointEnum.value"?.sensitive
        value != null ? value.toBoolean() : true
    }
}
