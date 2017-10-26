package grails.plugin.appinfo

import grails.plugin.appinfo.health.AwsS3HealthIndicator
import grails.plugin.appinfo.health.MongodbHealthIndicator
import grails.plugin.appinfo.health.UrlHealthIndicator
import grails.plugin.appinfo.info.GrailsRuntimeInfoContributor
import grails.plugin.appinfo.info.GrailsSystemInfoContributor
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.boot.actuate.health.DataSourceHealthIndicator
import org.springframework.boot.actuate.health.DiskSpaceHealthIndicatorProperties

@Slf4j
class GrailsAppinfoGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.2.0 > *"

    def loadAfter = ['dataSources', 'services', 'mongodb', 'aws-sdk']

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Appinfo Grails plugin application" // Headline display name of the plugin
    def author = "Bin Le"
    def authorEmail = "bin.le.code@gmail.com"
    def description = '''
Appinfo Grails plugin provides additional application info via Spring boog actuator endpoints.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-appinfo"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() {
        { ->

            // appinfo plugin Grails config holder
            def aiConfig = config.appinfo

            // ** health endpoint enhancement **

            diskSpaceHealthIndicatorProperties(DiskSpaceHealthIndicatorProperties) {
                threshold = 250 * 1024 * 1024
            }

            if (config.dataSource) {
                databaseHealthCheck(DataSourceHealthIndicator, ref('dataSource'))
            }

            config.dataSources?.each {
                def dsNameSuffix = (!it.key || it.key == 'dataSource') ? '' : "_${it.key}"
                def ds = ref("dataSource$dsNameSuffix")
                "databaseHealthCheck${dsNameSuffix}"(DataSourceHealthIndicator, ds)
            }

            if (config.grails.mongodb) {
                mongodbHealthCheck(MongodbHealthIndicator,
                    ref('mongo'), config.grails.mongodb
                )
            }

            if (config.grails.plugin.awssdk) {
                s3HealthCheck(AwsS3HealthIndicator, ref('amazonWebService')) {
                    def s3Cfg = aiConfig?.health?.aws?.s3
                    if (s3Cfg) {
                        s3Config = s3Cfg
                    }
                }
            }

            aiConfig?.health?.urls?.each { urlConfig ->
                "urlHealthCheck_${urlConfig.name.replaceAll('[^a-zA-Z0-9_]+','')}"(UrlHealthIndicator, urlConfig.url) {
                    if (urlConfig.method) {
                        method = urlConfig.method
                    }
                }
            }

            // ** info endpoint enhancement **

            if (Boolean.parseBoolean(aiConfig?.info?.system?.toString())) {
                grailsSystemInfoContributor(GrailsSystemInfoContributor) {
                    grailsApplication = grailsApplication
                }
            }

            if (Boolean.parseBoolean(aiConfig?.info?.runtime?.toString())) {
                grailsRuntimeInfoContributor(GrailsRuntimeInfoContributor) {
                    grailsApplication = grailsApplication
                }
            }

        } }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
