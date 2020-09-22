package ${package_name}.mappers

import ${package_name}.models.UserRole
import ${package_name}.viewmodels.userRole.UserRolePartlyEditRequest
import ${package_name}.viewmodels.userRole.UserRoleSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserRoleMapper {
    fun insertUserRole(@Param("userRole") userRole: UserRole)
    fun updateUserRole(@Param("userRole") userRole: UserRole)
    fun updateUserRolePartly(@Param("request") request: UserRolePartlyEditRequest)
    fun deleteUserRole(@Param("id") id: Long)
    fun selectUserRoleById(@Param("id") id: Long): UserRole?
    fun selectPagingUserRoles(@Param("request") request: UserRoleSearchRequest): List<UserRole>
    fun selectPagingUserRolesCount(@Param("request") request: UserRoleSearchRequest): Long
}