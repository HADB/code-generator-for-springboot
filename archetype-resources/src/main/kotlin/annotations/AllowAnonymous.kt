package ${package}.annotations

import kotlin.annotation.Retention

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AllowAnonymous()