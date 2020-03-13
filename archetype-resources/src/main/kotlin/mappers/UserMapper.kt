package ${package}.mappers

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import ${package}.models.User
import ${package}.viewmodels.user.UserSearchRequest
import org.springframework.stereotype.Repository

@Mapper
@Repository
interface UserMapper {
    fun insertUser(@Param("user") user: User)
    fun updateUser(@Param("user") user: User)
    fun deleteUser(@Param("id") id: Long)
    fun selectUserById(@Param("id") id: Long): User?
    fun selectPagingUsers(@Param("request") request: UserSearchRequest): List<User>
    fun selectPagingUsersCount(@Param("request") request: UserSearchRequest): Long

    fun selectUserByOpenId(@Param("openId") openId: String): User?
    fun selectUserByUsername(@Param("username") username: String): User?
    fun updateUserPassword(@Param("user") user: User)
}