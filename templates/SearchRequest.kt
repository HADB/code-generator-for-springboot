package ${package_name}.viewmodels.${model_camelcase}

import ${package_name}.models.Paging

data class ${model_upper_camelcase}SearchRequest (
    val paging: Paging = Paging(1,10)
)