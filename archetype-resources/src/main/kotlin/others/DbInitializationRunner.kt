package ${package_name}.others

import io.swagger.v3.oas.annotations.Operation
import ${package_name}.constants.BuiltInRoleKey
import ${package_name}.services.PermissionService
import ${package_name}.services.RoleService
import ${package_name}.services.UserRoleService
import ${package_name}.services.UserService
import ${package_name}.viewmodels.permission.PermissionEditRequest
import ${package_name}.viewmodels.role.RoleEditRequest
import ${package_name}.viewmodels.user.UserEditRequest
import ${package_name}.viewmodels.userRole.UserRoleEditRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.annotation.Resource

@Component
class DbInitializationRunner : CommandLineRunner {
    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var roleService: RoleService

    @Resource
    private lateinit var userRoleService: UserRoleService

    @Resource
    private lateinit var permissionService: PermissionService

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(vararg args: String?) {
        logger.info("db runner started")
        val builtInAdminRoleId = roleService.getRoleByKey(BuiltInRoleKey.Admin)?.id
            ?: roleService.editRole(
                RoleEditRequest(
                    key = BuiltInRoleKey.Admin,
                    name = "管理员",
                    description = "系统内置管理员",
                    builtIn = 1
                )
            ).also {
                logger.info("已创建系统内置管理员角色")
            }

        userService.getUserByUsername("Admin")?.id
            ?: userService.editUser(
                UserEditRequest(
                    username = "Admin",
                    password = "Qcga1WKe3idhi2r1"
                )
            ).also {
                userRoleService.editUserRole(
                    UserRoleEditRequest(
                        userId = it,
                        roleId = builtInAdminRoleId
                    )
                )
                logger.info("已创建初始管理员用户")
            }

        val controllers = getAllControllerClasses("${package_name}.controllers")
        for (controller in controllers) {
            val controllerPaths = controller.getAnnotation(RequestMapping::class.java).value
            for (controllerPath in controllerPaths) {
                val functions = controller.declaredMethods
                for (function in functions) {
                    val summary = function.getAnnotation(Operation::class.java).summary
                    val paths = function.getAnnotation(RequestMapping::class.java).value
                    for (path in paths) {
                        val methods = function.getAnnotation(RequestMapping::class.java).method
                        for (method in methods) {
                            val apiPath = "$${controllerPath}$${path}"
                            val apiMethod = method.name
                            val permissionKey = "$${apiMethod}:$${apiPath}"
                            val permission = permissionService.getPermissionByKey(permissionKey)
                            if (permission == null) {
                                permissionService.editPermission(
                                    PermissionEditRequest(
                                        key = permissionKey,
                                        name = summary,
                                        type = 0,
                                        apiPath = apiPath,
                                        apiMethod = apiMethod
                                    )
                                )
                            } else if (permission.name != summary) {
                                permissionService.editPermission(
                                    PermissionEditRequest(
                                        id = permission.id,
                                        key = permissionKey,
                                        name = summary,
                                        type = 0,
                                        apiPath = apiPath,
                                        apiMethod = apiMethod
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        logger.info("db runner done")
    }

    fun getAllControllerClasses(basePackage: String): List<Class<*>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(RestController::class.java))
        val controllers = ArrayList<Class<*>>()
        for (beanDefinition in scanner.findCandidateComponents(basePackage)) {
            val controllerClass = Class.forName(beanDefinition.beanClassName)
            controllers.add(controllerClass)
        }
        return controllers
    }
}
