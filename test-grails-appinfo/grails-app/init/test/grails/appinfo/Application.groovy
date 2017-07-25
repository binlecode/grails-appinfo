package test.grails.appinfo

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    Closure doWithSpring() {
        { ->


            // ** spring boot actuator health check indicators ** //



        }
    }




}