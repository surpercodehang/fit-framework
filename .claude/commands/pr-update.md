更新现有 Pull Request 的描述。

**用法：**
- `/pr-update <pr-number>` - 更新指定PR的描述，使其符合模板规范

**执行步骤：**

1. **解析PR编号**
   - 从命令参数中提取 PR 编号：命令格式为 `/pr-update <number>`

2. **读取 PR 模板**（必须执行）
   ```
   Read(".github/pull_request_template.md")
   ```

3. **查看最近 3 个 merged PR 作为参考**
   ```
   gh pr list --limit 3 --state merged --json number,title,body
   ```

4. **获取当前PR信息**
   ```
   gh pr view <pr-number>
   ```

5. **获取PR的变更内容**
   ```
   gh pr diff <pr-number>
   ```

6. **更新PR描述**
   - 按照 `.github/pull_request_template.md` 格式重新编写
   - 参考最近的 PR 格式
   - 保留原有的有效信息
   - 补充缺失的部分
   - 使用 HEREDOC 格式更新

   ```
   gh pr edit <pr-number> --body "$(cat <<'EOF'
   <完整的PR描述>
   EOF
   )"
   ```

**注意事项：**
- 必须严格遵循 PR 模板格式
- 保留原有PR中有价值的信息
- 补充所有必填项
- 结尾添加：`🤖 Generated with [Claude Code](https://claude.com/claude-code)`
