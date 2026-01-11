package ${package_name}.annotations

import kotlin.annotation.Retention

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentUser
