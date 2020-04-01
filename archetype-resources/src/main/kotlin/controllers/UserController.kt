package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.CurrentUser
import ${package_name}.constants.AppConstants
import ${package_name}.helpers.PasswordHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ${package_name}.models.Response
import ${package_name}.models.User
import ${package_name}.services.UserService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.user.*
import springfox.documentation.annotations.ApiIgnore

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
    @ApiImplicitParam(name = "id", value = "User ID", required = true, dataType = "Long")
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
    @ApiImplicitParam(name = "id", value = "User ID", required = true, dataType = "Long")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        userService.deleteUser(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「用户」详情")
    @ApiImplicitParam(name = "id", value = "User ID", required = true, dataType = "Long")
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
    fun passwordSignIn(@RequestBody request: PasswordSignInRequest): Response<Any> {
        val user = userService.getUserByUsername(request.username) ?: return Response.Errors.AccountNotExist()

        if (!passwordHelper.verify(request.password, user)) {
            return Response.Errors.PasswordIncorrect()
        }

        val response = userService.signIn(user)
        return Response.success(response)
    }

    /*
     * 微信登录
     */
    @ApiOperation(value = "小程序登录")
    @PostMapping("/wxapp-sign-in")
    @AllowAnonymous
    fun wxappSignIn(@RequestBody request: WxappSignInRequest): Response<SignInResponse> {
        val response = userService.wxappSignIn(request.code) ?: return Response.Errors.AccountNotExist()
        return Response.success(response)
    }

    /*
     * 微信注册
     */
    @ApiIgnore
    @PostMapping("/wxapp-register")
    @AllowAnonymous
    fun wxappRegister(@RequestBody request: WechatEncryptedDataRequest, @RequestAttribute openId: String): Response<Any> {
        if (request.encryptedData == null || request.iv == null) {
            return Response.Errors.wechatNotAuthorized()
        }
        userService.wxappRegister(request, openId)
        return Response.success()
    }

    /*
     * 注销
     */
    @PostMapping("/sign-out")
    fun signOut(@CurrentUser user: User): Response<Any> {
        userService.signOut(user)
        return Response.success()
    }
}