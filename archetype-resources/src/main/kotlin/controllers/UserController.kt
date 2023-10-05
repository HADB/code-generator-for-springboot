package ${package_name}.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Operation
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.AllowSignedIn
import ${package_name}.annotations.AllowUserNotExist
import ${package_name}.annotations.CurrentUser
import ${package_name}.helpers.PasswordHelper
import ${package_name}.models.Response
import ${package_name}.models.User
import ${package_name}.services.UserService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.user.*
import org.springframework.web.bind.annotation.*
import jakarta.annotation.Resource

@Tag(name = "用户")
@CrossOrigin
@RestController
@RequestMapping("/user")
class UserController {
    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var passwordHelper: PasswordHelper

    @Operation(summary = "新增「用户」")
    @RequestMapping("", method = [RequestMethod.POST])
    fun add(@RequestBody request: UserEditRequest): Response<Any> {
        val userId = userService.editUser(request)
        return Response.success(userId)
    }

    @Operation(summary = "修改「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: UserEditRequest): Response<Any> {
        request.id = id
        userService.getUserById(id) ?: return Response.error("User 不存在")
        userService.editUser(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PATCH])
    fun editPartly(@PathVariable("id") id: Long, @RequestBody request: UserPartlyEditRequest): Response<Any> {
        request.id = id
        userService.getUserById(id) ?: return Response.error("User 不存在")
        userService.editUserPartly(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}/patch", method = [RequestMethod.PUT])
    fun editPartlyCompatible(@PathVariable("id") id: Long, @RequestBody request: UserPartlyEditRequest): Response<Any> {
        request.id = id
        userService.getUserById(id) ?: return Response.error("User 不存在")
        userService.editUserPartly(request)
        return Response.success()
    }

    @Operation(summary = "删除「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        userService.deleteUser(id)
        return Response.success()
    }

    @Operation(summary = "获取「用户」详情")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<User> {
        val user = userService.getUserById(id)
        return Response.success(user)
    }

    @Operation(summary = "搜索「用户」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: UserSearchRequest): Response<SearchResponse<User>> {
        val results = userService.searchPagingUsers(request)
        val count = userService.searchPagingUsersCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }

    @Operation(summary = "检查「用户」登录状态")
    @RequestMapping("/status", method = [RequestMethod.GET])
    @AllowSignedIn
    fun checkStatus(): Response<Any> {
        return Response.success()
    }

    @Operation(summary = "账号密码登录")
    @RequestMapping("/password-sign-in", method = [RequestMethod.POST])
    @AllowAnonymous
    fun passwordSignIn(@RequestBody request: PasswordSignInRequest, @RequestAttribute service: String): Response<Any> {
        val user = userService.getUserByUsername(request.username) ?: return Response.Errors.accountNotExist()

        if (!passwordHelper.verify(request.password, user)) {
            return Response.Errors.passwordIncorrect()
        }

        val response = userService.signIn(service, user)
        return Response.success(response)
    }

    @Operation(summary = "注销")
    @RequestMapping("/sign-out", method = [RequestMethod.POST])
    @AllowSignedIn
    fun signOut(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        userService.signOut(service, user)
        return Response.success()
    }

    @Operation(summary = "小程序登录")
    @RequestMapping(path = ["/weapp-sign-in", "/wxapp-sign-in"], method = [RequestMethod.POST])
    @AllowAnonymous
    fun weappSignIn(@RequestBody request: WeappSignInRequest, @RequestAttribute service: String): Response<SignInResponse> {
        val response = userService.weappSignIn(service, request.code)
        return Response.success(response)
    }

    @Operation(summary = "小程序注册")
    @RequestMapping(path = ["/weapp-register", "/wxapp-register"], method = [RequestMethod.POST])
    @AllowUserNotExist
    fun weappRegister(@RequestBody request: WechatEncryptedDataRequest, @RequestAttribute key: String): Response<Any> {
        if (request.encryptedData == null || request.iv == null) {
            return Response.Errors.wechatNotAuthorized()
        }
        val userInfo = userService.getUserByOpenId(key)
        if (userInfo != null) {
            return Response.Errors.accountAlreadyExist()
        }
        return userService.weappRegister(request, key)
    }

    @Operation(summary = "注销登录")
    @RequestMapping(path = ["/weapp-sign-out", "/wxapp-sign-out"], method = [RequestMethod.POST])
    @AllowSignedIn
    fun weappSignOut(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        userService.weappSignOut(service, user)
        return Response.success()
    }

    @Operation(summary = "用户信息")
    @RequestMapping("/info", method = [RequestMethod.GET])
    @AllowSignedIn
    fun info(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        return Response.success(user)
    }

    @Operation(summary = "绑定「用户」手机号")
    @RequestMapping("/bind-mobile", method = [RequestMethod.POST])
    @AllowSignedIn
    fun bindMobile(@RequestBody request: WechatEncryptedDataRequest, @RequestAttribute key: String): Response<Any> {
        return userService.bindMobile(request, key)
    }
}
