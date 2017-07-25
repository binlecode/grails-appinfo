package grails.plugin.appinfo

/**
 * Health check interface for general service check and return basic service availability information.
 */
interface Checkable {

    /**
     * @return result hash map with keys:
     *  - service: name of the service
     *  - status:  ok, error, or warn
     *  - message: additional details of the checking result
     */
    Map check()
    
}
