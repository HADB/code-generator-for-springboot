# coding=utf-8
"""
crud-code-generator-for-springboot
"""
import getopt
import inflection
import os
import shutil
import string
import sys

CURRENT_PATH = os.getcwd()  # 当前目录
TEMPLATE_PATH = os.path.join(CURRENT_PATH, "templates")  # 模板目录
ARCHETYPE_RESOURCE_PATH = os.path.join(CURRENT_PATH, "archetype-resources")  # 原型资源目录

project_path = None
package_name = None
group_id = None
artifact_id = None
version = None
description = None
port = None
registry_instance = None
registry_namespace = None
registry_username = None
registry_password = None
payment = False
controller_names = []


def init_project():
    print("初始化项目，复制 .sql 文件")
    g = os.walk(ARCHETYPE_RESOURCE_PATH)
    for path, _, file_list in g:
        for file_name in file_list:
            if file_name == ".DS_Store" or (not payment and file_name == "t_payment.sql"):
                continue
            sub_path = path[len(ARCHETYPE_RESOURCE_PATH) + 1 :]

            with open(os.path.join(path, file_name), "r", encoding="utf-8") as file_read:
                if "src/main/resources/sql" in path or "src\\main\\resources\\sql" in path:
                    directory_path = os.path.join(project_path, sub_path)
                    if not os.path.exists(directory_path):
                        os.makedirs(directory_path)
                    file_path = os.path.join(directory_path, file_name)
                    print(file_path)
                    content = file_read.read()
                    with open(file_path, "w", encoding="utf-8") as file_write:
                        file_write.write(content)


def get_column_type_property_name(column):
    column_type = "String"
    property_name = column["name"]
    if column["type"] == "bigint":
        column_type = "Long"
    elif column["type"] == "double":
        column_type = "Double"
    elif column["type"] == "decimal":
        column_type = "BigDecimal"
    elif column["type"] == "tinyint" and column["name"].startswith("is_"):
        column_type = "Boolean"
        property_name = column["name"][3:]
    elif column["type"] == "tinyint" or column["type"] == "int":
        column_type = "Int"
    elif column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
        column_type = "Date"
    return column_type, property_name


def copy_archetype_resources():
    print("复制骨架资源文件")
    g = os.walk(ARCHETYPE_RESOURCE_PATH)
    controller_names.sort()
    controller_names_text = ", ".join(controller_names)
    for path, _, file_list in g:
        for file_name in file_list:
            if file_name == ".DS_Store":
                continue
            sub_path = path[len(ARCHETYPE_RESOURCE_PATH) + 1 :]

            with open(os.path.join(path, file_name), "r", encoding="utf-8") as file_read:
                directory_path = os.path.join(project_path, sub_path)
                if "src/main/kotlin" in path or "src\\main\\kotlin" in path:
                    directory_path = os.path.join(
                        project_path,
                        "src/main/kotlin",
                        *package_name.split("."),
                        sub_path[len("src/main/kotlin") + 1 :]
                    )
                if not os.path.exists(directory_path):
                    os.makedirs(directory_path)
                file_path = os.path.join(directory_path, file_name)
                if os.path.exists(file_path) and os.path.splitext(file_name)[-1] == ".sql":
                    print("跳过:" + file_path)
                    continue
                if not payment and file_name in [
                    "t_payment.sql",
                    "PaymentController.kt",
                    "PaymentService.kt",
                    "PaymentMapper.kt",
                    "PaymentStatus.kt",
                    "PaymentMapper.xml",
                    "WxPrepayResponse.kt",
                ]:
                    continue
                print("准备复制:" + file_path)
                content = file_read.read()
                t = string.Template(content)
                content = t.substitute(
                    package_name=package_name,
                    group_id=group_id,
                    artifact_id=artifact_id,
                    version=version,
                    description=description,
                    port=port,
                    registry_instance=registry_instance,
                    registry_namespace=registry_namespace,
                    registry_username=registry_username,
                    registry_password=registry_password,
                    project_path=project_path,
                    controller_names_text=controller_names_text,
                )
                with open(file_path, "w", encoding="utf-8") as file_write:
                    file_write.write(content)
                    print("已复制:" + file_path)


def run_package():
    print("执行 CRUD")
    input_path = os.path.join(project_path, "src/main/resources/sql")
    # kotlin 输出目录
    kotlin_output_path = os.path.join(project_path, "src", "main", "kotlin", *package_name.split("."))
    # mapper 输出目录
    mapper_output_path = os.path.join(project_path, "src", "main", "resources", "mapper")
    if os.path.exists(kotlin_output_path):
        shutil.rmtree(kotlin_output_path)
    if os.path.exists(mapper_output_path):
        shutil.rmtree(mapper_output_path)

    for input_file_name in os.listdir(input_path):
        input_file_path = os.path.join(input_path, input_file_name)
        if not os.path.isdir(input_file_path):
            if not input_file_name.endswith(".sql"):
                print("跳过: " + input_file_name)
                continue
            print("处理: " + input_file_name)
            file_name = os.path.splitext(input_file_name)[0].strip()  # 文件名
            if not payment and file_name == "t_payment":
                continue
            table_name = file_name  # 表名默认为文件名
            table_description = table_name  # 表注释默认为文件名
            file_read = open(input_file_path, "r", encoding="UTF-8")
            columns = []  # 字段数组

            for line in file_read:
                if line.find("CREATE TABLE ") >= 0:
                    table_name = line.strip().split()[2].strip("`")[2:]  # 读取表名
                    controller_names.append('"%s"' % inflection.camelize(table_name))
                    continue
                if line.find(" KEY ") >= 0:
                    continue  # 跳过索引
                if line.find("CHARSET=") >= 0:
                    table_description = line[line.find("COMMENT") + 8 :].split("'")[1]  # 读取表注释
                    continue
                if len(line.strip().split()) < 2:
                    continue
                column = {
                    "name": line.strip().split()[0].strip("`"),
                    "type": line.strip().split()[1].split("(")[0].lower(),
                }
                if line.find("NOT NULL ") > 0:
                    column["nullable"] = False  # 字段是否可空
                else:
                    column["nullable"] = True

                if line.find("DEFAULT ") > 0:
                    column["default"] = line[line.find("DEFAULT ") + 8 :].split()[0].replace("'", '"')  # 字段默认值
                    if column["default"] == "NULL" or column["default"] == "CURRENT_TIMESTAMP":
                        column["default"] = "null"
                    if column["type"].find("decimal") >= 0:
                        column["default"] = "BigDecimal(%s)" % column["default"].replace('"', "")
                    if (
                        column["type"].find("int") >= 0
                        or column["type"].find("double") >= 0
                        or column["type"].find("decimal") >= 0
                    ) and column["default"].find('"') >= 0:
                        column["default"] = column["default"].replace('"', "")
                    if column["type"] == "tinyint" and column["name"].startswith("is_"):
                        if column["default"] == "0":
                            column["default"] = "false"
                        else:
                            column["default"] = "true"
                else:
                    column["default"] = None

                column["comment"] = line[line.find("COMMENT") + 8 :].split("'")[1]  # 字段注释
                columns.append(column)
            file_read.close()

            # [Model]Mapper.xml
            lines = []
            lines_without_password = []
            for column in columns:
                property_name = column["name"]
                if column["name"] == "is_delete":
                    continue
                if column["name"].startswith("is_"):
                    property_name = column["name"][3:]

                lines.append(
                    "        `%s`.`%s` as `%s`"
                    % (
                        inflection.camelize(table_name, False),
                        column["name"],
                        inflection.camelize(property_name, False),
                    )
                )
                if table_name == "user" and column["name"] != "password" and column["name"] != "salt":
                    lines_without_password.append(
                        "        `%s`.`%s` as `%s`"
                        % (
                            inflection.camelize(table_name, False),
                            column["name"],
                            inflection.camelize(property_name, False),
                        )
                    )
            column_list = ",\n".join(lines)
            if table_name == "user":
                column_list_without_password = ",\n".join(lines_without_password)

            lines = []
            for column in columns:
                if (
                    column["name"] == "id"
                    or column["name"] == "sort_weight"
                    or (table_name == "user" and (column["name"] == "password" or column["name"] == "salt"))
                ):
                    continue
                if column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
                    lines.append(
                        '        <if test="request.%sFrom != null">' % (inflection.camelize(column["name"], False))
                    )
                    lines.append(
                        "            AND `%s`.`%s` &gt;= #{request.%sFrom}"
                        % (
                            inflection.camelize(table_name, False),
                            column["name"],
                            inflection.camelize(column["name"], False),
                        )
                    )
                    lines.append("        </if>")
                    lines.append(
                        '        <if test="request.%sTo != null">' % (inflection.camelize(column["name"], False))
                    )
                    lines.append(
                        "            AND `%s`.`%s` &lt;= #{request.%sTo}"
                        % (
                            inflection.camelize(table_name, False),
                            column["name"],
                            inflection.camelize(column["name"], False),
                        )
                    )
                    lines.append("        </if>")
                    continue
                if column["name"] == "is_delete":
                    lines.append("        AND `%s`.`%s` = 0" % (inflection.camelize(table_name, False), column["name"]))
                    continue
                if column["type"] == "varchar" or column["type"] == "text":
                    lines.append(
                        "        <if test=\"request.%s != null and request.%s !=''\">"
                        % (
                            inflection.camelize(column["name"], False),
                            inflection.camelize(column["name"], False),
                        )
                    )
                elif column["name"].startswith("is_"):
                    lines.append(
                        '        <if test="request.%s != null">' % (inflection.camelize(column["name"][3:], False))
                    )
                else:
                    lines.append(
                        '        <if test="request.%s != null">' % (inflection.camelize(column["name"], False))
                    )
                if column["name"].startswith("is_"):
                    lines.append(
                        "            AND `%s`.`%s` = #{request.%s}"
                        % (
                            inflection.camelize(table_name, False),
                            column["name"],
                            inflection.camelize(column["name"][3:], False),
                        )
                    )
                else:
                    lines.append(
                        "            AND `%s`.`%s` = #{request.%s}"
                        % (
                            inflection.camelize(table_name, False),
                            column["name"],
                            inflection.camelize(column["name"], False),
                        )
                    )
                lines.append("        </if>")
            search_where = "\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id" or column["name"] == "is_delete":
                    continue
                lines.append("        `%s`" % (column["name"]))
            name_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id" or column["name"] == "is_delete":
                    continue
                elif (
                    column["name"] == "create_time"
                    or column["name"] == "update_time"
                    or column["name"] == "created_time"
                    or column["name"] == "updated_time"
                ):
                    lines.append("        NOW()")
                elif column["name"].startswith("is_"):
                    lines.append(
                        "        #{%s.%s}"
                        % (
                            inflection.camelize(table_name, False),
                            inflection.camelize(column["name"][3:], False),
                        )
                    )
                else:
                    lines.append(
                        "        #{%s.%s}"
                        % (
                            inflection.camelize(table_name, False),
                            inflection.camelize(column["name"], False),
                        )
                    )
            value_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if (
                    column["name"] == "id"
                    or column["name"] == "create_time"
                    or column["name"] == "created_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                elif column["name"] == "update_time" or column["name"] == "updated_time":
                    lines.append("        `%s` = NOW()" % (column["name"]))
                elif column["name"].startswith("is_"):
                    lines.append(
                        "        `%s` = #{%s.%s}"
                        % (
                            column["name"],
                            inflection.camelize(table_name, False),
                            inflection.camelize(column["name"][3:], False),
                        )
                    )
                else:
                    lines.append(
                        "        `%s` = #{%s.%s}"
                        % (
                            column["name"],
                            inflection.camelize(table_name, False),
                            inflection.camelize(column["name"], False),
                        )
                    )
            update_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if (
                    column["name"] == "id"
                    or column["name"] == "create_time"
                    or column["name"] == "created_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                elif column["name"] == "update_time" or column["name"] == "updated_time":
                    lines.append("        `%s` = NOW()" % (column["name"]))
                elif column["name"].startswith("is_"):
                    lines.append(
                        '        <if test="request.%s != null">' % (inflection.camelize(column["name"][3:], False))
                    )
                    lines.append(
                        "            `%s` = #{request.%s},"
                        % (
                            column["name"],
                            inflection.camelize(column["name"][3:], False),
                        )
                    )
                    lines.append("        </if>")
                else:
                    if column["type"] == "varchar" or column["type"] == "text":
                        lines.append(
                            "        <if test=\"request.%s != null and request.%s !=''\">"
                            % (
                                inflection.camelize(column["name"], False),
                                inflection.camelize(column["name"], False),
                            )
                        )
                    else:
                        lines.append(
                            '        <if test="request.%s != null">' % (inflection.camelize(column["name"], False))
                        )
                    lines.append(
                        "            `%s` = #{request.%s},"
                        % (column["name"], inflection.camelize(column["name"], False))
                    )
                    lines.append("        </if>")
            update_partly_list = "\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "sort_weight":
                    lines.append("`%s`.`sort_weight` DESC" % (inflection.camelize(table_name, False)))
            if not lines:
                lines.append("`%s`.`id` DESC" % (inflection.camelize(table_name, False)))
            orders = ", ".join(lines)

            if table_name == "user":
                file_read = open(os.path.join(TEMPLATE_PATH, "UserMapper.xml"), "r", encoding="UTF-8")
            else:
                file_read = open(os.path.join(TEMPLATE_PATH, "Mapper.xml"), "r", encoding="UTF-8")

            content = file_read.read()
            t = string.Template(content)
            if table_name == "user":
                content = t.substitute(
                    package_name=package_name,
                    column_list_without_password=column_list_without_password,
                    search_where=search_where,
                    orders=orders,
                    name_list=name_list,
                    value_list=value_list,
                    update_list=update_list,
                    update_partly_list=update_partly_list,
                )
            else:
                content = t.substitute(
                    table_name="t_" + table_name,
                    package_name=package_name,
                    model_upper_camelcase=inflection.camelize(table_name),
                    model_camelcase=inflection.camelize(table_name, False),
                    column_list=column_list,
                    search_where=search_where,
                    orders=orders,
                    name_list=name_list,
                    value_list=value_list,
                    update_list=update_list,
                    update_partly_list=update_partly_list,
                )

            if not os.path.exists(mapper_output_path):
                os.makedirs(mapper_output_path)

            file_write = open(
                os.path.join(mapper_output_path, inflection.camelize(table_name) + "Mapper.xml"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model].kt
            content = "package %s.models\n\n" % package_name
            content += "import io.swagger.annotations.ApiModelProperty\n"
            content += "import %s.annotations.NoArg\n" % package_name
            content += "import java.math.BigDecimal\n"
            content += "import java.util.*\n\n"
            content += "@NoArg\n"
            content += "data class %s(\n" % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                if column["name"] == "is_delete":
                    continue
                else:
                    column_type, property_name = get_column_type_property_name(column)

                    if column["nullable"]:
                        column_type += "?"
                    if column["default"]:
                        column_type += " = " + column["default"]
                    if column["name"] == "id":
                        column_type += " = 0"
                    line_text = '    @ApiModelProperty(position = %s, notes = "%s")\n' % (
                        swagger_index,
                        column["comment"],
                    )

                    # 特殊处理 for Payment.kt
                    if table_name == "payment" and (
                        property_name == "status"
                        or property_name == "wx_transaction_id"
                        or property_name == "wx_payment_open_id"
                        or property_name == "message"
                        or property_name == "payment_time"
                    ):
                        line_text += "    var %s: %s" % (
                            inflection.camelize(property_name, False),
                            column_type,
                        )

                    # 特殊处理 for User.kt
                    elif table_name == "user" and (
                        property_name != "id" and property_name != "create_time" and property_name != "update_time"
                    ):
                        line_text += "    var %s: %s" % (
                            inflection.camelize(property_name, False),
                            column_type,
                        )
                    else:
                        line_text += "    val %s: %s" % (
                            inflection.camelize(property_name, False),
                            column_type,
                        )
                    lines.append(line_text)
                    swagger_index += 1
            content += "%s,\n" % (",\n\n".join(lines))
            content += ")\n"

            output_models_path = os.path.join(kotlin_output_path, "models")
            if not os.path.exists(output_models_path):
                os.makedirs(output_models_path)
            file_write = open(
                os.path.join(output_models_path, inflection.camelize(table_name) + ".kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]EditRequest.kt
            content = "package %s.viewmodels.%s\n\n" % (
                package_name,
                inflection.camelize(table_name, False),
            )
            content += "import io.swagger.annotations.ApiModelProperty\nimport java.math.BigDecimal\nimport java.util.*\nimport javax.validation.constraints.NotNull\n\n"
            content += "data class %sEditRequest(\n" % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                define = "val"
                required = True
                hidden = False
                line_text = ""
                column_type, property_name = get_column_type_property_name(column)

                if column["nullable"]:
                    column_type += "?"
                    required = False
                if column["default"]:
                    column_type += " = " + column["default"]
                if column["name"] == "id":
                    define = "var"
                    required = False
                    hidden = True
                if (
                    column["name"] == "create_time"
                    or column["name"] == "update_time"
                    or column["name"] == "created_time"
                    or column["name"] == "updated_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                if column["name"] == "id":
                    column_type += " = 0"
                if required:
                    line_text += '    @NotNull(message = "%s 不能为空")\n' % (inflection.camelize(property_name, False))
                line_text += '    @ApiModelProperty(position = %s, notes = "%s", required = %s, hidden = %s)\n' % (
                    swagger_index,
                    column["comment"],
                    "true" if required else "false",
                    "true" if hidden else "false",
                )
                line_text += "    %s %s: %s" % (
                    define,
                    inflection.camelize(property_name, False),
                    column_type,
                )
                lines.append(line_text)
                swagger_index += 1
            content += "%s,\n" % (",\n\n".join(lines))
            content += ")\n"

            output_viewmodels_path = os.path.join(
                kotlin_output_path, "viewmodels", inflection.camelize(table_name, False)
            )
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(
                os.path.join(
                    output_viewmodels_path,
                    inflection.camelize(table_name) + "EditRequest.kt",
                ),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]PartlyEditRequest.kt
            content = "package %s.viewmodels.%s\n\n" % (
                package_name,
                inflection.camelize(table_name, False),
            )
            content += "import io.swagger.annotations.ApiModelProperty\nimport java.math.BigDecimal\nimport java.util.*\nimport javax.validation.constraints.NotNull\n\n"
            content += "data class %sPartlyEditRequest(\n" % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                define = "var"
                hidden = "true"
                if (
                    column["name"] == "create_time"
                    or column["name"] == "update_time"
                    or column["name"] == "created_time"
                    or column["name"] == "updated_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                column_type, property_name = get_column_type_property_name(column)
                if column["name"] != "id":
                    define = "val"
                    hidden = "false"
                    column_type += "? = null"
                # 特殊处理 for UserPartlyEditRequest.kt
                if table_name == "user" and (column["name"] == "password" or column["name"] == "salt"):
                    define = "var"
                line_text = '    @ApiModelProperty(position = %s, notes = "%s", required = false, hidden = %s)\n' % (
                    swagger_index,
                    column["comment"],
                    hidden,
                )
                line_text += "    %s %s: %s" % (
                    define,
                    inflection.camelize(property_name, False),
                    column_type,
                )
                lines.append(line_text)
                swagger_index += 1
            content += "%s,\n" % (",\n\n".join(lines))
            content += ")\n"

            output_viewmodels_path = os.path.join(
                kotlin_output_path, "viewmodels", inflection.camelize(table_name, False)
            )
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(
                os.path.join(
                    output_viewmodels_path,
                    inflection.camelize(table_name) + "PartlyEditRequest.kt",
                ),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]SearchRequest.kt
            content = "package %s.viewmodels.%s\n\n" % (
                package_name,
                inflection.camelize(table_name, False),
            )
            content += (
                "import io.swagger.annotations.ApiModelProperty\nimport %s.models.Paging\nimport %s.viewmodels.common.SortOrder\nimport java.math.BigDecimal\nimport java.util.*\n\n"
                % (package_name, package_name)
            )
            content += "data class %sSearchRequest(\n" % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                if column["name"] == "id" or column["name"] == "sort_weight" or column["name"] == "is_delete":
                    continue
                column_type, property_name = get_column_type_property_name(column)
                column_type += "? = null"
                define = "val"
                if column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
                    line_text = '    @ApiModelProperty(position = %s, notes = "%s From")\n' % (
                        swagger_index,
                        column["comment"],
                    )
                    line_text += "    %s %s: %s" % (
                        define,
                        inflection.camelize(column["name"] + "From", False),
                        column_type,
                    )
                    lines.append(line_text)
                    swagger_index += 1
                    line_text = '    @ApiModelProperty(position = %s, notes = "%s To")\n' % (
                        swagger_index,
                        column["comment"],
                    )
                    line_text += "    %s %s: %s" % (
                        define,
                        inflection.camelize(column["name"] + "To", False),
                        column_type,
                    )
                    lines.append(line_text)
                    swagger_index += 1
                    continue

                line_text = '    @ApiModelProperty(position = %s, notes = "%s")\n' % (
                    swagger_index,
                    column["comment"],
                )
                line_text += "    %s %s: %s" % (
                    define,
                    inflection.camelize(property_name, False),
                    column_type,
                )
                lines.append(line_text)
                swagger_index += 1

            line_text = '    @ApiModelProperty(position = %s, notes = "排序条件")\n' % 98
            line_text += "    val sortOrders: List<SortOrder>? = null"
            lines.append(line_text)
            swagger_index += 1

            line_text = '    @ApiModelProperty(position = %s, notes = "分页(默认第1页，每页显示10条)")\n' % 99
            line_text += "    val paging: Paging = Paging(1, 10)"
            lines.append(line_text)
            content += "%s,\n" % (",\n\n".join(lines))
            content += ")\n"

            output_viewmodels_path = os.path.join(
                kotlin_output_path, "viewmodels", inflection.camelize(table_name, False)
            )
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(
                os.path.join(
                    output_viewmodels_path,
                    inflection.camelize(table_name) + "SearchRequest.kt",
                ),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]Mapper.kt
            file_read = open(os.path.join(TEMPLATE_PATH, "Mapper.kt"), "r", encoding="UTF-8")
            content = file_read.read()
            t = string.Template(content)
            content = t.substitute(
                package_name=package_name,
                model_upper_camelcase=inflection.camelize(table_name),
                model_camelcase=inflection.camelize(table_name, False),
            )

            output_mappers_path = os.path.join(kotlin_output_path, "mappers")
            if not os.path.exists(output_mappers_path):
                os.makedirs(output_mappers_path)
            file_write = open(
                os.path.join(output_mappers_path, inflection.camelize(table_name) + "Mapper.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]Service.kt
            if table_name == "user":
                file_read = open(os.path.join(TEMPLATE_PATH, "UserService.kt"), "r", encoding="UTF-8")
                content = file_read.read()
                t = string.Template(content)
                columns_data = []
                add_user_with_password_columns_data = []
                bind_mobile_columns_data = []
                for column in columns:
                    property_name = column["name"]
                    if (
                        column["name"] == "create_time"
                        or column["name"] == "update_time"
                        or column["name"] == "created_time"
                        or column["name"] == "updated_time"
                        or column["name"] == "is_delete"
                    ):
                        continue
                    if column["name"].startswith("is_"):
                        property_name = column["name"][3:]
                    columns_data.append(
                        "            %s = request.%s"
                        % (
                            inflection.camelize(property_name, False),
                            inflection.camelize(property_name, False),
                        )
                    )

                    if column["name"] != "password" and column["name"] != "salt":
                        add_user_with_password_columns_data.append(
                            "            %s = request.%s"
                            % (
                                inflection.camelize(property_name, False),
                                inflection.camelize(property_name, False),
                            )
                        )
                    else:
                        add_user_with_password_columns_data.append(
                            "            %s = %s"
                            % (
                                inflection.camelize(property_name, False),
                                inflection.camelize(property_name, False),
                            )
                        )
                    if column["name"] != "mobile" and column["name"] != "id":
                        bind_mobile_columns_data.append(
                            "            mobileUser.%s = currentUser.%s"
                            % (
                                inflection.camelize(property_name, False),
                                inflection.camelize(property_name, False),
                            )
                        )
                content = t.substitute(
                    package_name=package_name,
                    columns_data=",\n".join(columns_data),
                    add_user_with_password_columns_data=",\n".join(add_user_with_password_columns_data),
                    bind_mobile_columns_data="\n".join(bind_mobile_columns_data),
                )
            else:
                file_read = open(os.path.join(TEMPLATE_PATH, "Service.kt"), "r", encoding="UTF-8")
                content = file_read.read()
                t = string.Template(content)
                columns_data = []
                for column in columns:
                    property_name = column["name"]
                    if (
                        column["name"] == "create_time"
                        or column["name"] == "update_time"
                        or column["name"] == "created_time"
                        or column["name"] == "updated_time"
                        or column["name"] == "is_delete"
                    ):
                        continue
                    if column["name"].startswith("is_"):
                        property_name = column["name"][3:]
                    columns_data.append(
                        "            %s = request.%s"
                        % (
                            inflection.camelize(property_name, False),
                            inflection.camelize(property_name, False),
                        )
                    )
                content = t.substitute(
                    package_name=package_name,
                    model_upper_camelcase=inflection.camelize(table_name),
                    model_camelcase=inflection.camelize(table_name, False),
                    columns_data=",\n".join(columns_data),
                )

            output_services_path = os.path.join(kotlin_output_path, "services")
            if not os.path.exists(output_services_path):
                os.makedirs(output_services_path)
            file_write = open(
                os.path.join(output_services_path, inflection.camelize(table_name) + "Service.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]Controller.kt
            file_read = open(os.path.join(TEMPLATE_PATH, "Controller.kt"), "r", encoding="UTF-8")
            content = file_read.read()
            t = string.Template(content)
            content = t.substitute(
                package_name=package_name,
                model_dasherize=inflection.dasherize(table_name),
                model_upper_camelcase=inflection.camelize(table_name),
                model_camelcase=inflection.camelize(table_name, False),
                model_description=table_description,
            )

            output_controllers_path = os.path.join(kotlin_output_path, "controllers")
            if not os.path.exists(output_controllers_path):
                os.makedirs(output_controllers_path)
            file_write = open(
                os.path.join(
                    output_controllers_path,
                    inflection.camelize(table_name) + "Controller.kt",
                ),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()


if __name__ == "__main__":
    OPTS, ARGS = getopt.getopt(
        sys.argv[1:],
        "",
        [
            "group_id=",
            "artifact_id=",
            "version=",
            "port=",
            "package_name=",
            "project_path=",
            "description=",
            "registry_instance=",
            "registry_namespace=",
            "registry_username=",
            "registry_password=",
        ],
    )
    for name, value in OPTS:
        if name == "--group_id":
            group_id = value
        elif name == "--artifact_id":
            artifact_id = value
        elif name == "--version":
            version = value
        elif name == "--port":
            port = value
        elif name == "--package_name":
            package_name = value
        elif name == "--project_path":
            project_path = value
        elif name == "--description":
            description = value
        elif name == "--registry_instance":
            registry_instance = value
        elif name == "--registry_namespace":
            registry_namespace = value
        elif name == "--registry_username":
            registry_username = value
        elif name == "--registry_password":
            registry_password = value
        elif name == "--payment":
            payment = True
    print(group_id, artifact_id, version, port, package_name, project_path, description)
    if not os.path.exists(project_path):
        os.makedirs(project_path)

    if not os.path.exists(os.path.join(project_path, "pom.xml")):
        # 初始化项目（复制 .sql 文件）
        init_project()
    run_package()
    copy_archetype_resources()
