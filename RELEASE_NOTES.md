# OwnBox for Android v3.2.50 预览版 (v3.2.50-preview)

* **优化顶栏图标按钮与 APP 名称（OwnBox）视觉比例与间距**：
  - **左侧菜单按钮与 OwnBox 品牌名黄金间距**：收敛左上角抽屉导航按钮（`[≡]`）的默认冗余宽度与内边距并微缩至 0.9x，将 `OwnBox` 标题字号提升至 **17.5sp**、字重紧凑微调（`letterSpacing = -0.01`）并向右留出 **14dp** 舒适呼吸间距，彻底解决菜单图标与 `OwnBox` 贴得过近、字号偏小的问题；
  - **右上角 5 位操作图标精致微缩**：将右上角 5 个操作按钮（🔍 搜索、📄+ 添加、⚡ URL 测速、🧭 速度测试、⋮ 更多菜单）图标整体微缩至 **0.88x（约 21dp）** 并居中排布于 **33dp** 触控单元内，消除原 24dp 满幅图标在紧凑顶栏中的拥挤感，视觉层次更清爽均衡。
* **修复 Telegram 官方客户端自定义分流规则失效（仍走默认节点）的问题**：
  - **根治内置 Telegram DC IP 规则抢占优先级**：原 `ConfigBuilder` 在 `topRouteRules` 顶部无条件注入了 `ip_cidr = telegramDcIps -> mainProxyTag`（主代理）出站规则，由于其排在用户自定义路由规则（`extraRules`）之前，导致用户为 Telegram 客户端单独配置的指定节点规则被提前劫持回默认节点（如香港）；
  - **保留 0ms 秒连优化并交还路由控制权**：将 `topRouteRules` 中的 Telegram DC IP 处理改为仅跳过协议嗅探（`action = sniff, ip_cidr = telegramDcIps, invert = true`），并将 `telegramDcIps -> mainProxyTag` 兜底规则移至用户自定义路由规则**之后**，既保持 Telegram 纯 IP MTProto 0ms 零延迟直通，又让用户的 Telegram 分流规则 100% 优先生效；
  - **官方 Telegram 多包名与 UID 兜底**：当分流规则勾选任意 Telegram 官方包名（`org.telegram.messenger` / `.web` / `.beta`）时自动全量覆盖官方包名变体，并在 `PackageCache` 未命中时直接通过系统 `PackageManager` 实时查询 UID。
* **修复订阅拉取与节点链接解析的两项日志报错问题**：
  - **修复主进程代理拉取订阅失效与 UA 403/0 节点问题**：修复 `HTTPClient.tryProxyOutbound()` 在主进程（UI 进程）调用 `tryBoxOutbound()` 恒为空导致代理更新订阅静默退回直连的问题，改为优先通过本地 Mixed 混合端口（`127.0.0.1:mixedPort`）走代理由内核转发；同时将默认订阅 UA 恢复并自动迁移为兼容性最佳的 `NekoBox/Android/1.4.2 (Prefer ClashMeta Format)`，并支持对 Base64 编码的 Clash YAML / JSON / SIP008 订阅内容递归解码解析；
  - **消除 `vless://` / `vmess://` 误触发 Shadowsocks 解析报错与 `https://` 订阅链接误报 `Not http proxy` 警告**：修复 `splitLinks` 正则在 `vless://`、`vmess://` 内部第 4 个字符起误匹配出 `ss://` 子串导致刷屏报错的问题，并优化 `parseLink` 对带路径/参数的 `http(s)://` 订阅链接直接识别为订阅而不再抛出 `Not http proxy` 异常日志。
