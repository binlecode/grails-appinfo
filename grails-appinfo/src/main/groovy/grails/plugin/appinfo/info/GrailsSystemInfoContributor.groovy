package grails.plugin.appinfo.info

import grails.util.Holders
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor

class GrailsSystemInfoContributor implements InfoContributor {

    def grailsApplication

    @Override
    void contribute(Info.Builder builder) {
        if (!grailsApplication) {
            builder.withDetail('grailsApplication', 'not available')
            return
        }

        builder.withDetail('grails-profile', grailsApplication.config.grails?.profile)

//        builder.withDetail('spring-beans', splitBeans())

        builder.withDetail('grails-system-info', getGrailsSystemInfo())
    }

    /**
     * Partition beans into Controller, Domain, Filter, Service, TagLib, Job, and Other.
     *
     * @return the partitioned beans by Grails artifact categories
     */
    Map splitBeans() {

        def ctx = grailsApplication.mainContext
        def beanFactory = ctx.beanFactory

        def split = [
                Controller: [],
                Domain: [],
                Filter: [],
                Service: [],
                TagLib: [],
                Job: []
        ]

        def names = ctx.beanDefinitionNames as List
        for (String name : names) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name)
            if (name.endsWith('ServiceClass')) {
                findServiceBeanName name, names, ctx, beanFactory, split.Service
            }
            else if (name.endsWith('DomainClass')) {
                findDomainClassBeanName name, names, beanFactory, split.Domain
            }
            else if (name.endsWith('TagLib')) {
                if (beanDefinition.singleton) {
                    split.TagLib << name
                }
            }
            else if (name.endsWith('Controller')) {
                if (beanDefinition.prototype) {
                    split.Controller << name
                }
            }
            else if (name.endsWith('Filters')) {
                if (beanDefinition.singleton) {
                    split.Filter << name
                }
            }
            else if (name.endsWith('Job')) {
                if (beanDefinition.singleton) {
                    split.Job << name
                }
            }
        }

        names.removeAll split.Controller
        names.removeAll split.TagLib
        names.removeAll split.Service
        names.removeAll split.Domain
        names.removeAll split.Filter
        names.removeAll split.Job
        split.Other = names

        split
    }

    /**
     * Service bean names end in 'Service' but not all Spring beans ending in 'Service' are Grails
     * services, so use the '*ServiceClass' beans to find the corresponding service bean.
     *
     * @param serviceClassName the '*ServiceClass' bean name
     * @param names all bean names
     * @param ctx the application context
     * @param beanFactory the BeanFactory
     * @param typeNames service bean names to add to (passed by reference)
     */
    void findServiceBeanName(String serviceClassName, names, ctx, beanFactory, typeNames) {
        String beanName = ctx.getBean(serviceClassName).propertyName
        if (names.contains(beanName)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName)
            if (beanDefinition.singleton) {
                typeNames << beanName
            }
        }
    }

    /**
     * There's no suffix for domain classes or their bean names, so use the
     * '*DomainClass' beans to find the corresponding domain class bean.
     *
     * @param domainClassName '*DomainClass' bean name
     * @param names all bean names
     * @param beanFactory the BeanFactory
     * @param typeNames domain class bean names to add to (passed by reference)
     */
    void findDomainClassBeanName(String domainClassName, names, beanFactory, typeNames) {
        String beanName = domainClassName - 'DomainClass'
        if (names.contains(beanName)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName)
            if (beanDefinition.prototype) {
                typeNames << beanName
            }
        }
    }

    /**
     * Builds a list of maps containing bean information: name, class name, scope, whether it's lazy,
     * whether it's abstract, its parent bean (may be null), and its BeanDefinition beanClassName.
     *
     * @param names all bean names
     * @param beanFactory the BeanFactory
     * @return the info
     */
    List<Map<String, Object>> getBeanInfo(names, beanFactory) {
        names.sort()

        def beanDescriptions = []

        for (String name : names) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name)
            String className = buildClassName(beanFactory, name, beanDefinition)
            beanDescriptions << [name: name,
                                 className: className,
                                 scope: beanDefinition.scope ?: 'singleton',
                                 lazy: beanDefinition.lazyInit,
                                 isAbstract: beanDefinition.isAbstract(),
                                 parent: beanDefinition.parentName,
                                 beanClassName: beanDefinition.beanClassName]
        }

        beanDescriptions
    }

    /**
     * Calculate the class name of a bean, taking into account if it's an abstract bean definition, a
     * proxy, factory, etc.
     *
     * @param beanFactory the BeanFactory
     * @param name  the bean name
     * @param beanDefinition the BeanDefinition
     * @return the name
     */
    String buildClassName(beanFactory, String name, BeanDefinition beanDefinition) {

        if (beanDefinition.isAbstract()) {
            return '<i>abstract</i>'
        }

        if (beanDefinition.singleton) {
            def bean = beanFactory.getBean(name)
            if (AopUtils.isAopProxy(bean)) {
                return bean.getClass().name + " (" + AopUtils.getTargetClass(bean).name + ")"
            }
        }

        String beanClassName = beanDefinition.beanClassName
        if (!beanClassName && beanDefinition.factoryBeanName) {
            beanClassName = "Factory: $beanDefinition.factoryBeanName ($beanDefinition.factoryMethodName)"
        }

        beanClassName
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
                urlMappings: urlMappings,
                bootstraps: bootstraps,
                jobs: jobs
        ]
    }



}
