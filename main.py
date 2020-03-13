# coding=utf-8
"""
crud-code-generator-for-springboot
"""
import getopt
import os
import shutil
import string
import sys

import inflection

CURRENT_PATH = os.getcwd()  # 当前目录
INPUT_PATH = os.path.join(CURRENT_PATH, 'inputs')  # 输入目录
OUTPUT_PATH = os.path.join(CURRENT_PATH, 'outputs')  # 输出目录
TEMPLATE_PATH = os.path.join(CURRENT_PATH, 'templates')  # 模板目录


def get_column_type_property_name(column):
    column_type = 'String'
    property_name = column['name']
    if column['type'] == 'bigint':
        column_type = 'Long'
    elif column['type'] == 'double':
        column_type = 'Double'
    elif column['type'] == 'decimal':
        column_type = 'BigDecimal'
    elif column['type'] == 'tinyint' and column['name'].startswith('is_'):
        column_type = 'Boolean'
        property_name = column['name'][3:]
    elif column['type'] == 'tinyint' or column['type'] == 'int':
        column_type = 'Int'
    elif column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
        column_type = 'Date'
    return column_type, property_name


def run_package(package_name):
    input_path = os.path.join(INPUT_PATH, package_name)
    output_path = os.path.join(OUTPUT_PATH, package_name)
    if not os.path.exists(output_path):
        os.makedirs(output_path)
    kotlin_output_path = os.path.join(output_path, 'main', 'kotlin', *package_name.split('.'))  # kotlin 输出目录
    mapper_output_path = os.path.join(output_path, 'main', 'resources', 'mapper')  # mapper 输出目录
    if os.path.exists(kotlin_output_path):
        shutil.rmtree(kotlin_output_path)
    if os.path.exists(mapper_output_path):
        shutil.rmtree(mapper_output_path)

    for input_file_name in os.listdir(input_path):
        input_file_path = os.path.join(input_path, input_file_name)
        if not os.path.isdir(input_file_path):
            if not input_file_name.endswith('.sql'):
                print('skip: ' + input_file_name)
                continue
            print('work: ' + input_file_name)
            file_name = os.path.splitext(input_file_name)[0].strip()  # 文件名
            table_name = file_name  # 表名默认为文件名
            table_description = table_name  # 表注释默认为文件名
            file_read = open(input_file_path, 'r', encoding='UTF-8')
            columns = []  # 字段数组

            for line in file_read:
                if line.find('CREATE TABLE ') >= 0:
                    table_name = line.strip().split()[2].strip('`')[2:]  # 读取表名
                    continue
                if line.find(' KEY ') >= 0:
                    continue  # 跳过索引
                if line.find('CHARSET=') >= 0:
                    table_description = line[line.find('COMMENT') + 8:].split('\'')[1]  # 读取表注释
                    continue
                column = {
                    'name': line.strip().split()[0].strip('`'),
                    'type': line.strip().split()[1].split('(')[0].lower()
                }
                if line.find('NOT NULL ') > 0:
                    column['nullable'] = False  # 字段是否可空
                else:
                    column['nullable'] = True

                if line.find('DEFAULT ') > 0:
                    column['default'] = line[line.find('DEFAULT ') + 8:].split()[0].replace('\'', '"')  # 字段默认值
                    if column['default'] == 'NULL' or column['default'] == 'CURRENT_TIMESTAMP':
                        column['default'] = 'null'
                    if (column['type'].find('int') >= 0 or column['type'].find('double') >= 0 or column['type'].find('decimal') >= 0) and column['default'].find('"') >= 0:
                        column['default'] = column['default'].replace('"', '')
                    if column['type'] == 'tinyint' and column['name'].startswith('is_'):
                        if column['default'] == '0':
                            column['default'] = 'false'
                        else:
                            column['default'] = 'true'
                else:
                    column['default'] = None

                column['comment'] = line[line.find('COMMENT') + 8:].split('\'')[1]  # 字段注释
                columns.append(column)
            file_read.close()

            # [Model]Mapper.xml
            lines = []
            for column in columns:
                property_name = column['name']
                if column['name'] == 'is_delete':
                    continue
                if column['name'].startswith('is_'):
                    property_name = column['name'][3:]

                lines.append('        `%s`.`%s` as `%s`' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(property_name, False)))
            column_list = ',\n'.join(lines)

            lines = []
            for column in columns:
                if column['name'] == 'id' or column['name'] == 'sort_weight':
                    continue
                if column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
                    lines.append('        <if test="request.%sFrom != null">' % (inflection.camelize(column['name'], False)))
                    lines.append('            AND `%s`.`%s` &gt;= #{request.%sFrom}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'], False)))
                    lines.append('        </if>')
                    lines.append('        <if test="request.%sTo != null">' % (inflection.camelize(column['name'], False)))
                    lines.append('            AND `%s`.`%s` &lt;= #{request.%sTo}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'], False)))
                    lines.append('        </if>')
                    continue
                if column['name'] == 'is_delete':
                    lines.append('        AND `%s`.`%s` = 0' % (inflection.camelize(table_name, False), column['name']))
                    continue
                if column['type'] == 'varchar' or column['type'] == 'text':
                    lines.append('        <if test="request.%s != null and request.%s !=\'\'">' % (inflection.camelize(column['name'], False), inflection.camelize(column['name'], False)))
                elif column['name'].startswith('is_'):
                    lines.append('        <if test="request.%s != null">' % (inflection.camelize(column['name'][3:], False)))
                else:
                    lines.append('        <if test="request.%s != null">' % (inflection.camelize(column['name'], False)))
                if column['name'].startswith('is_'):
                    lines.append('            AND `%s`.`%s` = #{request.%s}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'][3:], False)))
                else:
                    lines.append('            AND `%s`.`%s` = #{request.%s}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'], False)))
                lines.append('        </if>')
            search_where = '\n'.join(lines)

            lines = []
            for column in columns:
                if column['name'] == 'id' or column['name'] == 'is_delete':
                    continue
                lines.append('        `%s`' % (column['name']))
            name_list = ',\n'.join(lines)

            lines = []
            for column in columns:
                if column['name'] == 'id' or column['name'] == 'is_delete':
                    continue
                elif column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'created_time' or column['name'] == 'updated_time':
                    lines.append('        NOW()')
                elif column['name'].startswith('is_'):
                    lines.append('        #{%s.%s}' % (inflection.camelize(table_name, False), inflection.camelize(column['name'][3:], False)))
                else:
                    lines.append('        #{%s.%s}' % (inflection.camelize(table_name, False), inflection.camelize(column['name'], False)))
            value_list = ',\n'.join(lines)

            lines = []
            for column in columns:
                if column['name'] == 'id' or column['name'] == 'create_time' or column['name'] == 'created_time' or column['name'] == 'is_delete':
                    continue
                elif column['name'] == 'update_time' or column['name'] == 'updated_time':
                    lines.append('        `%s` = NOW()' % (column['name']))
                elif column['name'].startswith('is_'):
                    lines.append('        `%s` = #{%s.%s}' % (column['name'], inflection.camelize(table_name, False), inflection.camelize(column['name'][3:], False)))
                else:
                    lines.append('        `%s` = #{%s.%s}' % (column['name'], inflection.camelize(table_name, False), inflection.camelize(column['name'], False)))
            update_list = ',\n'.join(lines)

            lines = []
            for column in columns:
                if column['name'] == 'sort_weight':
                    lines.append('`%s`.`sort_weight` DESC' % (inflection.camelize(table_name, False)))
            if not lines:
                lines.append('`%s`.`id` DESC' % (inflection.camelize(table_name, False)))
            orders = ', '.join(lines)

            file_read = open(os.path.join(TEMPLATE_PATH, 'Mapper.xml'), 'r', encoding='UTF-8')
            content = file_read.read()
            t = string.Template(content)
            content = t.substitute(table_name='t_' + table_name, package_name=package_name, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False), column_list=column_list, search_where=search_where, orders=orders, name_list=name_list, value_list=value_list, update_list=update_list)

            if not os.path.exists(mapper_output_path):
                os.makedirs(mapper_output_path)

            file_write = open(os.path.join(mapper_output_path, inflection.camelize(table_name) + 'Mapper.xml'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()

            # [Model].kt
            content = ''
            content += 'package %s.models\n\n' % package_name
            content += 'import io.swagger.annotations.ApiModelProperty\n'
            content += 'import %s.annotations.NoArg\n' % package_name
            content += 'import java.math.BigDecimal\n'
            content += 'import java.util.*\n\n'
            content += '@NoArg\n'
            content += 'data class %s(\n' % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                line_text = ''
                if column['name'] == 'is_delete':
                    continue
                else:
                    column_type, property_name = get_column_type_property_name(column)

                    if column['nullable']:
                        column_type += '?'
                    if column['default']:
                        column_type += ' = ' + column['default']
                    if column['name'] == 'id':
                        column_type += ' = 0'
                    line_text += '        @ApiModelProperty(position = %s, notes = "%s")\n' % (swagger_index, column['comment'])
                    line_text += '        val %s: %s' % (inflection.camelize(property_name, False), column_type)
                    lines.append(line_text)
                    swagger_index += 1
            content += '%s\n' % (',\n\n'.join(lines))
            content += ')\n'

            output_models_path = os.path.join(kotlin_output_path, 'models')
            if not os.path.exists(output_models_path):
                os.makedirs(output_models_path)
            file_write = open(os.path.join(output_models_path, inflection.camelize(table_name) + '.kt'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()

            # [Model]EditRequest.kt
            content = ''
            content += 'package %s.viewmodels.%s\n\n' % (package_name, inflection.camelize(table_name, False))
            content += 'import io.swagger.annotations.ApiModelProperty\nimport java.math.BigDecimal\nimport java.util.*\nimport javax.validation.constraints.NotNull\n\n'
            content += 'data class %sEditRequest(\n' % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                define = 'val'
                required = True
                hidden = False
                line_text = ''
                column_type, property_name = get_column_type_property_name(column)

                if column['nullable']:
                    column_type += '?'
                    required = False
                if column['default']:
                    column_type += ' = ' + column['default']
                if column['name'] == 'id':
                    define = 'var'
                    required = False
                    hidden = True
                if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'created_time' or column['name'] == 'updated_time' or column['name'] == 'is_delete':
                    continue
                if column['name'] == 'id':
                    column_type += ' = 0'
                if required:
                    line_text += '        @NotNull(message = "%s 不能为空")\n' % (inflection.camelize(property_name, False))
                line_text += '        @ApiModelProperty(position = %s, notes = "%s", required = %s, hidden = %s)\n' % (swagger_index, column['comment'], 'true' if required else 'false', 'true' if hidden else 'false')
                line_text += '        %s %s: %s' % (define, inflection.camelize(property_name, False), column_type)
                lines.append(line_text)
                swagger_index += 1
            content += '%s\n' % (',\n\n'.join(lines))
            content += ')\n'

            output_viewmodels_path = os.path.join(kotlin_output_path, 'viewmodels', inflection.camelize(table_name, False))
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(os.path.join(output_viewmodels_path, inflection.camelize(table_name) + 'EditRequest.kt'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()

            # [Model]SearchRequest.kt
            content = ''
            content += 'package %s.viewmodels.%s\n\n' % (package_name, inflection.camelize(table_name, False))
            content += 'import io.swagger.annotations.ApiModelProperty\nimport %s.models.Paging\nimport java.math.BigDecimal\nimport java.util.*\n\n' % package_name
            content += 'data class %sSearchRequest(\n' % (inflection.camelize(table_name))
            lines = []
            swagger_index = 0
            for column in columns:
                if column['name'] == 'id' or column['name'] == 'sort_weight' or column['name'] == 'is_delete':
                    continue
                column_type = 'String? = null'
                property_name = column['name']
                define = 'val'
                line_text = ''
                if column['type'] == 'bigint':
                    column_type = 'Long? = null'
                elif column['type'] == 'double':
                    column_type = 'Double? = null'
                elif column['type'] == 'decimal':
                    column_type = 'BigDecimal? = null'
                elif column['type'] == 'tinyint' and column['name'].startswith('is_'):
                    column_type = 'Boolean? = null'
                    property_name = column['name'][3:]
                elif column['type'] == 'tinyint' or column['type'] == 'int':
                    column_type = 'Int? = null'
                elif column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
                    column_type = 'Date? = null'
                    line_text = '        @ApiModelProperty(position = %s, notes = "%s From")\n' % (swagger_index, column['comment'])
                    line_text += '        %s %s: %s' % (define, inflection.camelize(column['name'] + "From", False), column_type)
                    lines.append(line_text)
                    swagger_index += 1
                    line_text = '        @ApiModelProperty(position = %s, notes = "%s To")\n' % (swagger_index, column['comment'])
                    line_text += '        %s %s: %s' % (define, inflection.camelize(column['name'] + "To", False), column_type)
                    lines.append(line_text)
                    swagger_index += 1
                    continue

                line_text += '        @ApiModelProperty(position = %s, notes = "%s")\n' % (swagger_index, column['comment'])
                line_text += '        %s %s: %s' % (define, inflection.camelize(property_name, False), column_type)
                lines.append(line_text)
                swagger_index += 1

            line_text = '        @ApiModelProperty(position = %s, notes = "排序字段")\n' % 97
            line_text += '        val sortBy: String? = null'
            lines.append(line_text)
            swagger_index += 1

            line_text = '        @ApiModelProperty(position = %s, notes = "排序顺序")\n' % 98
            line_text += '        val sortOrder: String? = null'
            lines.append(line_text)
            swagger_index += 1

            line_text = '        @ApiModelProperty(position = %s, notes = "分页(默认第1页，每页显示10条)")\n' % 99
            line_text += '        val paging: Paging = Paging(1,10)'
            lines.append(line_text)
            content += '%s\n' % (',\n\n'.join(lines))
            content += ')\n'

            output_viewmodels_path = os.path.join(kotlin_output_path, 'viewmodels', inflection.camelize(table_name, False))
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(os.path.join(output_viewmodels_path, inflection.camelize(table_name) + 'SearchRequest.kt'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()

            # [Model]Mapper.kt
            file_read = open(os.path.join(TEMPLATE_PATH, 'Mapper.kt'), 'r', encoding='UTF-8')
            content = file_read.read()
            t = string.Template(content)
            content = t.substitute(package_name=package_name, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False))

            output_mappers_path = os.path.join(kotlin_output_path, 'mappers')
            if not os.path.exists(output_mappers_path):
                os.makedirs(output_mappers_path)
            file_write = open(os.path.join(output_mappers_path, inflection.camelize(table_name) + 'Mapper.kt'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()

            # [Model]Service.kt
            file_read = open(os.path.join(TEMPLATE_PATH, 'Service.kt'), 'r', encoding='UTF-8')
            content = file_read.read()
            t = string.Template(content)
            columns_data = []
            for column in columns:
                property_name = column['name']
                if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'created_time' or column['name'] == 'updated_time' or column['name'] == 'is_delete':
                    continue
                if column['name'].startswith('is_'):
                    property_name = column['name'][3:]
                columns_data.append('                %s = request.%s' % (inflection.camelize(property_name, False), inflection.camelize(property_name, False)))
            content = t.substitute(package_name=package_name, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False), columns_data=',\n'.join(columns_data))

            output_services_path = os.path.join(kotlin_output_path, 'services')
            if not os.path.exists(output_services_path):
                os.makedirs(output_services_path)
            file_write = open(os.path.join(output_services_path, inflection.camelize(table_name) + 'Service.kt'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()

            # [Model]Controller.kt
            file_read = open(os.path.join(TEMPLATE_PATH, 'Controller.kt'), 'r', encoding='UTF-8')
            content = file_read.read()
            t = string.Template(content)
            content = t.substitute(package_name=package_name, model_dasherize=inflection.dasherize(table_name), model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False), model_description=table_description)

            output_controllers_path = os.path.join(kotlin_output_path, 'controllers')
            if not os.path.exists(output_controllers_path):
                os.makedirs(output_controllers_path)
            file_write = open(os.path.join(output_controllers_path, inflection.camelize(table_name) + 'Controller.kt'), 'w', encoding='UTF-8')
            file_write.write(content)
            file_write.close()


if __name__ == '__main__':
    if not os.path.exists(OUTPUT_PATH):
        os.makedirs(OUTPUT_PATH)
    OPTS, ARGS = getopt.getopt(sys.argv[1:], 'p:')
    for name, value in OPTS:
        if name == '-p':
            print(name, value)
            PACKAGE_NAME = value
            run_package(PACKAGE_NAME)
