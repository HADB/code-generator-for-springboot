package ${package_name}.services

import ${package_name}.mappers.PermissionMapper
import ${package_name}.models.Permission
import ${package_name}.viewmodels.permission.PermissionEditRequest
import ${package_name}.viewmodels.permission.PermissionPartlyEditRequest
import ${package_name}.viewmodels.permission.PermissionSearchRequest
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class PermissionService {
    @Resource
    private lateinit var permissionMapper: PermissionMapper

    fun getPermissionFromEditRequest(request: PermissionEditRequest): Permission {
        return Permission(
            id = request.id,
            key = request.key,
            name = request.name,
            description = request.description,
            type = request.type,
            apiPath = request.apiPath,
            apiMethod = request.apiMethod
        )
    }

    fun addPermission(request: PermissionEditRequest): Long {
        val permission = getPermissionFromEditRequest(request)
        return addPermission(permission)
    }

    fun addPermission(permission: Permission): Long {
        permissionMapper.insertPermission(permission)
        return permission.id
    }

    fun editPermission(request: PermissionEditRequest): Long {
        val permission = getPermissionFromEditRequest(request)
        return editPermission(permission)
    }

    fun editPermission(permission: Permission): Long {
        permissionMapper.insertOrUpdatePermission(permission)
        return permission.id
    }

    fun editPermissionPartly(request: PermissionPartlyEditRequest) {
        permissionMapper.insertOrUpdatePermissionPartly(request)
    }

    fun deletePermission(id: Long) {
        permissionMapper.deletePermission(id)
    }

    fun getPermissionById(id: Long): Permission? {
        return permissionMapper.selectPermissionById(id)
    }

    fun searchPagingPermissions(request: PermissionSearchRequest): List<Permission> {
        return permissionMapper.selectPagingPermissions(request)
    }

    fun searchPermissionsCount(request: PermissionSearchRequest): Long {
        return permissionMapper.selectPermissionsCount(request)
    }

    fun getPermissionsByRoleId(roleId: Long): List<Permission> {
        return permissionMapper.selectPermissionsByRoleId(roleId)
    }

    fun getPermissionsByUserId(userId: Long): List<Permission> {
        return permissionMapper.selectPermissionsByUserId(userId)
    }

    fun getPermissionByKey(key: String): Permission? {
        return permissionMapper.selectPermissionByKey(key)
    }
}
