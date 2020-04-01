package ${package_name}.annotations

import kotlin.annotation.Retention

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Permission(val roleKeys: Array<String> = [])