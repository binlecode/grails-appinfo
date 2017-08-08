package grails.plugin.appinfo

class LoggingInfoController {
    def loggingInfoService

    def index() {
        [logs: loggingInfoService.getLoggerNameAndLevelList()] + [allLevels: LoggingInfoService.LOG_LEVELS.keySet()]
    }

    @Deprecated
    def loggingInfo() {
        [logs: loggingInfoService.getLoggers()] + [allLevels: LoggingInfoService.LOG_LEVELS.keySet()]
    }

    def setLogLevel() {
        loggingInfoService.setLogLevel(params.logger, params.level)

        if (request.xhr) {
            render 'ok'
        } else {
            redirect action: 'loggingInfo'
        }
    }

}
