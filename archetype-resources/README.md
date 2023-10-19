# ${description}

#### 首次创建项目

```bash
$$ echo 'export CODE_GENERATOR_PATH="/path/to/code-generator-for-springboot"' >> ~/.zshrc
$$ source ~/.zshrc
$$ cd "/path/to/code-generator-for-springboot"
$$ python3 main.py \
    --project_path=/path/to/your/project \
    --package_name=${package_name} \
    --group_id=${group_id} \
    --artifact_id=${artifact_id} \
    --version=${version} \
    --description=${description} \
    --port=${port}
```

#### 更新项目

```bash
$$ cd "/path/to/your/project"
$$ ./generator.sh
```