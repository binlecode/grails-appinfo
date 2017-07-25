package grails.plugin.appinfo.health

import org.springframework.boot.actuate.health.AbstractHealthIndicator
import org.springframework.boot.actuate.health.Health

class UrlHealthIndicator extends AbstractHealthIndicator {
    final int DEFAULT_TIMEOUT = 10 * 1000  // 10 sec
    final String DEFAULT_METHOD = 'HEAD'
    int timeout = DEFAULT_TIMEOUT
    String method = DEFAULT_METHOD
    final String url

    UrlHealthIndicator(String url) {
        this.url = url
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        final HttpURLConnection urlConnection = (HttpURLConnection) url.toURL().openConnection()

        final int responseCode = urlConnection.with {
            requestMethod = method
            readTimeout = timeout
            connectTimeout = timeout
            connect()
            responseCode
        }

        builder.withDetail('url', url)
        builder.withDetail('method', method)
        builder.withDetail('timeout.threshold', "${timeout} ms".toString())

        // If code in 200 to 399 range everything is fine.
        if (responseCode in (200..399)) {
            builder.up()
        } else {
            builder.down(new Exception("Invalid responseCode '${responseCode}' checking '${url}'."))
        }
    }

}
