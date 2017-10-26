package grails.plugin.appinfo.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

/**
 * This Mongodb healthIndicator doesn't depend on Spring mongodb template.
 * Instead, it is based on Grails Mongodb plugin supporting beans.
 * Specifically, it is using Grails 'mongo' bean, which is MongoClient instance.
 */
class MongoHealthIndicator implements HealthIndicator {
    final def mongoBean
    final def mongodbConfig

    MongoHealthIndicator(mongo, mongodbConfig) {
        this.mongoBean = mongo
        this.mongodbConfig = mongodbConfig
    }

    protected boolean doHealthCheck() {
        try {
            def db
            if (mongodbConfig.url) {
                db = mongodbConfig.url.split('/')[-1]
            } else {
                db = mongodbConfig.databaseName
            }
            if (db && mongoBean.getDB(db)) {
                return true
            } else {
                return false
            }
        } catch (ex) {
            return false
        }
    }

    @Override
    Health health() {
        def check = doHealthCheck()
        if (!check) {
            return Health.down()
                    .withDetail("error", "not able to connect to url: ${mongodbConfig.url}")
                    .build()
        }
        return Health.up()
                .withDetail('url', mongodbConfig.url)
                .build()
    }
}
