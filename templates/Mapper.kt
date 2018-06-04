package ${package_name}.mappers

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import ${package_name}.models.${model_upper_camelcase}
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}SearchRequest

@Mapper
interface ${model_upper_camelcase}Mapper {
    fun insert${model_upper_camelcase}(@Param("${model_camelcase}") ${model_camelcase}: ${model_upper_camelcase})
    fun update${model_upper_camelcase}(@Param("${model_camelcase}") ${model_camelcase}: ${model_upper_camelcase})
    fun delete${model_upper_camelcase}(@Param("id") id: Long)
    fun select${model_upper_camelcase}ById(@Param("id") id: Long): ${model_upper_camelcase}
    fun selectAll${model_upper_camelcase}s(): List<${model_upper_camelcase}>
    fun selectPaging${model_upper_camelcase}s(@Param("request") ${model_camelcase}SearchRequest: ${model_upper_camelcase}SearchRequest): List<${model_upper_camelcase}>
    fun selectPaging${model_upper_camelcase}sCount(@Param("request") ${model_camelcase}SearchRequest: ${model_upper_camelcase}SearchRequest): Long
}