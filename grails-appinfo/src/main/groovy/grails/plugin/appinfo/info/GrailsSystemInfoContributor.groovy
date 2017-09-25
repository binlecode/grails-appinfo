package grails.plugin.appinfo.info

import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.util.Holders
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor

/**
 * Provides Grails system information and Grails artifacts specifications.
 */
class GrailsSystemInfoContributor implements InfoContributor {
    GrailsApplication grailsApplication

    @Override
    void contribute(Info.Builder builder) {
        if (!grailsApplication) {
            builder.withDetail('grailsApplication', 'not available')
            return
        }

        builder.withDetail('grails-profile', grailsApplication.config.grails?.profile)

        builder.withDetail('grails-system-info', getGrailsSystemInfo())
    }

    def getGrailsSystemInfo() {
        def plugins = Holders.getPluginManager().allPlugins.collect { p ->
            [
                    name: p.name,
                    version: p.version
            ]
        }
        def domains = grailsApplication.domainClasses.collect { d ->
            [
                    name: d.fullName,
                    persistenctProperties: d.persistentProperties.collect { p ->
                        p.name
                    }.sort()
            ]
        }
        def services = grailsApplication.serviceClasses.sort { it.fullName }.collect { s ->
            [
                    name: s.fullName,
                    isTransactional: s.isTransactional(),
                    datasource: s.datasource
            ]
        }
        def g
        try {
            g = grailsApplication.mainContext.getBean('org.grails.plugins.web.taglib.ApplicationTagLib')
        } catch (NoSuchBeanDefinitionException ex) {
            // do nothing if tagLib is not available
        }
        def controllers = grailsApplication.controllerClasses.sort { it.fullName }.collect { c ->
            def ctrlInfo =
            [
                    name: c.fullName,
                    scope: c.scope,
                    actions: c.actions.sort()
            ]
            if (g) {
                ctrlInfo.url = g.createLink(controller: c.logicalPropertyName, absolute: true)
            }
            ctrlInfo
        }
        def tagLibs = grailsApplication.tagLibClasses.sort { it.fullName }.collect { t ->
            [
                    name: t.name,
                    logicalPropertyName: t.logicalPropertyName,
                    namespace: t.namespace,
                    tagNames: t.tagNames
            ]
        }
        def codecs = grailsApplication.codecClasses.sort { it.fullName }.collect { GrailsClass t ->
            [
                    name: t.name,
                    logicalPropertyName: t.logicalPropertyName,
                    isAbstract: t.isAbstract(),
                    packageName: t.packageName
            ]
        }
        def urlMappings = grailsApplication.urlMappingsClasses.sort { it.fullName }.collect { t ->
            [
                    name: t.name,
                    logicalPropertyName: t.logicalPropertyName
            ]
        }
        def bootstraps = grailsApplication.bootstrapClasses.sort { it.fullName }.collect { t ->
            [
                    name: t.name,
                    logicalPropertyName: t.logicalPropertyName

            ]
        }
        def jobs = []
        try {
            jobs = grailsApplication.jobClasses.sort { it.fullName }.collect { t ->
                [
                        name: t.name,
                        logicalPropertyName: t.logicalPropertyName
                ]
            }
        } catch (ex) {
            // do nothing, silently skip
        }

        [
                environment: grails.util.Environment.current.name,
                appProfile: grailsApplication.config.grails?.profile,
                groovyVersion: groovy.lang.GroovySystem.getVersion(),
                jvmVersion: System.getProperty('java.version'),
                reloadingActive: grails.util.Environment.reloadingAgentEnabled,
                plugins: plugins,
                controllers: controllers,
                domains: domains,
                services: services,
                tagLibs: tagLibs,
                codecs: codecs,
                urlMappings: urlMappings,
                bootstraps: bootstraps,
                jobs: jobs
        ]
    }



}
