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
    final def healthMongodbConfig

    MongodbHealthIndicator(mongo, mongodbConfig, healthMongodbConfig) {
        this.mongoBean = mongo
        this.mongodbConfig = mongodbConfig
        this.healthMongodbConfig = healthMongodbConfig
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        try {
            def db
            if (mongodbConfig.url) {
                String mongodbUrl = mongodbConfig.url
                if (!Boolean.parseBoolean(healthMongodbConfig?.showPassword?.toString())) {
                    mongodbUrl = shadowUrlPassword(mongodbUrl)
                }
                builder.withDetail('url', mongodbUrl)
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

    /**
     * If the url contains username and password, then the password will be replaced as '<pswd>'
     * For example if the connection url is: 'mongodb://user:user_password@192.168.0.1/mydb',
     * then the shadowed url will be: 'mongodb://user:<pswd>@192.168.0.1/mydb'
     *
     * @param url
     * @return shadowed url
     */
    protected String shadowUrlPassword(final String conn) {
        //def conn = 'jdbc:oracle:thin:user:pswd@(DESCRIPTION = (LOAD_BALANCE=off)(FAILOVER=on'
        //def m = conn =~ /^jdbc:oracle:thin:.+:.+@/

        def ptrn = /^mongodb:\/\/.+:.+@/   // define pattern
        def m = conn =~ ptrn               // define matcher

        if (m.size() > 0) {  // match is found
            def str = m[0]   // there can be only one match, get first
            def tokens = str.split(':')    // split by ':'
            tokens[-1] = '<pswd>@'         // replace last token, which is the password trailed by '@'
            str = tokens.join(':')         // rejoin tokens to string

            return conn.replaceFirst(/^mongodb:\/\/.+:.+@/, str)     // replace original conn with shadowed partial
        } else {
            // no match found, do nothing and return original
            return conn
        }
    }

}
