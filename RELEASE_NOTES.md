# OwnBox for Android v3.2.49 预览版 (v3.2.49-preview)

* **修复顶栏搜索图标与操作按钮右侧被裁切截断（显示为“C”）的问题**：
  - **根治搜索图标被裁切成“C”**：`SearchView` 在收起状态下内部嵌套的 `search_button` 默认继承 AppCompat 的 48dp 最小宽度与 12dp 左右内边距，导致在外部容器收窄至紧凑宽度时，24dp 放大镜图标右侧半圆及手柄被硬裁切为“C”形；现已对 `search_bar` 与 `search_button` 同步穿透设置为 **32dp** 居中无内边距布局，并取消全链路子视图裁切；
  - **测量与布局尺寸严格同步**：在 `ActionMenuView` 紧凑重排阶段先对所有可见按钮执行 `EXACTLY 32dp` 重测量（`measure`）再执行 `layout`，确保 `ActionMenuItemView` 图标居中偏移（`paddingLeft = 4dp`）与实际绘制宽度完全一致，彻底消除测速表等图标右边缘被削切的现象；
  - **退出搜索状态完整恢复**：调整 `cancelSearch` 中菜单按钮恢复可见性与紧凑重排的执行时序，退出搜索后 5 位顶栏图标完整、均匀呈现。

