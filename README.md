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

The sample application also includes a Bootstrap styled dashboard with url:
`<root-context>/appinfoDashboard` which renders information with ajax call to above endpoints.

The web UI is straightforward gsp under folder `views/appinfoDashboard`, and its layout template is under folder `views/layouts/appinfo.gsp`.

UI static resource files are:
- `grails-app/assets/images/appinfo`
- `grails-app/assets/javascripts/appinfo`
- `grails-app/assets/stylesheets/appinfo`

Here is a screenshot of v1.3 sample application dashboard:
![Alt appinfo UI dashboard](screenshots/appinfo-ui-dashboard.png?raw=true "appinfo-ui dashboard screenshot")


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


