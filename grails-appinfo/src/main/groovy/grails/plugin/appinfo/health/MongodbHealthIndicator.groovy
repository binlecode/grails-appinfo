package grails.plugin.appinfo.health

import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * This Mongodb healthIndicator doesn't depend on Spring mongodb template.
 * Instead, it is based on Grails Mongodb plugin supporting beans.
 * Specifically, it is using Grails 'mongo' bean, which is MongoClient instance.
 */
class MongodbHealthIndicator extends AbstractHealthIndicator {
    final def mongoBean
    final def mongodbConfig
    final def healthMongodbConfig

    static final int DEFAULT_TIMEOUT_MILLIS = 3000

    MongodbHealthIndicator(mongo, mongodbConfig, healthMongodbConfig) {
        this.mongoBean = mongo
        this.mongodbConfig = mongodbConfig
        this.healthMongodbConfig = healthMongodbConfig
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

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

        long timeoutMillis = Long.parseLong(healthMongodbConfig?.timeoutMillis?.toString() ?: DEFAULT_TIMEOUT_MILLIS.toString())
        if (db) {
            try {
                ExecutorService es = Executors.newSingleThreadExecutor()
                Future ds = es.submit({ ->
                    try {
                        def dbStats = mongoBean.getDB(db).getStats()
                        // ['db':'test_grails_appinfo', 'collections':0, 'views':0, 'objects':0, 'avgObjSize':0, 'dataSize':0, 'storageSize':0, 'numExtents':0, 'indexes':0, 'indexSize':0, 'fileSize':0, 'ok':1.0]
                        builder.withDetail('stats', dbStats)
                        builder.up()
                        return dbStats
                    } catch (ex) {
                        builder.down(new Exception("MongoDB check fail: ${ex.message ?: ex.toString()}"))
                        return null
                    }
                })

                ds.get(timeoutMillis, TimeUnit.MILLISECONDS)  // will throw timeout exp
                es.shutdownNow()
            } catch (TimeoutException te) {
                builder.down(new Exception("MongoDB check timed out after ${timeoutMillis} ms"))
            }
        } else {
            builder.down(new Exception("MongoDB not available"))
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