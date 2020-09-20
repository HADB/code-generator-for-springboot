package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.Permission
import ${package_name}.constants.RoleKey
import ${package_name}.models.Response
import ${package_name}.models.Payment
import ${package_name}.services.PaymentService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.payment.PaymentEditRequest
import ${package_name}.viewmodels.payment.PaymentSearchRequest
import ${package_name}.viewmodels.payment.WxPrepayResponse
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

@Api(tags = ["支付"])
@CrossOrigin
@RestController
@RequestMapping("/payment")
class PaymentController {
    @Resource
    private lateinit var paymentService: PaymentService

    /*
     * 删除
     */
    @ApiOperation(value = "删除「支付」")
    @ApiImplicitParam(name = "id", value = "Payment ID", required = true, dataTypeClass = Long::class)
    @DeleteMapping("/{id}")
    @Permission([RoleKey.Admin])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        paymentService.deletePayment(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「支付」详情")
    @ApiImplicitParam(name = "id", value = "Payment ID", required = true, dataTypeClass = Long::class)
    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long): Response<Payment> {
        val payment = paymentService.getPaymentById(id)
        return Response.success(payment)
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「支付」")
    @PostMapping("/search")
    fun search(@RequestBody request: PaymentSearchRequest): Response<SearchResponse<Payment>> {
        val results = paymentService.searchPagingPayments(request)
        val count = paymentService.searchPagingPaymentsCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }

    /*
     * 预下单
     */
    @ApiOperation(value = "预下单")
    @PostMapping("/{id}/prepay")
    fun prepay(@PathVariable("id") id: Long, servletRequest: HttpServletRequest): Response<WxPrepayResponse> {
        return paymentService.prepay(id, servletRequest)
    }

    /*
     * 微信支付回调通知
     */
    @PostMapping("/notify/wxpay/order")
    @AllowAnonymous
    fun wxpayOrderNotify(@RequestBody xmlData: String): String {
        return paymentService.handleWxpayOrderNotify(xmlData)
    }

    /*
     * 微信退款回调通知
     */
    @PostMapping("/notify/wxpay/refund")
    @AllowAnonymous
    fun wxpayRefundNotify(@RequestBody xmlData: String): String {
        return paymentService.handleWxpayRefundNotify(xmlData)
    }
}