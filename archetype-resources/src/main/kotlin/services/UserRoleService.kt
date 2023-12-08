package ${package_name}.services

import ${package_name}.mappers.UserRoleMapper
import ${package_name}.models.UserRole
import ${package_name}.viewmodels.userRole.UserRoleEditRequest
import ${package_name}.viewmodels.userRole.UserRolePartlyEditRequest
import ${package_name}.viewmodels.userRole.UserRoleSearchRequest
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class UserRoleService {
    @Resource
    private lateinit var userRoleMapper: UserRoleMapper

    fun editUserRole(request: UserRoleEditRequest): Long {
        val userRole = UserRole(
            id = request.id,
            userId = request.userId,
            roleId = request.roleId
        )
        return editUserRole(userRole)
    }

    fun editUserRole(userRole: UserRole): Long {
        if (userRole.id == 0L) {
            userRoleMapper.insertUserRole(userRole)
        } else {
            userRoleMapper.updateUserRole(userRole)
        }
        return userRole.id
    }

    fun editUserRolePartly(request: UserRolePartlyEditRequest) {
        userRoleMapper.updateUserRolePartly(request)
    }

    fun deleteUserRole(id: Long) {
        userRoleMapper.deleteUserRole(id)
    }

    fun getUserRoleById(id: Long): UserRole? {
        return userRoleMapper.selectUserRoleById(id)
    }

    fun searchPagingUserRoles(request: UserRoleSearchRequest): List<UserRole> {
        return userRoleMapper.selectPagingUserRoles(request)
    }

    fun searchUserRolesCount(request: UserRoleSearchRequest): Long {
        return userRoleMapper.selectUserRolesCount(request)
    }
}
