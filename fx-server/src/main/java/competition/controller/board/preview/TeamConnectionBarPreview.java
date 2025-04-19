package competition.controller.board.preview;

import competition.util.TeamRuntime;
import competition.ws.WsTeamMemberWaiter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
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

    @FXML
    public void initialize(){

        WsTeamMemberWaiter.initData();

        this.container.setSpacing(10);

        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            refresh();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refresh() {
        this.container.getChildren().clear();

        HBox currentRow = new HBox();
        currentRow.setSpacing(10);
        container.getChildren().add(currentRow);
        int count = 0;

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
            Circle circle = new Circle(20); // 增大圆圈半径以放入文字
            if (clientIdList != null && !clientIdList.isEmpty()
                    && teamMemberMapping.get(teamId).size() == clientIdList.size()
                    && teamMemberMapping.get(teamId).equals(connectMembers.get(teamId))) {
                circle.setFill(Color.GREEN);
            } else {
                circle.setFill(Color.RED);
            }

            Text text = new Text(seq);
            text.setFill(Color.WHITE); // 设置文字为白色
            text.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

            // 使用 StackPane 使文字居中叠加在圆圈中
            javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane();
            stack.getChildren().addAll(circle, text);

            currentRow.getChildren().add(stack);
            count++;

            if (count % 20 == 0) {
                currentRow = new HBox();
                currentRow.setSpacing(10);
                container.getChildren().add(currentRow);
            }
        }
        this.container.autosize();
    }


    public void onWindowClose(){
        System.out.println("监听到连接界面关闭....... 停止实时加载");
        timeline.stop();
        timeline = null;
    }
}
