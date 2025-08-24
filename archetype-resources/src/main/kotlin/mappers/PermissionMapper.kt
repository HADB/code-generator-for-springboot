package ${package_name}.mappers

import ${package_name}.models.Permission
import ${package_name}.viewmodels.permission.PermissionPartlyEditRequest
import ${package_name}.viewmodels.permission.PermissionSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface PermissionMapper {
    fun insertPermission(@Param("permission") permission: Permission)
    fun insertOrUpdatePermission(@Param("permission") permission: Permission)
    fun updatePermissionPartly(@Param("request") request: PermissionPartlyEditRequest)
    fun deletePermission(@Param("id") id: Long)
    fun selectPermissionById(@Param("id") id: Long): Permission?
    fun selectPagingPermissions(@Param("request") request: PermissionSearchRequest): List<Permission>
    fun selectPermissionsCount(@Param("request") request: PermissionSearchRequest): Long

    fun selectPermissionsByRoleId(@Param("roleId") roleId: Long): List<Permission>
    fun selectPermissionsByUserId(@Param("userId") roleId: Long): List<Permission>
    fun selectPermissionByKey(@Param("key") key: String): Permission?
}
