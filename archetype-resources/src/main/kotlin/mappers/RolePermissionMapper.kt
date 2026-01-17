package ${package_name}.mappers

import ${package_name}.models.RolePermission
import ${package_name}.viewmodels.rolePermission.RolePermissionPartlyEditRequest
import ${package_name}.viewmodels.rolePermission.RolePermissionSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface RolePermissionMapper {
    fun insertOrUpdateRolePermission(@Param("rolePermission") rolePermission: RolePermission)
    fun updateRolePermissionPartly(@Param("request") request: RolePermissionPartlyEditRequest)
    fun deleteRolePermission(@Param("id") id: Long)
    fun selectRolePermissionById(@Param("id") id: Long): RolePermission?
    fun selectPagingRolePermissions(@Param("request") request: RolePermissionSearchRequest): List<RolePermission>
    fun selectRolePermissionsCount(@Param("request") request: RolePermissionSearchRequest): Long
}
