package ${package_name}.mappers

import ${package_name}.models.Payment
import ${package_name}.viewmodels.payment.PaymentPartlyEditRequest
import ${package_name}.viewmodels.payment.PaymentSearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface PaymentMapper {
    fun insertPayment(@Param("payment") payment: Payment)
    fun updatePayment(@Param("payment") payment: Payment)
    fun updatePaymentPartly(@Param("request") request: PaymentPartlyEditRequest)
    fun deletePayment(@Param("id") id: Long)
    fun selectPaymentById(@Param("id") id: Long): Payment?
    fun selectPagingPayments(@Param("request") request: PaymentSearchRequest): List<Payment>
    fun selectPagingPaymentsCount(@Param("request") request: PaymentSearchRequest): Long

    fun selectPaymentByWxOutTradeNo(@Param("wxOutTradeNo") wxOutTradeNo: String): Payment?
}