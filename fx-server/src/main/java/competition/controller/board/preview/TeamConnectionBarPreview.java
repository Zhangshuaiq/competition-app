package competition.controller.board.preview;

import competition.util.Navigator;
import competition.util.ScreenAdapter;
import competition.util.TeamRuntime;
import competition.ws.WsTeamMemberWaiter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeamConnectionBarPreview {

    @FXML
    private VBox container;

    private Timeline timeline;

    // 圆圈相关常量
    private static final double CIRCLE_RADIUS = 20.0;
    private static final double STACK_SIZE = 45.0; // 给StackPane更多空间
    private static final double H_SPACING = 10.0;
    private static final double V_SPACING = 10.0;
    private static final double CONTAINER_PADDING = 5.0; // 容器边距
    private static final int ITEMS_PER_ROW = 20;

    @FXML
    public void initialize(){
        WsTeamMemberWaiter.initData();

        // 设置容器间距和边距
        this.container.setSpacing(V_SPACING);
        this.container.setPadding(new Insets(CONTAINER_PADDING));

        // 由于拖拽逻辑已经在Navigator中统一处理，这里只需要确保Windows能响应
        // Navigator会自动处理操作系统差异
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows：设置透明背景让其能响应拖拽
            this.container.setStyle("-fx-background-color: rgba(0, 0, 0, 0.01);");
        }
        // Mac：保持默认透明，Navigator会处理实际内容区域的检测

        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            refresh();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refresh() {
        this.container.getChildren().clear();

        HBox currentRow = new HBox();
        currentRow.setSpacing(H_SPACING);
        container.getChildren().add(currentRow);
        int count = 0;
        int totalItems = 0; // 总元素数量

        Map<String, String> seqMap = TeamRuntime.getSeqMap();
        Map<String, List<String>> teamMemberMapping = TeamRuntime.getTeamMemberMapping();
        LinkedHashMap<String, List<String>> connectMembers = WsTeamMemberWaiter.getConnectMembers();

        for (Map.Entry<String, List<String>> entry : WsTeamMemberWaiter.getConnectTeams().entrySet()) {
            String teamId = entry.getKey();
            List<String> clientIdList = entry.getValue();
            String seq = seqMap.get(teamId);
            if (StringUtils.isEmpty(seq)) {
                continue;
            }

            // 创建圆圈
            Circle circle = new Circle(CIRCLE_RADIUS);
            if (clientIdList != null && !clientIdList.isEmpty()
                && teamMemberMapping.get(teamId).size() == clientIdList.size()
                && teamMemberMapping.get(teamId).equals(connectMembers.get(teamId))) {
                circle.setFill(Color.GREEN);
            } else {
                circle.setFill(Color.RED);
            }

            // 创建文字
            Text text = new Text(seq);
            text.setFill(Color.WHITE);
            text.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

            // 使用 StackPane 并设置固定大小
            StackPane stack = new StackPane();
            stack.getChildren().addAll(circle, text);

            // 关键修复：设置StackPane的固定大小
            stack.setPrefSize(STACK_SIZE, STACK_SIZE);
            stack.setMinSize(STACK_SIZE, STACK_SIZE);
            stack.setMaxSize(STACK_SIZE, STACK_SIZE);

            currentRow.getChildren().add(stack);
            count++;
            totalItems++; // 累计总数

            // 每行20个元素后换行
            if (count % ITEMS_PER_ROW == 0) {
                currentRow = new HBox();
                currentRow.setSpacing(H_SPACING);
                container.getChildren().add(currentRow);
            }
        }

        // 修复后的尺寸计算
        calculateAndSetContainerSize(totalItems);
    }

    /**
     * 计算并设置容器大小
     */
    private void calculateAndSetContainerSize(int totalItems) {
        if (totalItems == 0) {
            // 没有元素时设置最小尺寸
            container.setPrefWidth(100);
            container.setPrefHeight(50);
            if (Navigator.connectionStage != null) {
                Navigator.connectionStage.setWidth(100);
                Navigator.connectionStage.setHeight(50);
            }
            return;
        }

        // 计算行数和每行元素数量
        int totalRows = (int) Math.ceil((double) totalItems / ITEMS_PER_ROW);
        int itemsInLastRow = totalItems % ITEMS_PER_ROW;
        if (itemsInLastRow == 0 && totalItems > 0) {
            itemsInLastRow = ITEMS_PER_ROW; // 最后一行满行
        }

        // 1. 高度计算：行数 * StackPane高度 + 行间距 + 容器内边距
        double contentHeight = STACK_SIZE * totalRows;
        double spacingHeight = V_SPACING * Math.max(0, totalRows - 1);
        double paddingHeight = CONTAINER_PADDING * 2;
        double totalHeight = contentHeight + spacingHeight + paddingHeight;

        // 2. 宽度计算 - 关键修复
        double totalWidth;
        if (totalRows == 1) {
            // 单行：按实际元素数量计算
            double contentWidth = STACK_SIZE * totalItems;
            double spacingWidth = H_SPACING * Math.max(0, totalItems - 1);
            double paddingWidth = CONTAINER_PADDING * 2;
            totalWidth = contentWidth + spacingWidth + paddingWidth;
        } else {
            // 多行：宽度应该按满行计算（每行最多20个元素）
            double contentWidth = STACK_SIZE * ITEMS_PER_ROW;
            double spacingWidth = H_SPACING * (ITEMS_PER_ROW - 1);
            double paddingWidth = CONTAINER_PADDING * 2;
            totalWidth = contentWidth + spacingWidth + paddingWidth;
        }

        // 3. 应用屏幕适配（但先取整避免小数误差）
        int adaptedHeight = (int) totalHeight;
        int adaptedWidth = (int) totalWidth;
        // 4. 设置容器精确尺寸
        container.setPrefHeight(adaptedHeight);
        container.setMinHeight(adaptedHeight);
        container.setMaxHeight(adaptedHeight);

        container.setPrefWidth(adaptedWidth);
        container.setMinWidth(adaptedWidth);
        container.setMaxWidth(adaptedWidth);

        // 5. 设置窗口精确尺寸（窗口大小 = 内容大小，无额外边距）
        if (Navigator.connectionStage != null) {
            Navigator.connectionStage.setWidth(adaptedWidth);
            Navigator.connectionStage.setHeight(adaptedHeight);

            // 确保窗口大小固定，不可调整
            Navigator.connectionStage.setResizable(false);
        }
    }

    public void onWindowClose(){
        System.out.println("监听到连接界面关闭....... 停止实时加载");
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }
}