package ${package_name}.services

import ${package_name}.mappers.${model_name_pascal_case}Mapper
import ${package_name}.models.${model_name_pascal_case}
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}EditRequest
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}PartlyEditRequest
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}SearchRequest
import org.springframework.stereotype.Component
import jakarta.annotation.Resource

@Component
class ${model_name_pascal_case}Service {
    @Resource
    private lateinit var ${model_name_camel_case}Mapper: ${model_name_pascal_case}Mapper

    fun get${model_name_pascal_case}FromEditRequest(request: ${model_name_pascal_case}EditRequest): ${model_name_pascal_case} {
        return ${model_name_pascal_case}(
${columns_data}
        )
    }

    fun addOrEdit${model_name_pascal_case}(request: ${model_name_pascal_case}EditRequest): Long? {
        val ${model_name_camel_case} = get${model_name_pascal_case}FromEditRequest(request)
        return addOrEdit${model_name_pascal_case}(${model_name_camel_case})
    }

    fun addOrEdit${model_name_pascal_case}(${model_name_camel_case}: ${model_name_pascal_case}): Long? {
        ${model_name_camel_case}Mapper.insertOrUpdate${model_name_pascal_case}(${model_name_camel_case})
        return ${model_name_camel_case}.id.takeIf { it != 0L }
    }

    fun edit${model_name_pascal_case}Partly(request: ${model_name_pascal_case}PartlyEditRequest) {
        ${model_name_camel_case}Mapper.update${model_name_pascal_case}Partly(request)
    }

    fun delete${model_name_pascal_case}(id: Long) {
        ${model_name_camel_case}Mapper.delete${model_name_pascal_case}(id)
    }

    fun search${model_name_pascal_case}(request: ${model_name_pascal_case}SearchRequest): ${model_name_pascal_case}? {
        return ${model_name_camel_case}Mapper.select${model_name_pascal_case}(request)
    }

    fun search${model_name_pascal_case_plural}(request: ${model_name_pascal_case}SearchRequest): List<${model_name_pascal_case}> {
        return ${model_name_camel_case}Mapper.select${model_name_pascal_case_plural}(request)
    }

    fun search${model_name_pascal_case_plural}Count(request: ${model_name_pascal_case}SearchRequest): Long {
        return ${model_name_camel_case}Mapper.select${model_name_pascal_case_plural}Count(request)
    }
}
