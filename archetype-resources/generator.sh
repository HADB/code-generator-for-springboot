project_path=$$(pwd)
package_name="${package_name}"
group_id="${group_id}"
artifact_id="${artifact_id}"
version="${version}"
description="${description}"
port=${port}

cd "$$project_path" || exit

# 获取当前分支的名称
current_branch=$$(git symbolic-ref --short HEAD)

# 检查是否在 generator 分支
if [ "$$current_branch" != "generator" ]; then
    echo "当前不在 generator 分支"
    non_sql_files=$$(git status --porcelain | awk '$$2 !~ /\.sql$$/{print $$2}')
    # 如果有非 .sql 后缀的文件存在，终止
    if [ -n "$$non_sql_files" ]; then
        echo "存在非 .sql 文件改动，退出"
        exit 1
    fi

    echo "切换到 generator 分支"
    git checkout generator
    git pull
fi

# 检查是否存在 CODE_GENERATOR_PATH 环境变量
if [ -z "$$CODE_GENERATOR_PATH" ]; then
    echo "CODE_GENERATOR_PATH 环境变量不存在，请设置 CODE_GENERATOR_PATH 环境变量"
    exit 1
fi

# 进入 code generator 目录
cd "$$CODE_GENERATOR_PATH" || exit


# 运行 main.py 并传递参数
uv run main.py run \
--project_path="$$project_path" \
--package_name=$$package_name \
--group_id=$$group_id \
--artifact_id=$$artifact_id \
--version=$$version \
--description=$$description \
--port=$$port

if [ $$? -ne 0 ]; then
    echo "main.py 执行出错"
    exit 1
fi

# 进入项目目录
cd "$$project_path"

git add .

git commit -m "gen: auto commit"

if [ "$$current_branch" != "generator" ]; then
    # 切换回原分支
    git checkout "$$current_branch"
    if [ $$? -ne 0 ]; then
        echo "切换回 $$current_branch 分支时出错。"
        exit 1
    else
        echo "已切换回 $$current_branch 分支"

        # 合并 generator 分支到原分支
        git merge -m "gen: merge branch 'generator'" generator
        if [ $$? -ne 0 ]; then
            echo "合并 generator 分支到 $$current_branch 分支时出错"
            exit 1
        else
            echo "已成功合并 generator 分支到 $$current_branch 分支"
        fi
    fi
fi

echo "执行完成"
