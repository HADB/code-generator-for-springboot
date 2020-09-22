package ${package_name}.others

import ${package_name}.constants.BuiltInRoleKey
import ${package_name}.services.RoleService
import ${package_name}.services.UserRoleService
import ${package_name}.services.UserService
import ${package_name}.viewmodels.role.RoleEditRequest
import ${package_name}.viewmodels.user.UserEditRequest
import ${package_name}.viewmodels.userRole.UserRoleEditRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class DbInitializationRunner : CommandLineRunner {
    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var roleService: RoleService

    @Resource
    private lateinit var userRoleService: UserRoleService

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(vararg args: String?) {
        val builtInAdminRoleId = roleService.getRoleByKey(BuiltInRoleKey.Admin)?.id
                ?: roleService.editRole(RoleEditRequest(
                        key = BuiltInRoleKey.Admin,
                        name = "管理员",
                        description = "系统内置管理员",
                        builtIn = 1
                )).also {
                    logger.info("已创建系统内置管理员角色")
                }

        userService.getUserByUsername("Admin")?.id
                ?: userService.addUserWithPassword(UserEditRequest(
                        username = "Admin",
                        password = "Qcga1WKe3idhi2r1"
                )).also {
                    userRoleService.editUserRole(UserRoleEditRequest(
                            userId = it,
                            roleId = builtInAdminRoleId
                    ))
                    logger.info("已创建初始管理员用户")
                }
    }
}
