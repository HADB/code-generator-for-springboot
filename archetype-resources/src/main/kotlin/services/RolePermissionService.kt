package ${package_name}.services

import ${package_name}.mappers.RolePermissionMapper
import ${package_name}.models.RolePermission
import ${package_name}.viewmodels.rolePermission.RolePermissionEditRequest
import ${package_name}.viewmodels.rolePermission.RolePermissionPartlyEditRequest
import ${package_name}.viewmodels.rolePermission.RolePermissionSearchRequest
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class RolePermissionService {
    @Resource
    private lateinit var rolePermissionMapper: RolePermissionMapper

    fun editRolePermission(request: RolePermissionEditRequest): Long {
        val rolePermission = RolePermission(
            id = request.id,
            roleId = request.roleId,
            permissionId = request.permissionId
        )
        return editRolePermission(rolePermission)
    }

    fun editRolePermission(rolePermission: RolePermission): Long {
        if (rolePermission.id == 0L) {
            rolePermissionMapper.insertRolePermission(rolePermission)
        } else {
            rolePermissionMapper.updateRolePermission(rolePermission)
        }
        return rolePermission.id
    }

    fun editRolePermissionPartly(request: RolePermissionPartlyEditRequest) {
        rolePermissionMapper.updateRolePermissionPartly(request)
    }

    fun deleteRolePermission(id: Long) {
        rolePermissionMapper.deleteRolePermission(id)
    }

    fun getRolePermissionById(id: Long): RolePermission? {
        return rolePermissionMapper.selectRolePermissionById(id)
    }

    fun searchPagingRolePermissions(request: RolePermissionSearchRequest): List<RolePermission> {
        return rolePermissionMapper.selectPagingRolePermissions(request)
    }

    fun searchRolePermissionsCount(request: RolePermissionSearchRequest): Long {
        return rolePermissionMapper.selectRolePermissionsCount(request)
    }
}
