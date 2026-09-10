# libs

把女仆模组本体（`touhou_little_maid`）的 jar 放到本目录即可让 `build.gradle` 优先使用本地依赖：

```
libs/touhoulittlemaid-1.20.1-all.jar
```

文件名需匹配 `touhoulittlemaid-*.jar`。

本目录为空时（例如 CI 或全新克隆），构建会自动回退到 CurseMaven 上的公开版本
`curse.maven:touhou-little-maid-355044:6911704`，不会因缺少本地 jar 而失败。

注意：仓库的 `.gitignore` 忽略 `*.jar`，所以放到这里的文件不会被提交。
