package ${package_name}.services

import ${package_name}.mappers.RoleMapper
import ${package_name}.models.Role
import ${package_name}.viewmodels.role.RoleEditRequest
import ${package_name}.viewmodels.role.RolePartlyEditRequest
import ${package_name}.viewmodels.role.RoleSearchRequest
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class RoleService {
    @Resource
    private lateinit var roleMapper: RoleMapper

    fun editRole(request: RoleEditRequest): Long {
        val role = Role(
                id = request.id,
                key = request.key,
                name = request.name,
                description = request.description,
                builtIn = request.builtIn
        )
        return editRole(role)
    }

    fun editRole(role: Role): Long {
        if (role.id == 0L) {
            roleMapper.insertRole(role)
        } else {
            roleMapper.updateRole(role)
        }
        return role.id
    }

    fun editRolePartly(request: RolePartlyEditRequest): Long {
        roleMapper.updateRolePartly(request)
        return request.id
    }

    fun deleteRole(id: Long) {
        roleMapper.deleteRole(id)
    }

    fun getRoleById(id: Long): Role? {
        return roleMapper.selectRoleById(id)
    }

    fun searchPagingRoles(request: RoleSearchRequest): List<Role> {
        return roleMapper.selectPagingRoles(request)
    }

    fun searchPagingRolesCount(request: RoleSearchRequest): Long {
        return roleMapper.selectPagingRolesCount(request)
    }

    fun getRoleByKey(key: String): Role? {
        return roleMapper.selectRoleByKey(key)
    }

    fun getRolesByUserId(userId: Long): List<Role> {
        return roleMapper.selectRolesByUserId(userId)
    }
}