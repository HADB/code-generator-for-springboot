package ${package_name}.mappers

import ${package_name}.models.${model_name_pascal_case}
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}PartlyEditRequest
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}SearchRequest
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ${model_name_pascal_case}Mapper {
    fun insert${model_name_pascal_case}(@Param("${model_name_camel_case}") ${model_name_camel_case}: ${model_name_pascal_case})
    fun insertOrUpdate${model_name_pascal_case}(@Param("${model_name_camel_case}") ${model_name_camel_case}: ${model_name_pascal_case})
    fun update${model_name_pascal_case}Partly(@Param("request") request: ${model_name_pascal_case}PartlyEditRequest)
    fun delete${model_name_pascal_case}(@Param("id") id: Long)
    fun select${model_name_pascal_case}ById(@Param("id") id: Long): ${model_name_pascal_case}?
    fun selectPaging${model_name_pascal_case_plural}(@Param("request") request: ${model_name_pascal_case}SearchRequest): List<${model_name_pascal_case}>
    fun select${model_name_pascal_case_plural}Count(@Param("request") request: ${model_name_pascal_case}SearchRequest): Long
}
