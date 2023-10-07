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

    fun edit${model_name_pascal_case}(request: ${model_name_pascal_case}EditRequest): Long {
        val ${model_name_camel_case} = ${model_name_pascal_case}(
${columns_data}
        )
        return edit${model_name_pascal_case}(${model_name_camel_case})
    }

    fun edit${model_name_pascal_case}(${model_name_camel_case}: ${model_name_pascal_case}): Long {
        if (${model_name_camel_case}.id == 0L) {
            ${model_name_camel_case}Mapper.insert${model_name_pascal_case}(${model_name_camel_case})
        } else {
            ${model_name_camel_case}Mapper.update${model_name_pascal_case}(${model_name_camel_case})
        }
        return ${model_name_camel_case}.id
    }

    fun edit${model_name_pascal_case}Partly(request: ${model_name_pascal_case}PartlyEditRequest) {
        ${model_name_camel_case}Mapper.update${model_name_pascal_case}Partly(request)
    }

    fun delete${model_name_pascal_case}(id: Long) {
        ${model_name_camel_case}Mapper.delete${model_name_pascal_case}(id)
    }

    fun get${model_name_pascal_case}ById(id: Long): ${model_name_pascal_case}? {
        return ${model_name_camel_case}Mapper.select${model_name_pascal_case}ById(id)
    }

    fun searchPaging${model_name_plural_pascal_case}(request: ${model_name_pascal_case}SearchRequest): List<${model_name_pascal_case}> {
        return ${model_name_camel_case}Mapper.selectPaging${model_name_plural_pascal_case}(request)
    }

    fun searchPaging${model_name_plural_pascal_case}Count(request: ${model_name_pascal_case}SearchRequest): Long {
        return ${model_name_camel_case}Mapper.selectPaging${model_name_plural_pascal_case}Count(request)
    }
}