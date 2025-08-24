package ${package_name}.services

import ${package_name}.constants.AppConstants
import ${package_name}.helpers.PasswordHelper
import ${package_name}.helpers.RedisHelper
import ${package_name}.helpers.TokenHelper
import ${package_name}.mappers.UserMapper
import ${package_name}.models.Response
import ${package_name}.models.User
import ${package_name}.others.RedisKey
import ${package_name}.viewmodels.user.*
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import jakarta.annotation.Resource

@Component
class UserService {
    @Resource
    private lateinit var userMapper: UserMapper

    @Resource
    private lateinit var tokenHelper: TokenHelper

    @Resource
    private lateinit var passwordHelper: PasswordHelper

    @Resource
    private lateinit var redisHelper: RedisHelper

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

    fun addUser(request: UserEditRequest): Long {
        val user = getUserFromEditRequest(request)
        return addUser(user)
    }

    fun addUser(user: User): Long {
        userMapper.insertUser(user)
        return user.id
    }

    fun editUser(request: UserEditRequest): Long {
        val user = getUserFromEditRequest(request)
        return editUser(user)
    }

    fun editUser(user: User): Long {
        userMapper.insertOrUpdateUser(user)
        return user.id
    }

    fun editUserPartly(request: UserPartlyEditRequest) {
        if (!request.password.isNullOrEmpty()) {
            request.salt = passwordHelper.salt
            request.password = passwordHelper.generate(request.password!!, request.salt!!)
        } else {
            request.salt = null
        }
        userMapper.updateUserPartly(request)
    }

    fun deleteUser(id: Long) {
        userMapper.deleteUser(id)
    }

    fun getUserById(id: Long): User? {
        return userMapper.selectUserById(id)
    }

    fun getUserByKey(service: String, key: String): User? {
        return userMapper.selectUserById(key.toLong())
    }

    fun searchPagingUsers(request: UserSearchRequest): List<User> {
        return userMapper.selectPagingUsers(request)
    }

    fun searchUsersCount(request: UserSearchRequest): Long {
        return userMapper.selectUsersCount(request)
    }

    fun getUserByUsername(username: String): User? {
        return userMapper.selectUserByUsername(username)
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

    fun getUserByMobile(mobile: String): User? {
        return userMapper.selectUserByMobile(mobile)
    }
}
