package ${package_name}.others

import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.Resource
import ${package_name}.constants.BuiltInRoleKey
import ${package_name}.helpers.PasswordHelper
import ${package_name}.services.PermissionService
import ${package_name}.services.RoleService
import ${package_name}.services.UserRoleService
import ${package_name}.services.UserService
import ${package_name}.viewmodels.permission.PermissionEditRequest
import ${package_name}.viewmodels.permission.PermissionSearchRequest
import ${package_name}.viewmodels.role.RoleEditRequest
import ${package_name}.viewmodels.role.RoleSearchRequest
import ${package_name}.viewmodels.user.UserEditRequest
import ${package_name}.viewmodels.user.UserSearchRequest
import ${package_name}.viewmodels.userRole.UserRoleEditRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

    @Resource
    private lateinit var passwordHelper: PasswordHelper

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(vararg args: String) {
        logger.info("db runner started")
        val builtInAdminRoleId = roleService.searchRole(RoleSearchRequest(key = BuiltInRoleKey.Admin))?.id
            ?: roleService.addOrEditRole(
                RoleEditRequest(
                    key = BuiltInRoleKey.Admin,
                    name = "管理员",
                    description = "系统内置管理员",
                    builtIn = 1
                )
            ).also {
                logger.info("已创建系统内置管理员角色")
            }

        userService.searchUser(UserSearchRequest(username = "Admin")) ?: let {
            val password = passwordHelper.random(16)
            val adminUserId = userService.addOrEditUser(
                UserEditRequest(
                    username = "Admin",
                    password = password
                )
            )
            if (adminUserId != null && builtInAdminRoleId != null) {
                userRoleService.addOrEditUserRole(
                    UserRoleEditRequest(
                        userId = adminUserId,
                        roleId = builtInAdminRoleId
                    )
                )
            }
            logger.info("已创建初始管理员用户, 用户名: Admin, 密码: $$password")
        }

        val controllers = getAllControllerClasses("${package_name}.controllers")
        for (controller in controllers) {
            val controllerPaths = controller.getAnnotation(RequestMapping::class.java).value
            for (controllerPath in controllerPaths) {
                val functions = controller.declaredMethods
                for (function in functions) {
                    val summary = function.getAnnotation(Operation::class.java)?.summary ?: continue
                    val paths = function.getAnnotation(RequestMapping::class.java).value
                    for (path in paths) {
                        val methods = function.getAnnotation(RequestMapping::class.java).method
                        for (method in methods) {
                            val apiPath = "$${controllerPath}$${path}"
                            val apiMethod = method.name
                            val permissionKey = "$${apiMethod}:$${apiPath}"
                            val permission = permissionService.searchPermission(PermissionSearchRequest(key = permissionKey))
                            if (permission == null) {
                                permissionService.addOrEditPermission(
                                    PermissionEditRequest(
                                        key = permissionKey,
                                        name = summary,
                                        type = 0,
                                        apiPath = apiPath,
                                        apiMethod = apiMethod
                                    )
                                )
                            } else if (permission.name != summary) {
                                permissionService.addOrEditPermission(
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
