package ${package_name}.services

import ${package_name}.configurations.WechatConfiguration
import ${package_name}.constants.AppConstants
import ${package_name}.helpers.PasswordHelper
import ${package_name}.helpers.RedisHelper
import ${package_name}.helpers.TokenHelper
import ${package_name}.helpers.WechatHelper
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

    @Resource
    private lateinit var wechatHelper: WechatHelper

    @Resource
    private lateinit var wechatConfiguration: WechatConfiguration

    fun editUser(request: UserEditRequest): Long {
        val salt = passwordHelper.salt
        var password = passwordHelper.salt
        if (!request.password.isNullOrEmpty()) {
            password = passwordHelper.generate(request.password, salt)
        }
        val user = User(
${add_user_with_password_columns_data}
        )
        return editUser(user)
    }

    fun editUser(user: User): Long {
        if (user.id == 0L) {
            userMapper.insertUser(user)
        } else {
            userMapper.updateUser(user)
        }
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
        return when (service) {
            AppConstants.Service.WEAPP_C -> userMapper.selectUserByOpenId(key)
            else -> userMapper.selectUserById(key.toLong())
        }
    }

    fun searchPagingUsers(request: UserSearchRequest): List<User> {
        return userMapper.selectPagingUsers(request)
    }

    fun searchUsersCount(request: UserSearchRequest): Long {
        return userMapper.selectUsersCount(request)
    }

    fun getUserByOpenId(openId: String): User? {
        return userMapper.selectUserByOpenId(openId)
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

    fun weappSignIn(service: String, code: String): SignInResponse {
        val sessionResult = wechatHelper.getSessionResultByCode(code, wechatConfiguration.weappAppId, wechatConfiguration.weappAppSecret)
        val token = tokenHelper.createToken(service, sessionResult.openId)
        val response = SignInResponse(token = token)
        val user = getUserByOpenId(sessionResult.openId)
        redisHelper.set(RedisKey.sessionKey(sessionResult.openId), sessionResult.sessionKey, 15, TimeUnit.MINUTES)
        if (user != null) {
            response.userExists = true
            response.mobileBound = user.mobile != null
        }
        return response
    }

    fun weappRegister(request: WechatEncryptedDataRequest, openId: String): Response<Any> {
        val sessionKey = redisHelper.get(RedisKey.sessionKey(openId)) ?: return Response.Errors.tokenInvalid()
        val userInfo = wechatHelper.decryptUserProfile(sessionKey, request.encryptedData, request.iv)
        val user = User(
            nickname = userInfo.nickname,
            avatarUrl = userInfo.avatarUrl,
            openId = openId
        )
        userMapper.insertUser(user)
        return Response.success()
    }

    fun weappSignOut(service: String, user: User) {
        tokenHelper.deleteToken(service, user.openId)
    }

    fun bindMobile(request: WechatEncryptedDataRequest, openId: String): Response<Any> {
        val sessionKey = redisHelper.get(RedisKey.sessionKey(openId)) ?: return Response.Errors.tokenInvalid()
        val mobile = wechatHelper.decryptPhoneNumber(sessionKey, request.encryptedData, request.iv)
        val mobileUser = getUserByMobile(mobile)
        val currentUser = getUserByOpenId(openId)!!
        if (mobileUser != null) {
${bind_mobile_columns_data}

            if (currentUser.id != mobileUser.id) {
                userMapper.deleteUser(currentUser.id)
            }
            userMapper.updateUser(mobileUser)
        } else {
            currentUser.mobile = mobile
            userMapper.updateUser(currentUser)
        }
        return Response.success()
    }

    fun getUserByMobile(mobile: String): User? {
        return userMapper.selectUserByMobile(mobile)
    }
}
