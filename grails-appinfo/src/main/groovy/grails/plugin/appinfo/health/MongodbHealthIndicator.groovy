package grails.plugin.appinfo.health

import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health

/**
 * This Mongodb healthIndicator doesn't depend on Spring mongodb template.
 * Instead, it is based on Grails Mongodb plugin supporting beans.
 * Specifically, it is using Grails 'mongo' bean, which is MongoClient instance.
 */
class MongodbHealthIndicator extends AbstractHealthIndicator {
    final def mongoBean
    final def mongodbConfig

    MongodbHealthIndicator(mongo, mongodbConfig) {
        this.mongoBean = mongo
        this.mongodbConfig = mongodbConfig
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        try {
            def db
            if (mongodbConfig.url) {
                db = mongodbConfig.url.split('/')[-1]
            } else {
                db = mongodbConfig.databaseName
            }
            builder.withDetail('db', db)

            if (db && mongoBean.getDB(db)) {
                builder.up()
                //todo: need print out some additional info
            } else {
                builder.down(new Exception("MongoDB not available"))
            }
        } catch (ex) {
            builder.down("MongoDB check fail: ${ex.message ?: ex.toString()}")
        }
    }

}
