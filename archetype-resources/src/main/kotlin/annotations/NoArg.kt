package ${package}.annotations

import kotlin.annotation.Retention

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoArg