package ${package_name}.mappers

import ${package_name}.models.User
import ${package_name}.models.Role
import ${package_name}.models.Permission
import ${package_name}.viewmodels.user.UserPartlyEditRequest
import ${package_name}.viewmodels.user.UserSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper {
    fun insertOrUpdateUser(@Param("user") user: User)
    fun updateUserPartly(@Param("request") request: UserPartlyEditRequest)
    fun deleteUser(@Param("id") id: Long)
    fun selectUser(@Param("request") request: UserSearchRequest): User?
    fun selectUserWithPassword(@Param("request") request: UserSearchRequest): User?
    fun selectUsers(@Param("request") request: UserSearchRequest): List<User>
    fun selectUsersCount(@Param("request") request: UserSearchRequest): Long

    fun selectRolesByUserId(@Param("userId") userId: Long): List<Role>
    fun selectPermissionsByUserId(@Param("userId") roleId: Long): List<Permission>
}
