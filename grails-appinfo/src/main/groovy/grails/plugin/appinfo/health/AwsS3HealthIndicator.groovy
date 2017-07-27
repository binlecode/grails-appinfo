package grails.plugin.appinfo.health

import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health

/**
 * This S3 healthIndicator is based on Grails AWS SDK S3 module.
 * Basically it tries to fetch endpoint and bucket info from s3 for health check if bucket name is given
 * from Grails config (application.yml for example):
 * appinfo:
 *     aws:
 *         s3:
 *             bucket: {bucket-name}
 *
 */
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

            String bucket = s3Config.bucket
            if (bucket) {
                builder.withDetail('bucket', bucket)

                def bucketExist = s3.doesBucketExist(bucket)
                builder.withDetail('bucketExist', bucketExist)
            }

            builder.up()
        } catch (ex) {
            builder.down(new Exception("S3 check fail: ${ex.message ?: ex.toString()}"))
        }
    }

}
