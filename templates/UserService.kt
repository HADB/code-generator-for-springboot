package ${package_name}.services

import ${package_name}.helpers.PasswordHelper
import ${package_name}.helpers.RedisHelper
import ${package_name}.helpers.TokenHelper
import ${package_name}.mappers.UserMapper
import ${package_name}.models.Permission
import ${package_name}.models.Role
import ${package_name}.models.User
import ${package_name}.viewmodels.user.*
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class UserService {
    @Resource
    private lateinit var userMapper: UserMapper

    @Resource
    private lateinit var tokenHelper: TokenHelper

    @Resource
    private lateinit var passwordHelper: PasswordHelper


    fun getUserFromEditRequest(request: UserEditRequest): User {
        val salt = passwordHelper.salt
        var password = passwordHelper.salt
        if (!request.password.isNullOrEmpty()) {
            password = passwordHelper.generate(request.password, salt)
        }
        return User(
${add_user_with_password_columns_data}
        )
    }

    fun addOrEditUser(request: UserEditRequest): Long? {
        val user = getUserFromEditRequest(request)
        return addOrEditUser(user)
    }

    fun addOrEditUser(user: User): Long? {
        userMapper.insertOrUpdateUser(user)
        return user.id.takeIf { it != 0L }
    }

    fun editUserPartly(request: UserPartlyEditRequest) {
        if (!request.password.isNullOrEmpty()) {
            // 密码不为空，更新密码和盐
            request.salt = passwordHelper.salt
            request.password = passwordHelper.generate(request.password!!, request.salt!!)
        } else {
            // 不更新密码和盐
            request.salt = null
            request.password = null
        }
        userMapper.updateUserPartly(request)
    }

    fun deleteUser(id: Long) {
        userMapper.deleteUser(id)
    }

    fun searchUser(request: UserSearchRequest): User? {
        return userMapper.selectUser(request)
    }

    fun searchUserWithPassword(request: UserSearchRequest): User? {
        return userMapper.selectUserWithPassword(request)
    }

    fun searchUsers(request: UserSearchRequest): List<User> {
        return userMapper.selectUsers(request)
    }

    fun searchUsersCount(request: UserSearchRequest): Long {
        return userMapper.selectUsersCount(request)
    }

    fun getRolesByUserId(userId: Long): List<Role> {
        return userMapper.selectRolesByUserId(userId)
    }

    fun getPermissionsByUserId(userId: Long): List<Permission> {
        return userMapper.selectPermissionsByUserId(userId)
    }

    fun signIn(service: String, user: User): SignInResponse {
        val token = tokenHelper.createToken(service, user.id.toString())
        return SignInResponse(
            token = token
        )
    }

    fun signOut(service: String, user: User) {
        tokenHelper.deleteToken(service, user.id.toString())
    }
}
