package ${package_name}.mappers

import ${package_name}.models.Role
import ${package_name}.viewmodels.role.RolePartlyEditRequest
import ${package_name}.viewmodels.role.RoleSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface RoleMapper {
    fun insertRole(@Param("role") role: Role)
    fun updateRole(@Param("role") role: Role)
    fun updateRolePartly(@Param("request") request: RolePartlyEditRequest)
    fun deleteRole(@Param("id") id: Long)
    fun selectRoleById(@Param("id") id: Long): Role?
    fun selectPagingRoles(@Param("request") request: RoleSearchRequest): List<Role>
    fun selectPagingRolesCount(@Param("request") request: RoleSearchRequest): Long

    fun selectRoleByKey(@Param("key") key: String): Role
    fun selectRolesByUserId(@Param("userId") userId: Long): List<Role>
}