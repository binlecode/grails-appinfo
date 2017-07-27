package grails.plugin.appinfo.info

import grails.core.GrailsApplication
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor

import java.lang.management.ManagementFactory
import java.lang.management.ThreadInfo
import java.lang.management.ThreadMXBean

class GrailsRuntimeInfoContributor implements InfoContributor {
    private static final ThreadMXBean threadManagementBean = ManagementFactory.getThreadMXBean()
    GrailsApplication grailsApplication

    @Override
    void contribute(Info.Builder builder) {
        if (!grailsApplication) {
            builder.withDetail('grailsApplication', 'not available')
            return
        }

        builder.withDetail('jvm-version', System.getProperty('java.version'))
        builder.withDetail('groovy-version', groovy.lang.GroovySystem.getVersion())
        builder.withDetail('grails-runtime-environment', grails.util.Environment.current.name)
        builder.withDetail('grails-reload-enabled', grails.util.Environment.reloadingAgentEnabled)

        builder.withDetail('grails-runtime-threads-info', getAllThreadsInfo())
    }


    List<ThreadInfo> getAllThreadsInfo() {
        // check if we are running under Java 1.5
        if (threadManagementBean.metaClass.methods.find { it.name == "dumpAllThreads" }) {
            return threadManagementBean.dumpAllThreads(threadManagementBean.isObjectMonitorUsageSupported(),
                    threadManagementBean.isSynchronizerUsageSupported())
        }

        List<ThreadInfo> infos = threadManagementBean.getThreadInfo(threadManagementBean.getAllThreadIds())
        for (ThreadInfo info in infos) {
            // add this methods because _threadInfo wants to access them
            info.metaClass.getLockedMonitors = { -> null }
            info.metaClass.getLockedSynchronizers = { -> null }
        }
        infos
    }


    private static double nanosToMilis(long ns) {
        ns / ((double) 1000000)
    }

    def getThreadDumpData() {
        Map threadDump = [threadCount: threadManagementBean.threadCount,
                          peakThreadCount: threadManagementBean.peakThreadCount,
                          daemonThreadCount: threadManagementBean.daemonThreadCount]

        ThreadInfo[] threadsInfo = getAllThreadsInfo()

        for (ThreadInfo threadInfo in threadsInfo) {
            if (threadManagementBean.threadCpuTimeSupported) {
                def cpuTime = nanosToMilis(threadManagementBean.getThreadCpuTime(threadInfo.threadId))
                threadInfo.metaClass.getCpuTime = { -> cpuTime }
                def userTime = nanosToMilis(threadManagementBean.getThreadUserTime(threadInfo.threadId))
                threadInfo.metaClass.getUserTime = { -> userTime }
            }

            def stackTrace
            if (threadInfo.lockOwnerId != -1) {
                ThreadInfo lockerThreadInfo = threadsInfo.find { ti -> ti.threadId == threadInfo.lockOwnerId }
                stackTrace = lockerThreadInfo.stackTrace as List
            }
            threadInfo.metaClass.getLockStackTrace = { -> stackTrace }
        }

        threadDump.allThreads = threadsInfo

        threadDump
    }


}
