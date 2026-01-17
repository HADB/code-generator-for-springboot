package ${package_name}.mappers

import ${package_name}.models.User
import ${package_name}.viewmodels.user.UserPartlyEditRequest
import ${package_name}.viewmodels.user.UserSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper {
    fun insertOrUpdateUser(@Param("user") user: User)
    fun updateUserPartly(@Param("request") request: UserPartlyEditRequest)
    fun deleteUser(@Param("id") id: Long)
    fun selectUserById(@Param("id") id: Long): User?
    fun selectPagingUsers(@Param("request") request: UserSearchRequest): List<User>
    fun selectUsersCount(@Param("request") request: UserSearchRequest): Long

    fun selectUserByUsername(@Param("username") username: String): User?
    fun updateUserPassword(@Param("user") user: User)
    fun selectUserByMobile(@Param("mobile") mobile: String): User?
}
