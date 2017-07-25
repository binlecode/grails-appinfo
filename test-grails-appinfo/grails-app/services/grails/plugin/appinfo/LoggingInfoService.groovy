package grails.plugin.appinfo

import ch.qos.logback.classic.Level
import org.apache.log4j.Logger
import org.slf4j.LoggerFactory

class LoggingInfoService {
    static transactional = false

    /*** logger manangement ***/

    static final Map LOG_LEVELS = [
            ALL:   Level.ALL,
            TRACE: Level.TRACE,
            DEBUG: Level.DEBUG,
            INFO:  Level.INFO,
            WARN:  Level.WARN,
            ERROR: Level.ERROR,
            OFF:   Level.OFF]

    /**
     * Partition loggers into groups: spring, hibernate, codec, controller, controllerMixin,
     *	domain, filters, service, taglib, grails, groovy, and misc.
     * Each subset is sorted by name.
     *
     * @return the partitioned loggers
     */
    Map<String, List<Logger>> getLoggers() {

        def codec = []
        def controller = []
        def controllerMixin = []
        def domain = []
        def filters = []
        def service = []
        def taglib = []
        def grails = []
        def groovy = []
        def hibernate = []
        def spring = []
        def misc = []
        for (logger in sortedLoggers) {
            String name = logger.name
            def info = [name: name, level: logger.effectiveLevel.toString()]
            if (name.startsWith('grails.app.codec')) {
                codec << info
            }
            else if (name.startsWith('grails.app.controllerMixin')) {
                controllerMixin << info
            }
            else if (name.startsWith('grails.app.controller')) {
                controller << info
            }
            else if (name.startsWith('grails.app.domain')) {
                domain << info
            }
            else if (name.startsWith('grails.app.filters')) {
                filters << info
            }
            else if (name.startsWith('grails.app.service')) {
                service << info
            }
            else if (name.startsWith('grails.app.taglib')) {
                taglib << info
            }
            else if (name == 'grails' || name.startsWith('grails.') || name.startsWith('org.codehaus.groovy.grails.')) {
                grails << info
            }
            else if (name == 'groovy.' || name.startsWith('groovy.') || name.startsWith('org.codehaus.groovy.')) {
                groovy << info
            }
            else if (name.startsWith('org.hibernate')) {
                hibernate << info
            }
            else if (name.startsWith('org.springframework')) {
                spring << info
            }
            else {
                misc << info
            }
        }

        [spring: spring, hibernate: hibernate, codec: codec, controller: controller,
         controllerMixin: controllerMixin, domain: domain, filters: filters,
         service: service, taglib: taglib, grails: grails, groovy: groovy, misc: misc]
    }

    List<Map> getLoggerNameAndLevelList() {
        getSortedLoggers().collect {
            [name: it.name, level: it.effectiveLevel.toString()]
        }
    }

    /**
     * Get all current loggers sorted by name.
     * @return the loggers
     */
    List<Logger> getSortedLoggers() {
        LoggerFactory.getILoggerFactory().getLoggerList().sort { logger1, logger2 ->
            if (!logger1.name || !logger2.name) {
                return 0
            }
            logger1.name <=> logger2.name
        }
    }

    /**
     * Set a logger's level.
     *
     * @param loggerName the logger name
     * @param levelName one of ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
     */
    void setLogLevel(String loggerName, String levelName) {
        Level level = LOG_LEVELS[levelName]
        // need to cast to logback interface to change log level
        (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(loggerName).setLevel(level)
    }
}










