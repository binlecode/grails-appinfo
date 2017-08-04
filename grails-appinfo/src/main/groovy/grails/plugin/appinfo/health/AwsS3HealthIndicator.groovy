package grails.plugin.appinfo.health

import groovy.util.logging.Slf4j
import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health

/**
 * This S3 healthIndicator is based on Grails AWS SDK S3 module.
 * Basically it tries to fetch endpoint and bucket info from s3 for health check if bucket(s) names are given
 * from Grails config (application.yml for example):
 * appinfo:
 *     aws:
 *         s3:
 *             # either:
 *             bucket: {bucket-name}
 *             # or:
 *             buckets:
 *                 - {bucket-name}
 *                 - {another-bucket-name}
 *
 */
@Slf4j
class AwsS3HealthIndicator extends AbstractHealthIndicator {
    final def awsWebService
    final def s3
    def s3Config

    AwsS3HealthIndicator(amazonWebService) {
        this.awsWebService = amazonWebService
        this.s3 = awsWebService.s3
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        try {
            def endpoint = s3.getEndpoint()
            builder.withDetail('endpoint', endpoint)

            List<String> bucketsToCheck = []

            if (s3Config.bucket) {
                bucketsToCheck.add(s3Config.bucket as String)
            } else if (s3Config.buckets) {
                bucketsToCheck.addAll(s3Config.buckets as List)
            }

            if (!bucketsToCheck) {
                builder.withDetail('buckets', 'none')
                log.warn "S3 check can not find any bucket configured, bucket check skipped!"
                return
            }

            def bucketsCheck = []
            def allCheckOk = true
            bucketsToCheck.each { String bucket ->
                def bucketDetails = [bucket: bucket]

                if (s3.doesBucketExist(bucket)) {
                    bucketDetails.exists = true
                } else {
                    bucketDetails.exists = false
                    log.warn "S3 check error: bucket ${bucket} does not exist"
                    allCheckOk = false
                }
                bucketsCheck << bucketDetails
            }
            builder.withDetail('buckets', bucketsCheck)

            if (allCheckOk) {
                builder.up()
            } else {
                builder.down(new Exception('S3 check fail: bucket not all available'))
            }
        } catch (ex) {
            builder.down(new Exception("S3 check fail: ${ex.message ?: ex.toString()}"))
        }
    }

}
