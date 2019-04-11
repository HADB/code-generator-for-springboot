package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ${package_name}.models.Response
import ${package_name}.models.${model_upper_camelcase}
import ${package_name}.services.${model_upper_camelcase}Service
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}EditRequest
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}SearchRequest

@Api(tags = ["${model_description}"])
@CrossOrigin
@RestController
@RequestMapping("/${model_dasherize}")
class ${model_upper_camelcase}Controller {
    @Autowired
    lateinit var ${model_camelcase}Service: ${model_upper_camelcase}Service

    /*
     * 新增
     */
    @ApiOperation(value = "新增「${model_description}」")
    @PostMapping
    fun add(@RequestBody request: ${model_upper_camelcase}EditRequest): Response<Any> {
        val ${model_camelcase}Id = ${model_camelcase}Service.edit${model_upper_camelcase}(request)
        return Response.success(${model_camelcase}Id)
    }

    /*
     * 修改
     */
    @ApiOperation(value = "修改「${model_description}」")
    @ApiImplicitParam(name = "id", value = "${model_upper_camelcase} ID", required = true, dataType = "Long")
    @PutMapping("/{id}")
    fun edit(@PathVariable("id") id: Long, @RequestBody request: ${model_upper_camelcase}EditRequest): Response<Any> {
        request.id = id
        val ${model_camelcase} = ${model_camelcase}Service.get${model_upper_camelcase}ById(id) ?: return Response.error("${model_upper_camelcase} 不存在")
        ${model_camelcase}Service.edit${model_upper_camelcase}(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @ApiOperation(value = "删除「${model_description}」")
    @ApiImplicitParam(name = "id", value = "${model_upper_camelcase} ID", required = true, dataType = "Long")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        ${model_camelcase}Service.delete${model_upper_camelcase}(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「${model_description}」详情")
    @ApiImplicitParam(name = "id", value = "${model_upper_camelcase} ID", required = true, dataType = "Long")
    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long): Response<${model_upper_camelcase}> {
        val ${model_camelcase} = ${model_camelcase}Service.get${model_upper_camelcase}ById(id)
        return Response.success(${model_camelcase})
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「${model_description}」")
    @PostMapping("/search")
    fun search(@RequestBody request: ${model_upper_camelcase}SearchRequest): Response<SearchResponse<${model_upper_camelcase}>> {
        val results = ${model_camelcase}Service.searchPaging${model_upper_camelcase}s(request)
        val count = ${model_camelcase}Service.searchPaging${model_upper_camelcase}sCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}