package btpos.unittest

import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.CharArrayWriter
import java.io.OutputStream
import java.io.PrintWriter

private val LOGGER = LoggerFactory.getLogger("UNIT TESTS")

fun bootstrapJUnit() {
    LOGGER.info("Running JUnit!")
    val req = LauncherDiscoveryRequestBuilder.request()
        .selectors(DiscoverySelectors.selectPackage("btpos.unittest"))
        .build()
    
    val summaryGen = SummaryGeneratingListener()
    
    LauncherFactory.openSession().use { session ->
        session.launcher.apply {
            registerTestExecutionListeners(summaryGen)
            execute(req)
        }
    }
    
    summaryGen.summary.printTo(PrintWriter(LoggerOutputStream(LOGGER)))
}

private class LoggerOutputStream(val logger: Logger) : OutputStream() {
    var buffer = CharArrayWriter()
    
    override fun write(b: Int) {
        if (b.toChar() == '\n') {
            // Write a new line to the logger
            logger.info(buffer.toCharArray().concatToString())
            buffer = CharArrayWriter()
        } else {
            buffer.write(b)
        }
    }
    
    override fun flush() {
        super.flush()
        logger.info(buffer.toCharArray().concatToString())
        buffer = CharArrayWriter()
    }
}