package ${package_name}.services

import ${package_name}.mappers.RoleMapper
import ${package_name}.models.Role
import ${package_name}.viewmodels.role.RoleEditRequest
import ${package_name}.viewmodels.role.RolePartlyEditRequest
import ${package_name}.viewmodels.role.RoleSearchRequest
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class RoleService {
    @Resource
    private lateinit var roleMapper: RoleMapper

    fun getRoleFromEditRequest(request: RoleEditRequest): Role {
        return Role(
            id = request.id,
            key = request.key,
            name = request.name,
            description = request.description,
            builtIn = request.builtIn
        )
    }

    fun addRole(request: RoleEditRequest): Long {
        val role = getRoleFromEditRequest(request)
        return addRole(role)
    }

    fun addRole(role: Role): Long {
        roleMapper.insertRole(role)
        return role.id
    }

    fun editRole(request: RoleEditRequest): Long {
        val role = getRoleFromEditRequest(request)
        return editRole(role)
    }

    fun editRole(role: Role): Long {
        roleMapper.insertOrUpdateRole(role)
        return role.id
    }

    fun editRolePartly(request: RolePartlyEditRequest) {
        roleMapper.updateRolePartly(request)
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

    fun searchRolesCount(request: RoleSearchRequest): Long {
        return roleMapper.selectRolesCount(request)
    }

    fun getRoleByKey(key: String): Role? {
        return roleMapper.selectRoleByKey(key)
    }

    fun getRolesByUserId(userId: Long): List<Role> {
        return roleMapper.selectRolesByUserId(userId)
    }
}
