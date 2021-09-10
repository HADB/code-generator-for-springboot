package ${package_name}.others

import com.google.common.base.CaseFormat
import io.swagger.annotations.ApiOperation
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
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.Resource

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

        val controllerNames = listOf(${controller_names_text})
        for (controllerName in controllerNames) {
            val controller = Class.forName("${package_name}.controllers.$${controllerName}Controller")
            val controllerMappingPaths = controller.getAnnotation(RequestMapping::class.java).value
            for (controllerMappingPath in controllerMappingPaths) {
                val methods = controller.declaredMethods
                for (method in methods) {
                    val key = "$${CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, controllerName)}_$${CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.name)}"
                    val methodMappingPaths = method.getAnnotation(RequestMapping::class.java).value
                    for (methodMappingPath in methodMappingPaths) {
                        val apiMethods = method.getAnnotation(RequestMapping::class.java).method
                        for (apiMethod in apiMethods) {
                            val permission = permissionService.getPermissionByKey(key)
                            if (permission == null) {
                                permissionService.editPermission(
                                    PermissionEditRequest(
                                        key = key,
                                        name = method.getAnnotation(ApiOperation::class.java).value,
                                        type = 0,
                                        apiPath = "$${controllerMappingPath}$${methodMappingPath}",
                                        apiMethod = apiMethod.name
                                    )
                                )
                            } else {
                                permissionService.editPermission(
                                    PermissionEditRequest(
                                        id = permission.id,
                                        key = key,
                                        name = method.getAnnotation(ApiOperation::class.java).value,
                                        type = 0,
                                        apiPath = "$${controllerMappingPath}$${methodMappingPath}",
                                        apiMethod = apiMethod.name
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
