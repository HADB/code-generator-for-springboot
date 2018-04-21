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
            column = line.strip().split()[0].strip('`')
            columns.append(column)

        # Mapper文件
        fileWrite = open(os.path.join(outputFilePath, inflection.camelize(tableName) + 'Mapper.xml'), 'w')
        content = ''

        ## columns
        content += '<sql id="%sColumns">\n' % (tableName)
        lines = []
        for column in columns:
            lines.append('    `%s` as `%s`' % (column, inflection.camelize(column, False)))
        content += '%s\n' % (',\n'.join(lines))
        content += '</sql>\n\n'

        ## insert
        content += '<insert id="insert%s">\n' % (inflection.camelize(tableName))
        content += '    INSERT INTO `t_%s`(\n' % (tableName)
        lines = []
        for column in columns:
            if column == 'id' or column == 'is_delete':
                continue
            else:
                lines.append('    `%s`' % (column))
        content += '%s)\n' % (',\n'.join(lines))
        content += '    VALUES(\n'
        lines = []
        for column in columns:
            if column == 'id' or column == 'is_delete':
                continue
            elif column == 'create_time' or column == 'update_time':
                lines.append('    NOW()')
            else:
                lines.append('    #{%s.%s}' % (inflection.camelize(tableName, False), inflection.camelize(column, False)))
        content += '%s)\n' % (',\n'.join(lines))
        content += '</insert>\n\n'

        ## update
        content += '<update id="update%s">\n' % (inflection.camelize(tableName))
        content += '    UPDATE `%s` SET\n' % (tableName)
        lines = []
        for column in columns:
            if column == 'id' or column == 'create_time' or column == 'is_delete':
                continue
            elif column == 'update_time':
                lines.append('    `update_time` = NOW()')
            else:
                lines.append('    `%s` = #{%s.%s}' % (column, inflection.camelize(tableName, False), inflection.camelize(column, False)))
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
        content += '<select id="selectPaging%ss">\n' % (inflection.camelize(tableName))
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