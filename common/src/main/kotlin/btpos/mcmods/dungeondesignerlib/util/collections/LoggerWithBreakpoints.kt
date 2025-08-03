package btpos.mcmods.dungeondesignerlib.util.collections

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogBuilder
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.message.EntryMessage
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.MessageFactory
import org.apache.logging.log4j.util.MessageSupplier
import org.apache.logging.log4j.util.Supplier

/**
 * Just a logger that delegates to another logger in a place where I can personally set breakpoints
 */
class LoggerWithBreakpoints(val internal: Logger) : Logger {
    
    /**
     * Place to set a breakpoint whenever an error message is called, specifically to diagnose a single issue happening at runtime.
     *
     * Attempting to set a breakpoint on a field access is a recipe for a LAGGY game...
     */
    fun onError() {
        println("Hit breakpoint!")
    }
    
    override fun logMessage(
        level: Level?,
        marker: Marker?,
        fqcn: String?,
        location: StackTraceElement?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.logMessage(level, marker, fqcn, location, message, throwable)
    }
    
    override fun atTrace(): LogBuilder? {
        return internal.atTrace()
    }
    
    override fun atDebug(): LogBuilder? {
        return internal.atDebug()
    }
    
    override fun atInfo(): LogBuilder? {
        return internal.atInfo()
    }
    
    override fun atWarn(): LogBuilder? {
        return internal.atWarn()
    }
    
    override fun atError(): LogBuilder? {
        return internal.atError()
    }
    
    override fun atFatal(): LogBuilder? {
        return internal.atFatal()
    }
    
    override fun always(): LogBuilder? {
        return internal.always()
    }
    
    override fun atLevel(level: Level?): LogBuilder? {
        return internal.atLevel(level)
    }
    
    override fun catching(level: Level?, throwable: Throwable?) {
        internal.catching(level, throwable)
    }
    
    override fun catching(throwable: Throwable?) {
        internal.catching(throwable)
    }
    
    override fun debug(marker: Marker?, message: Message?) {
        internal.debug(marker, message)
    }
    
    override fun debug(
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.debug(
            marker,
            message,
            throwable
        )
    }
    
    override fun debug(
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        internal.debug(
            marker,
            messageSupplier
        )
    }
    
    override fun debug(
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.debug(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun debug(marker: Marker?, message: CharSequence?) {
        internal.debug(marker, message)
    }
    
    override fun debug(
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.debug(
            marker,
            message,
            throwable
        )
    }
    
    override fun debug(marker: Marker?, message: Any?) {
        internal.debug(marker, message)
    }
    
    override fun debug(marker: Marker?, message: Any?, throwable: Throwable?) {
        internal.debug(marker, message, throwable)
    }
    
    override fun debug(marker: Marker?, message: String?) {
        internal.debug(marker, message)
    }
    
    override fun debug(marker: Marker?, message: String?, vararg params: Any?) {
        internal.debug(marker, message, params)
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.debug(
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        throwable: Throwable?
    ) {
        internal.debug(
            marker,
            message,
            throwable
        )
    }
    
    override fun debug(
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        internal.debug(
            marker,
            messageSupplier
        )
    }
    
    override fun debug(
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.debug(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun debug(message: Message?) {
        internal.debug(message)
    }
    
    override fun debug(message: Message?, throwable: Throwable?) {
        internal.debug(message, throwable)
    }
    
    override fun debug(messageSupplier: MessageSupplier?) {
        internal.debug(messageSupplier)
    }
    
    override fun debug(messageSupplier: MessageSupplier?, throwable: Throwable?) {
        internal.debug(messageSupplier, throwable)
    }
    
    override fun debug(message: CharSequence?) {
        internal.debug(message)
    }
    
    override fun debug(message: CharSequence?, throwable: Throwable?) {
        internal.debug(message, throwable)
    }
    
    override fun debug(message: Any?) {
        internal.debug(message)
    }
    
    override fun debug(message: Any?, throwable: Throwable?) {
        internal.debug(message, throwable)
    }
    
    override fun debug(message: String?) {
        internal.debug(message)
    }
    
    override fun debug(message: String?, vararg params: Any?) {
        internal.debug(message, params)
    }
    
    override fun debug(message: String?, vararg paramSuppliers: Supplier<*>?) {
        internal.debug(message, paramSuppliers)
    }
    
    override fun debug(message: String?, throwable: Throwable?) {
        internal.debug(message, throwable)
    }
    
    override fun debug(messageSupplier: Supplier<*>?) {
        internal.debug(messageSupplier)
    }
    
    override fun debug(messageSupplier: Supplier<*>?, throwable: Throwable?) {
        internal.debug(messageSupplier, throwable)
    }
    
    override fun debug(marker: Marker?, message: String?, p0: Any?) {
        internal.debug(marker, message, p0)
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.debug(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun debug(message: String?, p0: Any?) {
        internal.debug(message, p0)
    }
    
    override fun debug(message: String?, p0: Any?, p1: Any?) {
        internal.debug(message, p0, p1)
    }
    
    override fun debug(message: String?, p0: Any?, p1: Any?, p2: Any?) {
        internal.debug(message, p0, p1, p2)
    }
    
    override fun debug(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
        internal.debug(message, p0, p1, p2, p3)
    }
    
    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.debug(
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.debug(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.debug(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.debug(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.debug(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.debug(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun entry() {
        internal.entry()
    }
    
    override fun entry(vararg params: Any?) {
        internal.entry(params)
    }
    
    override fun error(marker: Marker?, message: Message?) {
        onError()
        internal.error(marker, message)
    }
    
    override fun error(
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        onError()
        internal.error(
            marker,
            message,
            throwable
        )
    }
    
    override fun error(
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        onError()
        internal.error(
            marker,
            messageSupplier
        )
    }
    
    override fun error(
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        onError()
        internal.error(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun error(marker: Marker?, message: CharSequence?) {
        onError()
        internal.error(marker, message)
    }
    
    override fun error(
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        onError()
        internal.error(
            marker,
            message,
            throwable
        )
    }
    
    override fun error(marker: Marker?, message: Any?) {
        onError()
        internal.error(marker, message)
    }
    
    override fun error(marker: Marker?, message: Any?, throwable: Throwable?) {
        onError()
        internal.error(marker, message, throwable)
    }
    
    override fun error(marker: Marker?, message: String?) {
        onError()
        internal.error(marker, message)
    }
    
    override fun error(marker: Marker?, message: String?, vararg params: Any?) {
        onError()
        internal.error(marker, message, params)
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        onError()
        internal.error(
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        throwable: Throwable?
    ) {
        onError()
        internal.error(
            marker,
            message,
            throwable
        )
    }
    
    override fun error(
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        onError()
        internal.error(
            marker,
            messageSupplier
        )
    }
    
    override fun error(
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        onError()
        internal.error(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun error(message: Message?) {
        onError()
        internal.error(message)
    }
    
    override fun error(message: Message?, throwable: Throwable?) {
        onError()
        internal.error(message, throwable)
    }
    
    override fun error(messageSupplier: MessageSupplier?) {
        onError()
        internal.error(messageSupplier)
    }
    
    override fun error(messageSupplier: MessageSupplier?, throwable: Throwable?) {
        onError()
        internal.error(messageSupplier, throwable)
    }
    
    override fun error(message: CharSequence?) {
        onError()
        internal.error(message)
    }
    
    override fun error(message: CharSequence?, throwable: Throwable?) {
        onError()
        internal.error(message, throwable)
    }
    
    override fun error(message: Any?) {
        onError()
        internal.error(message)
    }
    
    override fun error(message: Any?, throwable: Throwable?) {
        onError()
        internal.error(message, throwable)
    }
    
    override fun error(message: String?) {
        onError()
        internal.error(message)
    }
    
    override fun error(message: String?, vararg params: Any?) {
        onError()
        internal.error(message, params)
    }
    
    override fun error(message: String?, vararg paramSuppliers: Supplier<*>?) {
        onError()
        internal.error(message, paramSuppliers)
    }
    
    override fun error(message: String?, throwable: Throwable?) {
        onError()
        internal.error(message, throwable)
    }
    
    override fun error(messageSupplier: Supplier<*>?) {
        onError()
        internal.error(messageSupplier)
    }
    
    override fun error(messageSupplier: Supplier<*>?, throwable: Throwable?) {
        onError()
        internal.error(messageSupplier, throwable)
    }
    
    override fun error(marker: Marker?, message: String?, p0: Any?) {
        onError()
        internal.error(marker, message, p0)
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        onError()
        internal.error(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun error(message: String?, p0: Any?) {
        onError()
        internal.error(message, p0)
    }
    
    override fun error(message: String?, p0: Any?, p1: Any?) {
        onError()
        internal.error(message, p0, p1)
    }
    
    override fun error(message: String?, p0: Any?, p1: Any?, p2: Any?) {
        onError()
        internal.error(message, p0, p1, p2)
    }
    
    override fun error(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
        onError()
        internal.error(message, p0, p1, p2, p3)
    }
    
    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        onError()
        internal.error(
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        onError()
        internal.error(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        onError()
        internal.error(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        onError()
        internal.error(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        onError()
        internal.error(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        onError()
        internal.error(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun exit() {
        internal.exit()
    }
    
    override fun <R : Any?> exit(result: R?): R? {
        TODO("Not yet implemented")
    }
    
    override fun fatal(marker: Marker?, message: Message?) {
        internal.fatal(marker, message)
    }
    
    override fun fatal(
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.fatal(
            marker,
            message,
            throwable
        )
    }
    
    override fun fatal(
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        internal.fatal(
            marker,
            messageSupplier
        )
    }
    
    override fun fatal(
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.fatal(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun fatal(marker: Marker?, message: CharSequence?) {
        internal.fatal(marker, message)
    }
    
    override fun fatal(
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.fatal(
            marker,
            message,
            throwable
        )
    }
    
    override fun fatal(marker: Marker?, message: Any?) {
        internal.fatal(marker, message)
    }
    
    override fun fatal(marker: Marker?, message: Any?, throwable: Throwable?) {
        internal.fatal(marker, message, throwable)
    }
    
    override fun fatal(marker: Marker?, message: String?) {
        internal.fatal(marker, message)
    }
    
    override fun fatal(marker: Marker?, message: String?, vararg params: Any?) {
        internal.fatal(marker, message, params)
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.fatal(
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        throwable: Throwable?
    ) {
        internal.fatal(
            marker,
            message,
            throwable
        )
    }
    
    override fun fatal(
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        internal.fatal(
            marker,
            messageSupplier
        )
    }
    
    override fun fatal(
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.fatal(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun fatal(message: Message?) {
        internal.fatal(message)
    }
    
    override fun fatal(message: Message?, throwable: Throwable?) {
        internal.fatal(message, throwable)
    }
    
    override fun fatal(messageSupplier: MessageSupplier?) {
        internal.fatal(messageSupplier)
    }
    
    override fun fatal(messageSupplier: MessageSupplier?, throwable: Throwable?) {
        internal.fatal(messageSupplier, throwable)
    }
    
    override fun fatal(message: CharSequence?) {
        internal.fatal(message)
    }
    
    override fun fatal(message: CharSequence?, throwable: Throwable?) {
        internal.fatal(message, throwable)
    }
    
    override fun fatal(message: Any?) {
        internal.fatal(message)
    }
    
    override fun fatal(message: Any?, throwable: Throwable?) {
        internal.fatal(message, throwable)
    }
    
    override fun fatal(message: String?) {
        internal.fatal(message)
    }
    
    override fun fatal(message: String?, vararg params: Any?) {
        internal.fatal(message, params)
    }
    
    override fun fatal(message: String?, vararg paramSuppliers: Supplier<*>?) {
        internal.fatal(message, paramSuppliers)
    }
    
    override fun fatal(message: String?, throwable: Throwable?) {
        internal.fatal(message, throwable)
    }
    
    override fun fatal(messageSupplier: Supplier<*>?) {
        internal.fatal(messageSupplier)
    }
    
    override fun fatal(messageSupplier: Supplier<*>?, throwable: Throwable?) {
        internal.fatal(messageSupplier, throwable)
    }
    
    override fun fatal(marker: Marker?, message: String?, p0: Any?) {
        internal.fatal(marker, message, p0)
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.fatal(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun fatal(message: String?, p0: Any?) {
        internal.fatal(message, p0)
    }
    
    override fun fatal(message: String?, p0: Any?, p1: Any?) {
        internal.fatal(message, p0, p1)
    }
    
    override fun fatal(message: String?, p0: Any?, p1: Any?, p2: Any?) {
        internal.fatal(message, p0, p1, p2)
    }
    
    override fun fatal(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
        internal.fatal(message, p0, p1, p2, p3)
    }
    
    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.fatal(
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.fatal(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.fatal(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.fatal(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.fatal(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.fatal(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun getLevel(): Level? {
        TODO("Not yet implemented")
    }
    
    override fun <MF : MessageFactory?> getMessageFactory(): MF? {
        TODO("Not yet implemented")
    }
    
    override fun getName(): String? {
        TODO("Not yet implemented")
    }
    
    override fun info(marker: Marker?, message: Message?) {
        internal.info(marker, message)
    }
    
    override fun info(
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.info(
            marker,
            message,
            throwable
        )
    }
    
    override fun info(
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        internal.info(
            marker,
            messageSupplier
        )
    }
    
    override fun info(
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.info(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun info(marker: Marker?, message: CharSequence?) {
        internal.info(marker, message)
    }
    
    override fun info(
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.info(
            marker,
            message,
            throwable
        )
    }
    
    override fun info(marker: Marker?, message: Any?) {
        internal.info(marker, message)
    }
    
    override fun info(marker: Marker?, message: Any?, throwable: Throwable?) {
        internal.info(marker, message, throwable)
    }
    
    override fun info(marker: Marker?, message: String?) {
        internal.info(marker, message)
    }
    
    override fun info(marker: Marker?, message: String?, vararg params: Any?) {
        internal.info(marker, message, params)
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.info(
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun info(marker: Marker?, message: String?, throwable: Throwable?) {
        internal.info(marker, message, throwable)
    }
    
    override fun info(
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        internal.info(
            marker,
            messageSupplier
        )
    }
    
    override fun info(
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.info(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun info(message: Message?) {
        internal.info(message)
    }
    
    override fun info(message: Message?, throwable: Throwable?) {
        internal.info(message, throwable)
    }
    
    override fun info(messageSupplier: MessageSupplier?) {
        internal.info(messageSupplier)
    }
    
    override fun info(messageSupplier: MessageSupplier?, throwable: Throwable?) {
        internal.info(messageSupplier, throwable)
    }
    
    override fun info(message: CharSequence?) {
        internal.info(message)
    }
    
    override fun info(message: CharSequence?, throwable: Throwable?) {
        internal.info(message, throwable)
    }
    
    override fun info(message: Any?) {
        internal.info(message)
    }
    
    override fun info(message: Any?, throwable: Throwable?) {
        internal.info(message, throwable)
    }
    
    override fun info(message: String?) {
        internal.info(message)
    }
    
    override fun info(message: String?, vararg params: Any?) {
        internal.info(message, params)
    }
    
    override fun info(message: String?, vararg paramSuppliers: Supplier<*>?) {
        internal.info(message, paramSuppliers)
    }
    
    override fun info(message: String?, throwable: Throwable?) {
        internal.info(message, throwable)
    }
    
    override fun info(messageSupplier: Supplier<*>?) {
        internal.info(messageSupplier)
    }
    
    override fun info(messageSupplier: Supplier<*>?, throwable: Throwable?) {
        internal.info(messageSupplier, throwable)
    }
    
    override fun info(marker: Marker?, message: String?, p0: Any?) {
        internal.info(marker, message, p0)
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.info(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun info(message: String?, p0: Any?) {
        internal.info(message, p0)
    }
    
    override fun info(message: String?, p0: Any?, p1: Any?) {
        internal.info(message, p0, p1)
    }
    
    override fun info(message: String?, p0: Any?, p1: Any?, p2: Any?) {
        internal.info(message, p0, p1, p2)
    }
    
    override fun info(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
        internal.info(message, p0, p1, p2, p3)
    }
    
    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.info(
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.info(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.info(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.info(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.info(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.info(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun isDebugEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isDebugEnabled(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isEnabled(level: Level?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isEnabled(
        level: Level?,
        marker: Marker?
    ): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isErrorEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isErrorEnabled(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isFatalEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isFatalEnabled(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isInfoEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isInfoEnabled(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isTraceEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isTraceEnabled(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isWarnEnabled(): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun isWarnEnabled(marker: Marker?): Boolean {
        TODO("Not yet implemented")
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: Message?
    ) {
        internal.log(
            level,
            marker,
            message
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            marker,
            message,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        internal.log(
            level,
            marker,
            messageSupplier
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: CharSequence?
    ) {
        internal.log(
            level,
            marker,
            message
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            marker,
            message,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: Any?
    ) {
        internal.log(
            level,
            marker,
            message
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: Any?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            marker,
            message,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?
    ) {
        internal.log(
            level,
            marker,
            message
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        vararg params: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            params
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.log(
            level,
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            marker,
            message,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        internal.log(
            level,
            marker,
            messageSupplier
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun log(level: Level?, message: Message?) {
        internal.log(level, message)
    }
    
    override fun log(
        level: Level?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            message,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        messageSupplier: MessageSupplier?
    ) {
        internal.log(
            level,
            messageSupplier
        )
    }
    
    override fun log(
        level: Level?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            messageSupplier,
            throwable
        )
    }
    
    override fun log(level: Level?, message: CharSequence?) {
        internal.log(level, message)
    }
    
    override fun log(
        level: Level?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            message,
            throwable
        )
    }
    
    override fun log(level: Level?, message: Any?) {
        internal.log(level, message)
    }
    
    override fun log(level: Level?, message: Any?, throwable: Throwable?) {
        internal.log(level, message, throwable)
    }
    
    override fun log(level: Level?, message: String?) {
        internal.log(level, message)
    }
    
    override fun log(level: Level?, message: String?, vararg params: Any?) {
        internal.log(level, message, params)
    }
    
    override fun log(
        level: Level?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.log(
            level,
            message,
            paramSuppliers
        )
    }
    
    override fun log(level: Level?, message: String?, throwable: Throwable?) {
        internal.log(level, message, throwable)
    }
    
    override fun log(
        level: Level?,
        messageSupplier: Supplier<*>?
    ) {
        internal.log(
            level,
            messageSupplier
        )
    }
    
    override fun log(
        level: Level?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.log(
            level,
            messageSupplier,
            throwable
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.log(
            level,
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun log(level: Level?, message: String?, p0: Any?) {
        internal.log(level, message, p0)
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.log(
            level,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun printf(
        level: Level?,
        marker: Marker?,
        format: String?,
        vararg params: Any?
    ) {
        internal.printf(
            level,
            marker,
            format,
            params
        )
    }
    
    override fun printf(level: Level?, format: String?, vararg params: Any?) {
        internal.printf(level, format, params)
    }
    
    override fun <T : Throwable?> throwing(level: Level?, throwable: T?): T? {
        TODO("Not yet implemented")
    }
    
    override fun <T : Throwable?> throwing(throwable: T?): T? {
        TODO("Not yet implemented")
    }
    
    override fun trace(marker: Marker?, message: Message?) {
        internal.trace(marker, message)
    }
    
    override fun trace(
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.trace(
            marker,
            message,
            throwable
        )
    }
    
    override fun trace(
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        internal.trace(
            marker,
            messageSupplier
        )
    }
    
    override fun trace(
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.trace(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun trace(marker: Marker?, message: CharSequence?) {
        internal.trace(marker, message)
    }
    
    override fun trace(
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.trace(
            marker,
            message,
            throwable
        )
    }
    
    override fun trace(marker: Marker?, message: Any?) {
        internal.trace(marker, message)
    }
    
    override fun trace(marker: Marker?, message: Any?, throwable: Throwable?) {
        internal.trace(marker, message, throwable)
    }
    
    override fun trace(marker: Marker?, message: String?) {
        internal.trace(marker, message)
    }
    
    override fun trace(marker: Marker?, message: String?, vararg params: Any?) {
        internal.trace(marker, message, params)
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.trace(
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        throwable: Throwable?
    ) {
        internal.trace(
            marker,
            message,
            throwable
        )
    }
    
    override fun trace(
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        internal.trace(
            marker,
            messageSupplier
        )
    }
    
    override fun trace(
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.trace(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun trace(message: Message?) {
        internal.trace(message)
    }
    
    override fun trace(message: Message?, throwable: Throwable?) {
        internal.trace(message, throwable)
    }
    
    override fun trace(messageSupplier: MessageSupplier?) {
        internal.trace(messageSupplier)
    }
    
    override fun trace(messageSupplier: MessageSupplier?, throwable: Throwable?) {
        internal.trace(messageSupplier, throwable)
    }
    
    override fun trace(message: CharSequence?) {
        internal.trace(message)
    }
    
    override fun trace(message: CharSequence?, throwable: Throwable?) {
        internal.trace(message, throwable)
    }
    
    override fun trace(message: Any?) {
        internal.trace(message)
    }
    
    override fun trace(message: Any?, throwable: Throwable?) {
        internal.trace(message, throwable)
    }
    
    override fun trace(message: String?) {
        internal.trace(message)
    }
    
    override fun trace(message: String?, vararg params: Any?) {
        internal.trace(message, params)
    }
    
    override fun trace(message: String?, vararg paramSuppliers: Supplier<*>?) {
        internal.trace(message, paramSuppliers)
    }
    
    override fun trace(message: String?, throwable: Throwable?) {
        internal.trace(message, throwable)
    }
    
    override fun trace(messageSupplier: Supplier<*>?) {
        internal.trace(messageSupplier)
    }
    
    override fun trace(messageSupplier: Supplier<*>?, throwable: Throwable?) {
        internal.trace(messageSupplier, throwable)
    }
    
    override fun trace(marker: Marker?, message: String?, p0: Any?) {
        internal.trace(marker, message, p0)
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.trace(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun trace(message: String?, p0: Any?) {
        internal.trace(message, p0)
    }
    
    override fun trace(message: String?, p0: Any?, p1: Any?) {
        internal.trace(message, p0, p1)
    }
    
    override fun trace(message: String?, p0: Any?, p1: Any?, p2: Any?) {
        internal.trace(message, p0, p1, p2)
    }
    
    override fun trace(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
        internal.trace(message, p0, p1, p2, p3)
    }
    
    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.trace(
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.trace(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.trace(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.trace(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.trace(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.trace(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun traceEntry(): EntryMessage? {
        TODO("Not yet implemented")
    }
    
    override fun traceEntry(
        format: String?,
        vararg params: Any?
    ): EntryMessage? {
        TODO("Not yet implemented")
    }
    
    override fun traceEntry(vararg paramSuppliers: Supplier<*>?): EntryMessage? {
        TODO("Not yet implemented")
    }
    
    override fun traceEntry(
        format: String?,
        vararg paramSuppliers: Supplier<*>?
    ): EntryMessage? {
        TODO("Not yet implemented")
    }
    
    override fun traceEntry(message: Message?): EntryMessage? {
        TODO("Not yet implemented")
    }
    
    override fun traceExit() {
        internal.traceExit()
    }
    
    override fun <R : Any?> traceExit(result: R?): R? {
        TODO("Not yet implemented")
    }
    
    override fun <R : Any?> traceExit(format: String?, result: R?): R? {
        TODO("Not yet implemented")
    }
    
    override fun traceExit(message: EntryMessage?) {
        internal.traceExit(message)
    }
    
    override fun <R : Any?> traceExit(message: EntryMessage?, result: R?): R? {
        TODO("Not yet implemented")
    }
    
    override fun <R : Any?> traceExit(message: Message?, result: R?): R? {
        TODO("Not yet implemented")
    }
    
    override fun warn(marker: Marker?, message: Message?) {
        internal.warn(marker, message)
    }
    
    override fun warn(
        marker: Marker?,
        message: Message?,
        throwable: Throwable?
    ) {
        internal.warn(
            marker,
            message,
            throwable
        )
    }
    
    override fun warn(
        marker: Marker?,
        messageSupplier: MessageSupplier?
    ) {
        internal.warn(
            marker,
            messageSupplier
        )
    }
    
    override fun warn(
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
        internal.warn(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun warn(marker: Marker?, message: CharSequence?) {
        internal.warn(marker, message)
    }
    
    override fun warn(
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
        internal.warn(
            marker,
            message,
            throwable
        )
    }
    
    override fun warn(marker: Marker?, message: Any?) {
        internal.warn(marker, message)
    }
    
    override fun warn(marker: Marker?, message: Any?, throwable: Throwable?) {
        internal.warn(marker, message, throwable)
    }
    
    override fun warn(marker: Marker?, message: String?) {
        internal.warn(marker, message)
    }
    
    override fun warn(marker: Marker?, message: String?, vararg params: Any?) {
        internal.warn(marker, message, params)
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
        internal.warn(
            marker,
            message,
            paramSuppliers
        )
    }
    
    override fun warn(marker: Marker?, message: String?, throwable: Throwable?) {
        internal.warn(marker, message, throwable)
    }
    
    override fun warn(
        marker: Marker?,
        messageSupplier: Supplier<*>?
    ) {
        internal.warn(
            marker,
            messageSupplier
        )
    }
    
    override fun warn(
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
        internal.warn(
            marker,
            messageSupplier,
            throwable
        )
    }
    
    override fun warn(message: Message?) {
        internal.warn(message)
    }
    
    override fun warn(message: Message?, throwable: Throwable?) {
        internal.warn(message, throwable)
    }
    
    override fun warn(messageSupplier: MessageSupplier?) {
        internal.warn(messageSupplier)
    }
    
    override fun warn(messageSupplier: MessageSupplier?, throwable: Throwable?) {
        internal.warn(messageSupplier, throwable)
    }
    
    override fun warn(message: CharSequence?) {
        internal.warn(message)
    }
    
    override fun warn(message: CharSequence?, throwable: Throwable?) {
        internal.warn(message, throwable)
    }
    
    override fun warn(message: Any?) {
        internal.warn(message)
    }
    
    override fun warn(message: Any?, throwable: Throwable?) {
        internal.warn(message, throwable)
    }
    
    override fun warn(message: String?) {
        internal.warn(message)
    }
    
    override fun warn(message: String?, vararg params: Any?) {
        internal.warn(message, params)
    }
    
    override fun warn(message: String?, vararg paramSuppliers: Supplier<*>?) {
        internal.warn(message, paramSuppliers)
    }
    
    override fun warn(message: String?, throwable: Throwable?) {
        internal.warn(message, throwable)
    }
    
    override fun warn(messageSupplier: Supplier<*>?) {
        internal.warn(messageSupplier)
    }
    
    override fun warn(messageSupplier: Supplier<*>?, throwable: Throwable?) {
        internal.warn(messageSupplier, throwable)
    }
    
    override fun warn(marker: Marker?, message: String?, p0: Any?) {
        internal.warn(marker, message, p0)
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.warn(
            marker,
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
    
    override fun warn(message: String?, p0: Any?) {
        internal.warn(message, p0)
    }
    
    override fun warn(message: String?, p0: Any?, p1: Any?) {
        internal.warn(message, p0, p1)
    }
    
    override fun warn(message: String?, p0: Any?, p1: Any?, p2: Any?) {
        internal.warn(message, p0, p1, p2)
    }
    
    override fun warn(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
        internal.warn(message, p0, p1, p2, p3)
    }
    
    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
        internal.warn(
            message,
            p0,
            p1,
            p2,
            p3,
            p4
        )
    }
    
    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
        internal.warn(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5
        )
    }
    
    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
        internal.warn(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6
        )
    }
    
    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
        internal.warn(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7
        )
    }
    
    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ) {
        internal.warn(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8
        )
    }
    
    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ) {
        internal.warn(
            message,
            p0,
            p1,
            p2,
            p3,
            p4,
            p5,
            p6,
            p7,
            p8,
            p9
        )
    }
}
