# grails-appinfo
Grails plugin to check and monitor application status with a dashboard UI

[![Build Status](https://travis-ci.org/binlecode/grails-appinfo.svg?branch=master)](https://travis-ci.org/binlecode/grails-appinfo)

`grails-appinfo` plugin download:
[ ![Download](https://api.bintray.com/packages/ikalizpet/plugins/grails-appinfo/images/download.svg) ](https://bintray.com/ikalizpet/plugins/grails-appinfo/_latestVersion)

`grails-appinfo-ui` plugin download:
[ ![Download](https://api.bintray.com/packages/ikalizpet/plugins/grails-appinfo-ui/images/download.svg) ](https://bintray.com/ikalizpet/plugins/grails-appinfo-ui/_latestVersion)

## INTRODUCTION 

Grails-appinfo Grails plugin is a set of convenient utilities for application 
system details, health-check, configuration and monitoring at runtime.

This Grails plugin builds on top of spring boot actuator API with Grails specific
enhancements on stock actuator endpoints. 

To consume actuator JSON endpoints, the testing Grails application also provides a monitoring
dashboard inspired by [grails-actuator-ui](https://github.com/dmahapatro/grails-actuator-ui).

The dashboard UI is built on bootstrap with CSS framework from [AdminLTE](https://adminlte.io/). 

This repository contains three software modules:
- `grails-appinfo` Grails plugin that provides RESTful JSON Actuator endpoints
- `grails-appinfo-ui` Grails plugin that provides on top of `grails-appinfo` RESTful endpoint a web UI dashboard based on AdminLTE theme framework
- `test-grails-appinfo` a testing host Grails application.

The `grails-appinfo-ui` plugin includes a AdminLTE (Bootstrap styled) dashboards that make ajax calls to RESTful endpoints that `grails-appinfo` enhances on stock `Spring Boot Actuator` endpoints. 

The dashboard's url is `<root-context>/appinfoDashboard`. It contains following components:

UI dynamic (Grails GSP) components:
- gsp in folder `views/appinfoDashboard`
- layout template `views/layouts/appinfo.gsp`
- taglib `taglib/grails/plugin/appinfo/AvatarTaglib.groovy`

UI static resources (from AdminLTE distribution):
- `grails-app/assets/images/appinfo`
- `grails-app/assets/javascripts/appinfo`
- `grails-app/assets/stylesheets/appinfo`

## INSTALL

In host Grails application's build.gradle file:

```groovy
plugins {
    compile ':grails-appinfo:$version'
}
```

or load the UI dashboard
```groovy
plugins {
    compile ':grails-appinfo-ui:$version'
}
```

## PREREQUISITES

Hosting Grails application version 3.3.0+.


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

## SAMPLE APPLICATION

The plugin provides RESTful json view by itself with endpoints as below:
`<root-context>/` followed by `autoconfig`, `configprops`, `dump`, `env`, `health`, `info`, `metrics`, `mappings`, `shutdown`, `trace`, `beans`.

Most of them are decorators of Spring Boot Actuator native endpoints. But with enhanced information and connectivity support such as mongodb, s3, generic web url endpoint, etc.

For example, `localhost:8080/health` endpoint returns:
```json
{
    "status": "DOWN",
    "diskSpace": {
        "status": "UP",
        "total": 499963170816,
        "free": 281595985920,
        "threshold": 262144000
    },
    "urlHealthCheck_web_info": {
        "status": "UP",
        "url": "http://localhost:8080/info",
        "method": "HEAD",
        "timeout.threshold": "10000 ms"
    },
    "databaseHealthCheck": {
        "status": "UP",
        "database": "H2",
        "hello": 1
    },
    "urlHealthCheck_webroot": {
        "status": "UP",
        "url": "http://localhost:8080",
        "method": "GET",
        "timeout.threshold": "10000 ms"
    },
    "mongodbHealthCheck": {
        "status": "DOWN",
        "url": "mongodb://localhost/test_grails_appinfo",
        "db": "test_grails_appinfo",
        "error": "java.lang.Exception: MongoDB check timed out after 3000 ms"
    },
    "s3HealthCheck": {
        "status": "DOWN",
        "endpoint": "https://s3.amazonaws.com",
        "error": "java.lang.Exception: S3 check fail: Unable to load AWS credentials from any provider in the chain"
    }
}
```

Some dashboard UI screenshots:
- dashboard 
![Alt appinfo UI dashboard](screenshots/appinfo-ui-dashboard.png?raw=true "appinfo-ui dashboard")
- URL mappings
![Alt appinfo UI URL mappings](screenshots/appinfo-ui-mappings.png?raw=true "appinfo-ui url mappings trace")
- http call trace
![Alt appinfo UI http trace](screenshots/appinfo-ui-trace.png?raw=true "appinfo-ui http trace")
- runtime beans
![Alt appinfo UI beans](screenshots/appinfo-ui-beans.png?raw=true "appinfo-ui beans")


## CHANGELOG

#### v 2.2
* scale back appinfo plugin to non-web profiled plugin, to keep it lean as RESTful only
* add appinfo-ui plugin for web UI dashboard support based on AdminLTE theme framework

#### v 2.1.1
* fix mongodb connection checking logic
* enhance mongodb connection check with timeout window support, when timeout is reached, the connection will throw exception

#### v 2.0
* support Grails 3.3.x with Spring Boot 1.5.x and GORM 6.1.x


## CONTRIBUTORS

Bin Le (bin.le.code@gmail.com)


## LICENSE

Apache License Version 2.0. (http://www.apache.org/licenses/)


