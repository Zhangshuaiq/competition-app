# 后台功能专项测试报告（2026-10-20）

本次仅测试和分析，未修改业务代码。测试使用原数据库的 SQLite 在线备份及资源副本，写入全部发生于 `/private/tmp/fx-backend-audit`，未修改原始比赛数据。测试结束后逐表比较配置、团队、题目、成绩明细和环节，与测试前快照一致。

## 结论

已通过真实 FXML、后台控制器、MyBatis Mapper、SQLite 副本及比赛环节预览复现 8 类问题。其中最贴合“改设置后进入比赛空白”的问题是：**界面设置 → 勾选「显示环节列表」→ 保存，比赛环节列表反而消失。**

请注意后台有两个独立菜单：「界面设置」（PageConfig）和「网页设置」（WebPageConfig）。正常修改网页设置行数为 6 后，配置 1、2 保持不变，比赛环节预览保存前后均正常加载。本次未复现「仅修改网页设置就无法进入比赛」的直接因果关系，不能把另一菜单中已证实的问题冒充该问题的最终根因。

原始数据库快照中「显示环节列表」为 false、团队编号均可正常加载；本报告中的非法数据由测试在副本中构造，不能据此认定用户原始数据已经损坏。

## 测试方法和边界

- 原有自动化测试：38 项通过。
- 新增后台专项测试：17 项通过，0 失败、0 错误。**其中缺陷复现测试通过，表示已成功观测到错误行为，不表示缺陷已修复。**
- 9 个后台页面实际加载：环节、界面、团队、抢答、计分、题库、成绩、平板、网页设置。终端设置因涉及硬件排除。
- 正常流程：环节名称修改并回读；抢答、计分、平板配置保存后重载；网页配置保存并重新创建比赛环节预览；成绩 Excel 写出回读、清空副本成绩；无选中环节时更新不改变数据库。
- 异常流程：非法团队编号、数值越界、保留分隔符、数据库写入失败、题库导入插入失败。
- 未测试浏览器或网页交互、串口与其他硬件、实际扩展屏显示。平板只测试后台配置保存，不连接设备。
- 跨控制器刷新、WebSocket 通知使用 mock 隔离；核心配置保存、SQLite 写入、FXML 控件和比赛环节预览渲染为真实代码。并未模拟完整主程序从启动到全部比赛模式的端到端操作。
- 文件选择器、系统剪贴板、系统打开 Excel、每种题型/层级的全部增删排序组合、所有导入模板与历史数据库升级组合尚未穷尽。本报告是后台专项覆盖，不能宣称所有后台操作均已完整验收。

## 已复现的问题

### 1. P1：勾选「显示环节列表」导致比赛环节页空白

复现：打开界面设置，勾选「显示环节列表」，保存，再加载比赛环节页。

实际：配置 2 的字段 9 为 true 时，比赛预览容器的子节点数变为 0；修改前容器非空。没有异常也会出现“比赛界面出不来”的表现。

原因：`StagePreviewPreview.showView()` 先清空容器，再在 `if (Boolean.valueOf(split[9]))` 中进入只有注释的分支，渲染列表的实际代码全部位于 else。扩展屏 `StagePreview` 有同样的判断；扩展屏结论来自代码核对，未做实屏测试。

位置：`src/main/resources/view/backend/PageConfig.fxml:103`；`src/main/java/competition/controller/preview/StagePreviewPreview.java:129`；`src/main/java/competition/controller/match/StagePreview.java:101`。

测试：`enablingShowStageListSavesSuccessfullyButBlanksMatch`。

建议：明确该开关的含义并修正分支；覆盖开关两种状态。临时规避是保持该选项不勾选。

### 2. P1：数值格式校验不包含范围，错误值可以持久化并阻断加载

复现 A：界面设置中环节 X 坐标输入 `999999999999999`，保存后重新加载比赛环节预览。

实际：保存成功，配置 2 字段 4 写入该值；比赛环节预览调用 `Integer.parseInt` 抛出 `NumberFormatException`。

复现 B：计分牌 X 坐标输入相同值并保存。

实际：配置 1 字段 17 已经写入，后续刷新窗口位置时才抛出 `NumberFormatException`，保存过程不能恢复旧值。

原因：`CustomTextField.NUMERIC` 仅限制数字字符，允许超出 int 范围的数字；配置保存前未统一转换、校验范围。

位置：`CustomTextField.java:89`；`PageConfig.java:967`、`:1024`；`StagePreviewPreview.java:188`。

测试：`stageCoordinateOverflowSavesButBreaksMatchLoad`、`pageSaveCommitsInvalidCoordinateBeforeRefreshFails`。

建议：保存前完成所有字段的类型和范围校验；加载端对旧坏值提供默认值及明确提示。

### 3. P1：团队编号未校验，保存后报错且留下坏数据

复现：团队设置选择现有团队，将编号改为 `abc`，保存。

实际：数据库已经提交 `abc`，随后刷新团队列表时抛出 `NumberFormatException`。后续后台团队页加载也依赖同一数字排序；主控制台初始化亦调用 `Integer.parseInt(team.getCode())`，存在被该值阻断的确定代码路径。未实际重启整套应用验证最后一步。

位置：`TeamConfig.java:241`、`:412`；`ControlPane.java:655`。

测试：`teamNonNumericCodeCommitsBeforeSortFailure`。

建议：写入前验证编号合法性、范围和唯一性，并在加载历史坏数据时给出可修复提示。

### 4. P1：答案前缀中的 %% 会破坏整条界面配置

复现：界面设置将答案前缀设为 `答案%%提示` 并保存，再打开界面设置。

实际：配置 1 从 45 个字段变为 46 个，后续字段全部错位，重新加载 FXML 抛出 `LoadException`。

原因：配置直接用 `%%` 拼接，没有转义，而答案前缀允许任意字符。

位置：`PageConfig.java:738`；`DelimitedConfigCodec.java:19`；`PageConfig.java:443` 起的配置读取。

测试：`pageAnswerPrefixDelimiterCorruptsFieldPositions`。

建议：改用结构化配置并兼容迁移旧数据；短期至少拒绝保留分隔符。

### 5. P1：界面设置多段写入失败会留下部分新配置

复现：通过 SQLite trigger 模拟写入第 2 条配置失败，修改答题 X 坐标为 321，点击保存。

实际：抛出数据库异常，但第 1 条配置中的 321 已提交，后续配置未保存，形成新旧混合状态。

位置：`PageConfig.java:963–1003`，顺序执行多个 `updateById`，没有整体事务。后续还写样式 Excel，数据库与文件也需要一致性设计。

测试：`pageDatabaseFailureLeavesEarlierSectionsCommitted`。

建议：数据库更新整体事务化，文件写入使用临时文件和替换/恢复机制；刷新显示应在存储成功后执行。

### 6. P1：题库导入插入失败，会清空旧题库

复现：载入已有题库，在副本数据库中通过 trigger 模拟题目插入失败，再应用当前题库。

实际：原先题目数大于 0，操作报错后题目数为 0。

原因：先全表删除旧题目，再逐条插入，未使用事务保护。测试执行的是未变更题库选择的分支；变更选择的分支有相同的删除/插入结构，后者未经确认弹窗端到端实测。

位置：`QuestionStock.java:147–169`、`:173–189`。

测试：`questionImportInsertFailureErasesPreviousStock`。

建议：先完整解析和校验，再在事务中替换题库，失败保留旧题目；同时考虑样式文件更新的一致性。

### 7. P2：网页设置允许非法数值入库

复现：网页设置将模式 6 行数填 `abc`，模式 7 列数填 `0`，保存。

实际：两个值均原样写入配置 15，没有校验错误提示。

位置：`WebPageConfig.java:222` 起的字段收集与保存。

测试：`webNumericGarbageIsPersisted`。

建议：按字段定义校验整数、正数、空值和范围。当前只证实后台允许非法数据写入，未测试其浏览器端表现，也未证明它导致桌面比赛入口失败。

### 8. P2：网页设置保存失败被吞掉，没有界面反馈

复现：在已加载的网页设置控制器中注入抛出数据库异常的 mapper，点击保存对应方法。

实际：保存方法正常返回，只向标准错误输出堆栈，没有向用户报告失败，也没有成功/失败返回值。

位置：`WebPageConfig.java:288–290`。

测试：`webSaveDoesNotReportDatabaseFailure`。

建议：记录日志并向后台操作人提示失败；根据持久化结果返回明确状态。

## 重跑方法

需要 Java 17+、本地 Maven 依赖及可运行 JavaFX 的桌面会话。专项测试默认关闭，避免普通测试启动桌面环境。

```sh
python3 tools/testing/prepare_backend_audit.py
mvn -o -q -Dtest=BackendConfigAuditTest -Dbackend.audit=true test
```

准备脚本读取当前仓库数据库，生成副本；重跑将覆盖临时 baseline 和临时资源，不改原始文件。用例每项创建独立数据库，资源副本在本次测试进程中共享；该套审计基于当前仓库快照，部分断言依赖现有配置格式及非空题库、团队数据。

测试源码：`src/test/java/competition/audit/BackendConfigAuditTest.java`。

精简执行结果：`docs/testing/backend-audit-results.json`。完整本机执行日志位于 `/private/tmp/fx-backend-audit/run-final.log`，含比赛副本数据，未复制进仓库。

优先修复建议：先处理环节列表空白、题库事务和团队编号校验，再完善所有配置的校验、保存事务及失败反馈。对于用户报告的「仅网页设置变更」场景，仍需以确切修改字段、数值和当时异常日志继续缩小范围。
