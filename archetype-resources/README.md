# ${description}

#### 克隆 code-generator-for-springboot 项目到本地

```bash
$$ git clone git@github.com:HADB/code-generator-for-springboot.git
$$ echo 'export CODE_GENERATOR_PATH="/path/to/code-generator-for-springboot"' >> ~/.zshrc
$$ source ~/.zshrc
$$ cd "/path/to/code-generator-for-springboot"
$$ uv sync
```

#### 更新项目 CRUD 代码

```bash
$$ cd "/path/to/your/project"
$$ git switch generator
# 修改 src/main/resources/ddl 下的 SQL 文件，新增或更新表结构
$$ ./generator.sh
# 将修改合并到 main 分支
```
