# MeloX Android — 前端约定（长期有效）

## 设计基准：AOSP / Material 3

- **播放相关界面一律以 AOSP / Google 官方播放器示例（Material 3 / Material You）为唯一基准**：
  迷你播放条、全屏播放页、队列、播放控制、进度条。
- 不再新增或保留 iOS / SF Symbols 风格的播放器 UI：不使用玻璃折射、模糊采样、
  SF 符号图标、iOS 圆角与排版、Apple Music 风格控件。
- 播放页进度条必须是 **Material 3 风格的波浪线进度条**（wave amplitude 随进度/播放状态变化），
  参考 Material 3 Expressive 的 wavy progress indicator。
- 颜色一律走 `MaterialTheme.colorScheme`：Android 12+ 默认莫奈动态取色，
  `设置 → 外观 → 取色方式` 可切回 MeloX 品牌配色。
- 其余界面同样使用标准 Material 3 组件：`NavigationBar`、`ListItem`、`OutlinedTextField`、
  `Card`、`FilledTonalButton`、`AlertDialog`、`ModalBottomSheet` 等。

## 工作流

- 只在本仓库（fork）内修改；绝不改动上游仓库，也不向上游开 PR。
- 每次前端改动完成后必须执行 `scripts/adb-verify.ps1`
  （构建 → 安装 → 启动 → 崩溃缓冲检查，尽量带 `-Screenshot`）后再回报结果。
- 提交信息使用 conventional commits；作者为用户身份，并附
  `Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>`。
