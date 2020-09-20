package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.CurrentUser
import ${package_name}.helpers.PasswordHelper
import ${package_name}.models.Response
import ${package_name}.models.User
import ${package_name}.services.UserService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.user.*
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Api(tags = ["用户"])
@CrossOrigin
@RestController
@RequestMapping("/user")
class UserController {
    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var passwordHelper: PasswordHelper

    /*
     * 新增
     */
    @ApiOperation(value = "新增「用户」")
    @PostMapping
    fun add(@RequestBody request: UserEditRequest): Response<Any> {
        val userId = userService.editUser(request)
        return Response.success(userId)
    }

    /*
     * 修改
     */
    @ApiOperation(value = "修改「用户」")
    @ApiImplicitParam(name = "id", value = "User ID", required = true, dataTypeClass = Long::class)
    @PutMapping("/{id}")
    fun edit(@PathVariable("id") id: Long, @RequestBody request: UserEditRequest): Response<Any> {
        request.id = id
        val user = userService.getUserById(id) ?: return Response.error("User 不存在")
        userService.editUser(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @ApiOperation(value = "删除「用户」")
    @ApiImplicitParam(name = "id", value = "User ID", required = true, dataTypeClass = Long::class)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        userService.deleteUser(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「用户」详情")
    @ApiImplicitParam(name = "id", value = "User ID", required = true, dataTypeClass = Long::class)
    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long): Response<User> {
        val user = userService.getUserById(id)
        return Response.success(user)
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「用户」")
    @PostMapping("/search")
    fun search(@RequestBody request: UserSearchRequest): Response<SearchResponse<User>> {
        val results = userService.searchPagingUsers(request)
        val count = userService.searchPagingUsersCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }

    /*
     * 检查登录状态
     */
    @GetMapping("/status")
    fun checkStatus(): Response<Any> {
        return Response.success()
    }

    /*
     * 密码登录
     */
    @PostMapping("/password-sign-in")
    @AllowAnonymous
    fun passwordSignIn(@RequestBody request: PasswordSignInRequest, @RequestAttribute service: String): Response<Any> {
        val user = userService.getUserByUsername(request.username) ?: return Response.Errors.accountNotExist()

        if (!passwordHelper.verify(request.password, user)) {
            return Response.Errors.passwordIncorrect()
        }

        val response = userService.signIn(service, user)
        return Response.success(response)
    }

    /*
     * 注销
     */
    @PostMapping("/sign-out")
    fun signOut(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        userService.signOut(service, user)
        return Response.success()
    }

    /*
     * 微信登录
     */
    @ApiOperation(value = "小程序登录")
    @PostMapping("/wxapp-sign-in")
    @AllowAnonymous
    fun wxappSignIn(@RequestBody request: WxappSignInRequest, @RequestAttribute service: String): Response<SignInResponse> {
        val response = userService.wxappSignIn(service, request.code) ?: return Response.Errors.accountNotExist()
        return Response.success(response)
    }

    /*
     * 微信注册
     */
    @ApiOperation(value = "小程序注册")
    @PostMapping("/wxapp-register")
    fun wxappRegister(@RequestBody request: WechatEncryptedDataRequest, @RequestAttribute key: String): Response<Any> {
        if (request.encryptedData == null || request.iv == null) {
            return Response.Errors.wechatNotAuthorized()
        }
        val userInfo = userService.getUserByOpenId(key)
        if (userInfo != null) {
            return Response.Errors.accountAlreadyExist()
        }
        userService.wxappRegister(request, key)
        return Response.success()
    }

    /*
     * 注销
     */
    @ApiOperation(value = "注销登录")
    @PostMapping("/wxapp-sign-out")
    fun wxappSignOut(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        userService.wxappSignOut(service, user)
        return Response.success()
    }

    /*
     * 获取用户信息
     */
    @ApiOperation(value = "用户信息")
    @GetMapping("/info")
    fun info(@RequestAttribute service: String, @RequestAttribute key: String): Response<Any> {
        val userInfo = userService.getUserByKey(service, key)
        return Response.success(userInfo)
    }

    /*
     * 绑定手机号
     */
    @PostMapping("/bind-mobile")
    fun bindMobile(@RequestBody request: WechatEncryptedDataRequest, @RequestAttribute key: String): Response<Any> {
        val response = userService.bindMobile(request, key)
        return Response.success(response)
    }
}
