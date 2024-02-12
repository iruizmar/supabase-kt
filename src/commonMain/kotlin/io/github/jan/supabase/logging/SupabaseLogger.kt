package io.github.jan.supabase.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal

abstract class SupabaseLogger {

    abstract val level: LogLevel?

    abstract fun log(level: LogLevel, throwable: Throwable? = null, message: String)

    inline fun log(level: LogLevel, throwable: Throwable? = null, message: () -> String) {
        if (level >= (this.level ?: SupabaseClient.DEFAULT_LOG_LEVEL)) {
            log(level, throwable, message())
        }
    }

    @SupabaseInternal
    abstract fun setLevel(level: LogLevel)

}

class KermitSupabaseLogger(level: LogLevel?, tag: String, private val logger: Logger = Logger.withTag(tag)):
    SupabaseLogger() {

    override var level: LogLevel? = level
        private set

    @SupabaseInternal
    override fun setLevel(level: LogLevel) {
        this.level = level
    }

    override fun log(level: LogLevel, throwable: Throwable?, message:  String) {
        logger.log(level.toSeverity(), logger.tag, throwable, message)
    }

    private fun LogLevel.toSeverity() = when(this) {
        LogLevel.Debug -> Severity.Debug
        LogLevel.Info -> Severity.Info
        LogLevel.Warning -> Severity.Warn
        LogLevel.Error -> Severity.Error
    }

}

inline fun SupabaseLogger.d(throwable: Throwable? = null, message: () -> String) {
    log(LogLevel.Debug, throwable, message)
}

inline fun SupabaseLogger.i(throwable: Throwable? = null, message: () -> String) {
    log(LogLevel.Info, throwable, message)
}

inline fun SupabaseLogger.w(throwable: Throwable? = null, message: () -> String) {
    log(LogLevel.Warning, throwable, message)
}

inline fun SupabaseLogger.e(throwable: Throwable? = null, message: () -> String) {
    log(LogLevel.Error, throwable, message)
}