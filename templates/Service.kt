package ${package_name}.services

import ${package_name}.mappers.${model_upper_camelcase}Mapper
import ${package_name}.models.${model_upper_camelcase}
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}EditRequest
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}PartlyEditRequest
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}SearchRequest
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class ${model_upper_camelcase}Service {
    @Resource
    private lateinit var ${model_camelcase}Mapper: ${model_upper_camelcase}Mapper

    fun edit${model_upper_camelcase}(request: ${model_upper_camelcase}EditRequest): Long {
        val ${model_camelcase} = ${model_upper_camelcase}(
${columns_data}
        )
        return edit${model_upper_camelcase}(${model_camelcase})
    }

    fun edit${model_upper_camelcase}(${model_camelcase}: ${model_upper_camelcase}): Long {
        if (${model_camelcase}.id == 0L) {
            ${model_camelcase}Mapper.insert${model_upper_camelcase}(${model_camelcase})
        } else {
            ${model_camelcase}Mapper.update${model_upper_camelcase}(${model_camelcase})
        }
        return ${model_camelcase}.id
    }

    fun edit${model_upper_camelcase}Partly(request: ${model_upper_camelcase}PartlyEditRequest): Long {
        ${model_camelcase}Mapper.update${model_upper_camelcase}Partly(request)
        return request.id
    }

    fun delete${model_upper_camelcase}(id: Long) {
        ${model_camelcase}Mapper.delete${model_upper_camelcase}(id)
    }

    fun get${model_upper_camelcase}ById(id: Long): ${model_upper_camelcase}? {
        return ${model_camelcase}Mapper.select${model_upper_camelcase}ById(id)
    }

    fun searchPaging${model_upper_camelcase}s(request: ${model_upper_camelcase}SearchRequest): List<${model_upper_camelcase}> {
        return ${model_camelcase}Mapper.selectPaging${model_upper_camelcase}s(request)
    }

    fun searchPaging${model_upper_camelcase}sCount(request: ${model_upper_camelcase}SearchRequest): Long {
        return ${model_camelcase}Mapper.selectPaging${model_upper_camelcase}sCount(request)
    }
}