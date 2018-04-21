# coding=utf-8
"""
生成Mybatis基础的增删改查SQL
"""
import os
import inflection

CURRENT_PATH = os.getcwd()  #当前路径
INPUT_PATH = os.path.join(CURRENT_PATH, 'inputs')  #输入路径
OUTPUT_PATH = os.path.join(CURRENT_PATH, 'outputs')  #输出路径
INPUT_FILES = os.listdir(INPUT_PATH)  #输入文件
PACKAGE_NAME = 'run.monkey.op.xiaotong'

for inputFileName in INPUT_FILES:
    inputFilePath = os.path.join(INPUT_PATH, inputFileName)
    if not os.path.isdir(inputFilePath):
        fileName = os.path.splitext(inputFileName)[0].strip()
        tableName = fileName[2:]
        outputFilePath = os.path.join(OUTPUT_PATH, tableName)
        if not os.path.exists(outputFilePath):
            os.makedirs(outputFilePath)
        fileRead = open(inputFilePath, 'r')
        columns = []
        for line in fileRead:
            column = {}
            column['name'] = line.strip().split()[0].strip('`')
            column['type'] = line.strip().split()[1].split('(')[0].lower()
            if line.find('NOT NULL') > 0:
                column['nullable'] = False
            else:
                column['nullable'] = True

            if line.find('DEFAULT') > 0:
                column['default'] = line[line.find('DEFAULT') + 8:].split()[0]
            else:
                column['default'] = None

            column['comment'] = line[line.find('COMMENT') + 8:].split('\'')[1]
            columns.append(column)

        # [Model]Mapper.xml
        fileWrite = open(os.path.join(outputFilePath, inflection.camelize(tableName) + 'Mapper.xml'), 'w')
        content = ''

        ## columns
        content += '<sql id="%sColumns">\n' % (tableName)
        lines = []
        for column in columns:
            lines.append('    `%s` as `%s`' % (column['name'], inflection.camelize(column['name'], False)))
        content += '%s\n' % (',\n'.join(lines))
        content += '</sql>\n\n'

        ## insert
        content += '<insert id="insert%s">\n' % (inflection.camelize(tableName))
        content += '    INSERT INTO `t_%s`(\n' % (tableName)
        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'is_delete':
                continue
            else:
                lines.append('    `%s`' % (column['name']))
        content += '%s)\n' % (',\n'.join(lines))
        content += '    VALUES(\n'
        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'is_delete':
                continue
            elif column['name'] == 'create_time' or column['name'] == 'update_time':
                lines.append('    NOW()')
            else:
                lines.append('    #{%s.%s}' % (inflection.camelize(tableName, False), inflection.camelize(column['name'], False)))
        content += '%s)\n' % (',\n'.join(lines))
        content += '</insert>\n\n'

        ## update
        content += '<update id="update%s">\n' % (inflection.camelize(tableName))
        content += '    UPDATE `%s` SET\n' % (tableName)
        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'create_time' or column['name'] == 'is_delete':
                continue
            elif column['name'] == 'update_time':
                lines.append('    `update_time` = NOW()')
            else:
                lines.append('    `%s` = #{%s.%s}' % (column['name'], inflection.camelize(tableName, False), inflection.camelize(column['name'], False)))
        content += '%s\n' % (',\n'.join(lines))
        content += '    WHERE `id` = #{id}\n'
        content += '</update>\n\n'

        ## delete
        content += '<update id="delete%s">\n' % (inflection.camelize(tableName))
        content += '    UPDATE `%s` SET\n' % (tableName)
        content += '    `is_delete` = 1\n'
        content += '    WHERE `id` = #{id}\n'
        content += '</update>\n\n'

        ## select
        content += '<select id="selectPaging%ss" resultType="%s.models.%s">\n' % (inflection.camelize(tableName), PACKAGE_NAME, inflection.camelize(tableName))
        content += '    SELECT\n'
        content += '    <include refid="%sColumns"></include>\n' % (inflection.camelize(tableName, False))
        content += '    FROM `%s`\n' % (tableName)
        content += '    WHERE `is_delete` = 0\n'
        content += '    ORDER BY `create_time` ASC\n'
        content += '    LIMIT #{request.paging.offset}, #{request.paging.pageSize}\n'
        content += '</select>\n\n'

        ## selectCount
        content += '<select id="selectPaging%ssCount" resultType="Long">\n' % (inflection.camelize(tableName))
        content += '    SELECT COUNT(*)\n'
        content += '    FROM `%s`\n' % (tableName)
        content += '    WHERE `is_delete` = 0\n'
        content += '</select>\n\n'

        fileWrite.write(content)
        fileWrite.close()

        # [Model].kt
        fileWrite = open(os.path.join(outputFilePath, inflection.camelize(tableName) + '.kt'), 'w')
        content = ''
        content += 'package %s.models\n\n' % (PACKAGE_NAME)
        content += 'import %s.annotations.NoArg\n' % (PACKAGE_NAME)
        content += 'import java.util.*\n\n'
        content += '@NoArg\n'
        content += 'data class %s(\n' % (inflection.camelize(tableName))
        lines = []
        for column in columns:
            type = 'String'
            if column['type'] == 'bigint':
                type = 'Long'
            elif column['type'] == 'tinyint' or column['type'] == 'int':
                type = 'Int'
            elif column['type'] == 'datetime':
                type = 'Date'

            if column['nullable']:
                type += '?'
            if column['name'] == 'create_time' or column['name'] == 'update_time':
                type += ' = Date(0)'
            if column['name'] == 'id' or column['name'] == 'is_delete':
                type += ' = 0'
            lines.append('        val %s: %s' % (inflection.camelize(column['name'], False), type))
        max_length = len(max(lines, key=len))
        for index, line in enumerate(lines):
            if index < len(lines) - 1:
                lines[index] += ','
            lines[index] = lines[index].ljust(max_length + 5)
        for index, column in enumerate(columns):
            lines[index] += '// ' + column['comment']
        content += '%s\n' % ('\n'.join(lines))
        content += ')\n'

        fileWrite.write(content)
        fileWrite.close()