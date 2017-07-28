# grails-appinfo
Grails plugin to check and monitor application status with a dashboard UI


 
## INTRODUCTION 

Grails-appinfo Grails plugin is a set of convenient utilities for application 
system details, health-check, configuration and monitoring at runtime.

This Grails plugin builds on top of spring boot actuator API with Grails specific
enhancements on stock actuator endpoints. 

To consume actuator JSON endpoints, the testing Grails application also provides a monitoring
dashboard inspired by [grails-actuator-ui](https://github.com/dmahapatro/grails-actuator-ui).

The dashboard UI is built on bootstrap with CSS framework from [AdminLTE](https://adminlte.io/). 

This repository contains source code of Grails-appinfo plugin, and a testing host Grails application.

## INSTALL

In host Grails application's build.gradle file:

```groovy
plugins {
    compile ':grails-appinfo:$version'
}
```

## PREREQUISITES

Hosting Grails application version 3.0+.


## CONFIGURATION


In host Grails application grails-app/conf/application.yml

```yaml
# Appinfo grails plugin settings
appinfo:
    health:
        urlList:   # list of webservice endpoints to check
            - url: 'http://localhost:8080'
              name: 'web root'   # name of the endpoint
              method: 'GET'      # http method, default to 'HEAD' if not given
            - url: 'http://localhost:8080/info'
              name: 'web_info'
        aws:
            s3:
                bucket: 'dda-de-dev'  # bucket name used in s3 health check
    info:
        system: true
        runtime: true
        bean: true
        logging: true
```

## CONTRIBUTORS

Bin Le (bin.le.code@gmail.com)


## LICENSE

Apache License Version 2.0. (http://www.apache.org/licenses/)


