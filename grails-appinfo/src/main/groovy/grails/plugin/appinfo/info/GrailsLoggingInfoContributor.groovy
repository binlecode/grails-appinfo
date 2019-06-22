package grails.plugin.appinfo.info

import grails.core.GrailsApplication
import grails.plugin.appinfo.util.GrailsLoggingUtil
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor

/**
 * Provides Grails system information and Grails artifacts specifications.
 */
class GrailsLoggingInfoContributor implements InfoContributor {
    GrailsApplication grailsApplication

    @Override
    void contribute(Info.Builder builder) {
        builder.withDetail('grails-logging-info', grailsLoggingInfo)
    }

    def getGrailsLoggingInfo() {
        GrailsLoggingUtil.getLoggers()
    }



}
