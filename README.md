# grails-appinfo
Grails plugin to check and monitor application status with a dashboard UI

[![Build Status](https://travis-ci.org/binlecode/grails-appinfo.svg?branch=master)](https://travis-ci.org/binlecode/grails-appinfo)
 
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
        mongodb:
            # hide or show password in the mongodb connection url if it contains credential info
            # if set to false (default if not set), the password will be replaced as '<pswd>'
            showPassword: false  # default to false
        urls:   # list of webservice endpoints to check
            - url: 'http://localhost:8080'
              name: 'web root'   # name of the endpoint
              method: 'GET'      # http method, default to 'HEAD' if not given
            - url: 'http://localhost:8080/info'
              name: 'web_info'
        aws:
            s3:
                # either:
                bucket: 'bucket-name'  # bucket name used in s3 health check
                # or: (for multiple buckets)
                #buckets:
                #    - 'bucket-name'
                #    - 'another-bucket-name'
    info:
        # add 'grails-system-info' to Actuator info endpoint, default (if not set) is not enabled
        system: true
        # add 'grails-logging-info' to Actuator info endpoint, default (if not set) is not enabled
        logging: true
        # add following keys to Actuator info endpoint, default (if not set) is not enabled
        # - 'jvm-version'
        # - 'groovy-version'
        # - 'grails-runtime-environment'
        # - 'grails-reload-enabled'
        # - 'grails-runtime-threads-info'
        runtime: true
```

## CHANGELOG

#### 1.3
* support both AWS SDK Grails plugin version 1.x and 2.x.

#### 1.2
* fix mongodb check timeout error during healthcheck
* enhancements of loggingInfo exposure in Actuator endpoint
* restore runtime logger management web UI because of SpringBoot Actuator v1.4's lack of loggers RESTful endpoint 

#### v1.1
* add password shadowing option for mongodb connection url

#### v1.0
* stable release for Grails 3.2.x, with Spring Boot 1.4 and GORM 6.0

#### v0.9
* support both multi-dataSources and single dataSource in health check
* support mongodb in health check


## CONTRIBUTORS

Bin Le (bin.le.code@gmail.com)


## LICENSE

Apache License Version 2.0. (http://www.apache.org/licenses/)


