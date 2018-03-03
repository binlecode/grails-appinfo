package test.grails.appinfo

import groovy.util.logging.Slf4j

@Slf4j
class BootStrap {

    def init = { servletContext ->

        log.info "bootstrap is running"

    }

    def destroy = {
    }
}
