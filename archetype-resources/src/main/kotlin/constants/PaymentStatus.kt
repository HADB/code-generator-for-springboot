package ${package_name}.constants

class PaymentStatus {
    companion object {
        const val Created = 0       // 已创建
        const val Prepaid = 1       // 已预下单
        const val Canceled = 2      // 已取消
        const val Paid = 3          // 已支付
        const val Refunded = 4      // 已退款
    }
}
