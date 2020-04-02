package ${package_name}.services

import ${package_name}.configurations.WxConfiguration
import ${package_name}.constants.AppConstants
import ${package_name}.helpers.PasswordHelper
import ${package_name}.helpers.RedisHelper
import ${package_name}.helpers.TokenHelper
import ${package_name}.helpers.WechatHelper
import ${package_name}.mappers.UserMapper
import ${package_name}.models.User
import ${package_name}.others.RedisKey
import ${package_name}.viewmodels.user.*
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

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
    private lateinit var wxConfiguration: WxConfiguration

    fun editUser(request: UserEditRequest): Long {
        val user = User(
                id = request.id,
                mobile = request.mobile,
                openId = request.openId,
                nickname = request.nickname,
                username = request.username,
                password = request.password,
                salt = request.salt,
                avatarUrl = request.avatarUrl,
                role = request.role,
                balanceSeconds = request.balanceSeconds
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

    fun editUserPartly(request: UserPartlyEditRequest): Long {
        userMapper.updateUserPartly(request)
        return request.id
    }

    fun deleteUser(id: Long) {
        userMapper.deleteUser(id)
    }

    fun getUserById(id: Long): User? {
        return userMapper.selectUserById(id)
    }

    fun getUserByKey(service: String, key: String): User? {
        when (service) {
            AppConstants.Service.DEFAULT -> return userMapper.selectUserById(key.toLong())
            AppConstants.Service.WXAPP_C -> return userMapper.selectUserByOpenId(key)
        }
        return null
    }

    fun searchPagingUsers(request: UserSearchRequest): List<User> {
        return userMapper.selectPagingUsers(request)
    }

    fun searchPagingUsersCount(request: UserSearchRequest): Long {
        return userMapper.selectPagingUsersCount(request)
    }

    fun addUserWithPassword(request: UserEditRequest) {
        val salt = passwordHelper.salt
        var password = passwordHelper.salt
        if (request.password != null && request.password.isNotEmpty()) {
            password = passwordHelper.generate(request.password, salt)
        }
        val user = User(
                username = request.username,
                password = password,
                salt = salt,
                role = request.role
        )
        userMapper.insertUser(user)
    }

    fun editUserPassword(user: User, password: String) {
        val salt = passwordHelper.salt
        val encryptedPassword = passwordHelper.generate(password, salt)
        user.password = encryptedPassword
        user.salt = salt
        userMapper.updateUserPassword(user)
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

    fun wxappSignIn(service: String, code: String): SignInResponse? {
        val sessionResult = wechatHelper.getSessionResultByCode(code, wxConfiguration.wxAppId, wxConfiguration.wxAppSecret)
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

    fun wxappRegister(request: WechatEncryptedDataRequest, openId: String) {
        val sessionKey = redisHelper.get(RedisKey.sessionKey(openId))!! // TODO: session_key 会过期，需处理一下
        val userInfo = wechatHelper.decryptUserInfo(sessionKey, request.encryptedData, request.iv)
        val user = User(
                nickname = userInfo.nickname,
                avatarUrl = userInfo.avatarUrl,
                openId = userInfo.openId
        )
        userMapper.insertUser(user)
    }

    fun wxappSignOut(service: String, user: User) {
        tokenHelper.deleteToken(service, user.openId)
    }

    fun bindMobile(request: WechatEncryptedDataRequest, openId: String) {
        val sessionKey = redisHelper.get(RedisKey.sessionKey(openId))!! // TODO: session_key 会过期，需处理一下
        val mobile = wechatHelper.decryptPhoneNumber(sessionKey, request.encryptedData, request.iv)
        val mobileUser = getUserByMobile(mobile)
        val currentUser = getUserByOpenId(openId)!!
        if (mobileUser != null) {
            mobileUser.openId = currentUser.openId
            mobileUser.nickname = currentUser.nickname
            mobileUser.avatarUrl = currentUser.avatarUrl
            mobileUser.username = currentUser.username
            mobileUser.password = currentUser.password
            mobileUser.salt = currentUser.salt
            mobileUser.role = currentUser.role
            mobileUser.balanceSeconds = currentUser.balanceSeconds

            if (currentUser.id != mobileUser.id) {
                userMapper.deleteUser(currentUser.id)
            }
            userMapper.updateUser(mobileUser)
        } else {
            currentUser.mobile = mobile
            userMapper.updateUser(currentUser)
        }
    }

    fun getUserByMobile(mobile: String): User? {
        return userMapper.selectUserByMobile(mobile)
    }
}