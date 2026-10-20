package competition.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.StagesMapper;
import competition.util.GlobalCache;
import competition.util.ImageResource;
import competition.util.PreviewStack;
import competition.util.TeamRuntime;
import competition.ws.QuestionSubmitRuntime;
import competition.ws.StageSelectRuntime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class StageConfig {

    private boolean add;

    private int addLevel;

    private Stages current;

    private ToggleGroup timeCountType;

    // 出题方式
    private ToggleGroup ccType;

    @FXML
    private TextField stageNoText;

    @Resource
    private StagesMapper stagesMapper;

    @FXML
    private HBox stageAddress;

    // 当前环节是否以抢答模式打开
    @FXML
    private CheckBox qdMode, qdDescScore;

    @FXML
    private ComboBox<String> paChoice, ruleChoice;

    @FXML
    private CheckBox countTotal;

    @FXML
    private TextField desValue, buzzDescInput;

    @FXML
    private TextField nameInput;

    @FXML
    TextField scoreInput;

    @FXML
    TextField timeDescInput;

    @FXML
    CheckBox showNumberSelect;

    @FXML
    CheckBox selectGreySelect;

    @FXML
    RadioButton timeType1, timeType2;

    @FXML
    RadioButton ccType1, ccType2, ccType3, ccType4, ccType5, ccType6, ccType7, ccType8, ccType9;

    @FXML
    CheckBox randomSort, randomSort2, stageShow;

    @FXML
    TextArea ruleText;

    @FXML
    Label ruleImg;

    @FXML
    ImageView previewImg;

    @FXML
    CheckBox model6Check, answerShow, freeAnswerSingle, ccType8Mode, ccType8AnswerShow;

    @Resource
    private ControlPane controlPane;

    @FXML
    CheckBox timeOrder;

    private Map<Integer, String> dataMap = new HashMap<>();

    private VBox firstBox = new VBox();

    private VBox secondBox = new VBox();

    private VBox thirdBox = new VBox();

    private List<VBox> levelList;

    private List<Integer> recorded;

    private String filePath;

    private final Comparator<Stages> comparator = (o1, o2) -> {
        if (o1.getSortVal().length() > o2.getSortVal().length()) {
            return 1;
        } else if (o1.getSortVal().length() < o2.getSortVal().length()) {
            return -1;
        }
        String[] split1 = o1.getSortVal().split("\\.");
        String[] split2 = o2.getSortVal().split("\\.");
        return Integer.compare(Integer.parseInt(split1[split1.length - 1]), Integer.parseInt(split2[split2.length - 1]));
    };

    @FXML
    public void initialize() {

        buzzDescInput.setText("0");
        recorded = new ArrayList<>();
        firstBox.getChildren().clear();
        secondBox.getChildren().clear();
        thirdBox.getChildren().clear();

        firstBox.setPrefWidth(171);
        firstBox.setPrefHeight(772);
        firstBox.setSpacing(10);
        firstBox.setPadding(new Insets(10, 0, 0, 0));
        secondBox.setPrefWidth(171);
        secondBox.setPrefHeight(772);
        secondBox.setSpacing(10);
        secondBox.setPadding(new Insets(10, 0, 0, 0));
        thirdBox.setPrefWidth(171);
        thirdBox.setPrefHeight(772);
        thirdBox.setSpacing(10);
        thirdBox.setPadding(new Insets(10, 0, 0, 0));
        levelList = Arrays.asList(firstBox, secondBox, thirdBox);

        stageAddress.getChildren().add(firstBox);
        stageAddress.getChildren().add(secondBox);
        stageAddress.getChildren().add(thirdBox);

        dataMap.put(1, "连续出题");
        dataMap.put(2, "自选题目");
        dataMap.put(3, "自选题组");
        dataMap.put(4, "滚动选题");
        dataMap.put(5, "线索题");
        dataMap.put(6, "答题模式");
        dataMap.put(7, "竞技模式");
        dataMap.put(8, "平板自选");
        dataMap.put(Stages.FREE_ANSWER_TYPE, "自由答题");
        timeCountType = new ToggleGroup();
        timeCountType.getToggles().addAll(timeType1, timeType2);

        ccType = new ToggleGroup();
        ccType.getToggles().addAll(ccType1, ccType2, ccType3, ccType4, ccType5, ccType6, ccType7, ccType8, ccType9);
        ccType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            randomSort2.setVisible(newValue == ccType7);
            model6Check.setVisible(newValue == ccType6);
            answerShow.setVisible(newValue == ccType6);
            ccType8Mode.setVisible(newValue == ccType8);
            ccType8AnswerShow.setVisible(newValue == ccType8);
            freeAnswerSingle.setVisible(newValue == ccType9);
        });

        paChoice.getItems().addAll("直接抢答",
            "标准模式",
            "淘汰模式"
        );

        ruleChoice.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("图片")) {
                    ruleText.setVisible(false);
                    previewImg.setVisible(false);
                    ruleImg.setVisible(true);
                } else {
                    ruleText.setVisible(true);
                    ruleImg.setVisible(false);
                    previewImg.setVisible(false);
                }
            }
        });

        ruleChoice.getItems().addAll(
            "文字",
            "图片"
        );
        ruleChoice.setValue("文字");

        // 上传图片
        previewImg.setOnMouseClicked(this::handleUpload);
        ruleImg.setOnMouseClicked(this::handleUpload);
        showStageData();
    }

    private void showStageData(){
        List<Stages> stages = stagesMapper.selectList(new LambdaQueryWrapper<>());
        final List<Stages> firstStages = stages.stream().filter(x -> x.getParentId() == null)
                .sorted(comparator).collect(Collectors.toList());
        try {
            nextStageData(firstStages, firstBox, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void nextStageData(List<Stages> firstStages, VBox firstBox, int level) throws FileNotFoundException {
        for (int i = 0; i < firstStages.size(); i++) {
            Stages stage = firstStages.get(i);
            HBox row = new HBox();
            row.setPrefHeight(50);
            row.setPrefWidth(171);

            Label title = new Label( stage.getSortVal());
            title.setPrefHeight(50);
            title.setPrefWidth(50);
            title.setAlignment(Pos.CENTER);
            title.getStyleClass().add("stage-label");
            AnchorPane rightArea = new AnchorPane();
            rightArea.setPrefHeight(50);
            rightArea.setPrefWidth(130);
            Label description = new Label(stage.getName() + "\n" + stage.getDescTime() + "\\" + stage.getScorePerQ() + "\\" + dataMap.get(stage.getQuestionType()));
            description.setPrefHeight(50);
            description.setPrefWidth(130);
            description.setAlignment(Pos.CENTER);
            description.getStyleClass().add("stage-title");
            rightArea.getChildren().add(description);
            HBox operarions = new HBox();
            operarions.setVisible(false);
            operarions.setLayoutY(-10);
            operarions.setLayoutX(70);
            operarions.setPrefWidth(60);
            operarions.setPrefHeight(20);
            operarions.setSpacing(1);
            operarions.setPadding(new Insets(1, 0 ,1 , 0));
            Label up = new Label();
            up.setPrefWidth(20);
            up.setPrefHeight(20);
            up.setBackground(new Background(new BackgroundImage(new Image("file:" + ImageResource.UP()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(20, 20, false, false, false, false))));
            up.setTextFill(Color.WHITE);
            up.setAlignment(Pos.CENTER);
            up.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    sortStage(stage, true);
                }
            });
            Label down = new Label();
            down.setPrefWidth(20);
            down.setPrefHeight(20);
            down.setBackground(new Background(new BackgroundImage(new Image("file:" + ImageResource.DOWN()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(20, 20, false, false, false, false))));
            down.setTextFill(Color.WHITE);
            down.setAlignment(Pos.CENTER);
            down.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    sortStage(stage, false);
                }
            });

            Label delete = new Label();
            delete.setPrefWidth(20);
            delete.setPrefHeight(20);
            delete.setBackground(new Background(new BackgroundImage(new Image("file:" + ImageResource.DELETE()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(20, 20, false, false, false, false))));
            delete.setTextFill(Color.WHITE);
            delete.setAlignment(Pos.CENTER);
            delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    deleteStage(stage);
                }
            });

            operarions.getChildren().addAll(up, down, delete);
            rightArea.getChildren().add(operarions);
            row.getChildren().addAll(title, rightArea);
            recorded.add(stage.getId());
            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @SneakyThrows
                @Override
                public void handle(MouseEvent event) {
                    // 标记为修改
                    add = false;
                    clearClicked();
                    operarions.setVisible(true);
                    description.getStyleClass().add("stage-title-clicked");
                    current = stage;
                    showCurrentInfo();
                    // 显示下一阶段的数据
                    List<Stages> stages = stagesMapper.selectList(new LambdaQueryWrapper<>());
                    final List<Stages> firstStages = stages.stream().filter(x -> Objects.equals(current.getId(), x.getParentId()))
                        .sorted(comparator).collect(Collectors.toList());
                    Integer level = current.getLevel();
                    // 清除子环节
                    for (int i1 = 0; i1 < levelList.size(); i1++) {
                        if (i1 >= level) {
                            levelList.get(i1).getChildren().clear();
                        }
                    }
                    if (level == levelList.size()) {
                        return;
                    }
                    nextStageData(firstStages, levelList.get(level), current.getLevel() + 1);
                }
            });
            firstBox.getChildren().add(row);
        }
        StackPane stackPane = new StackPane();
        Label addLab = new Label();
        addLab.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(ImageResource.添加()), 171, 50, true, true),
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        addLab.setPrefHeight(50);
        addLab.setPrefWidth(171);
        addLab.setAlignment(Pos.CENTER);
        addLab.setTextFill(Color.BLACK);
        addLab.setFont(Font.font(24));
        stackPane.getChildren().add(addLab);
        firstBox.getChildren().add(stackPane);
        stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clearClicked();
                add = true;
                addLevel = level;
                stackPane.getStyleClass().add("stage-title-clicked");

                if (addLevel == 1) {
                    int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                        .isNull(Stages::getParentId)).size() + 1;
                    stageNoText.setText((size) + "");
                } else  if (addLevel == 2) {
                    if (current.getLevel() == 1) {
                        int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, current.getId())
                        ).size() + 1;
                        stageNoText.setText(current.getSortVal() + "." + (size));
                    } else if (current.getLevel() == 2) {
                        Stages p = stagesMapper.selectById(current.getParentId());
                        int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, p.getId())
                        ).size() + 1;
                        stageNoText.setText(p.getSortVal() + "." + (size));
                    }

                } else  if (addLevel == 3) {
                    if (current.getLevel() == 2) {
                        int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, current.getId())
                        ).size() + 1;
                        stageNoText.setText(current.getSortVal() + "." + (size));
                    } else if (current.getLevel() == 3) {
                        Stages p = stagesMapper.selectById(current.getParentId());
                        int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getParentId, p.getId())
                        ).size() + 1;
                        stageNoText.setText(p.getSortVal() + "." + (size));
                    }
                }

            }
        });
    }

    private void sortStage(Stages stages, boolean up) {
        Integer level = stages.getLevel();
        List<Stages> allStages = stagesMapper.selectList(new LambdaQueryWrapper<>());

        // 获取同级别的兄弟环节
        List<Stages> siblings = getSiblingsOfStage(allStages, stages);

        // 找到当前环节在兄弟环节中的位置
        int currentIndex = -1;
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i).getId().equals(stages.getId())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            return;
        }

        // 确定要交换的目标环节
        Stages targetStage = null;
        if (up && currentIndex > 0) {
            targetStage = siblings.get(currentIndex - 1);
        } else if (!up && currentIndex < siblings.size() - 1) {
            targetStage = siblings.get(currentIndex + 1);
        }

        if (targetStage == null) {
            return; // 无法移动
        }

        // 执行交换
        swapStagesAndChildren(allStages, stages, targetStage);

        // 刷新显示
        refreshStageDisplay();
    }

    /**
     * 获取指定环节的所有兄弟环节（包括自己）
     */
    private List<Stages> getSiblingsOfStage(List<Stages> allStages, Stages stage) {
        if (stage.getLevel() == 1) {
            // 顶级环节：parentId为null的所有环节
            return allStages.stream()
                .filter(x -> x.getLevel() == 1 && x.getParentId() == null)
                .sorted(comparator)
                .collect(Collectors.toList());
        } else {
            // 子环节：相同parentId的所有环节
            return allStages.stream()
                .filter(x -> x.getLevel().equals(stage.getLevel()) &&
                    Objects.equals(x.getParentId(), stage.getParentId()))
                .sorted(comparator)
                .collect(Collectors.toList());
        }
    }

    private void swapStagesAndChildren(List<Stages> allStages, Stages stage1, Stages stage2) {
        String originalSortVal1 = stage1.getSortVal();
        String originalSortVal2 = stage2.getSortVal();

        System.out.println("交换前: " + originalSortVal1 + " <-> " + originalSortVal2);

        // 第一步：交换两个主环节的序号
        stage1.setSortVal(originalSortVal2);
        stage2.setSortVal(originalSortVal1);
        stagesMapper.updateById(stage1);
        stagesMapper.updateById(stage2);
        updateCurrentStage();
        System.out.println("交换后: " + stage1.getSortVal() + " <-> " + stage2.getSortVal());

        // 第二步：递归更新stage1的所有子孙环节
        updateAllDescendants(allStages, stage1.getId(), originalSortVal1, originalSortVal2);

        // 第三步：递归更新stage2的所有子孙环节
        updateAllDescendants(allStages, stage2.getId(), originalSortVal2, originalSortVal1);
    }

    /**
     * 递归更新指定环节的所有子孙环节的序号
     * @param allStages 所有环节列表
     * @param parentId 父环节ID
     * @param oldPrefix 旧的序号前缀
     * @param newPrefix 新的序号前缀
     */
    private void updateAllDescendants(List<Stages> allStages, Integer parentId, String oldPrefix, String newPrefix) {
        // 找到所有直接子环节
        List<Stages> directChildren = allStages.stream()
            .filter(x -> Objects.equals(x.getParentId(), parentId))
            .collect(Collectors.toList());

        for (Stages child : directChildren) {
            String oldChildSortVal = child.getSortVal();

            // 更新子环节的序号：将旧前缀替换为新前缀
            String newChildSortVal = oldChildSortVal.replaceFirst("^" + Pattern.quote(oldPrefix), newPrefix);

            System.out.println("  子环节更新: " + oldChildSortVal + " -> " + newChildSortVal);

            child.setSortVal(newChildSortVal);
            stagesMapper.updateById(child);
            updateCurrentStage();
            // 递归更新这个子环节的所有子孙环节
            updateAllDescendants(allStages, child.getId(), oldChildSortVal, newChildSortVal);
        }
    }

    /**
     * 修复后的删除环节方法
     */
    private void deleteStage(Stages stages) {
        // 1. 记录被删除环节的信息，用于后续重新排序
        String deletedSortVal = stages.getSortVal();
        Integer deletedLevel = stages.getLevel();
        Integer deletedParentId = stages.getParentId();

        // 2. 删除指定环节
        stagesMapper.deleteById(stages.getId());

        // 3. 递归删除所有子环节
        deleteAllChildrenRecursively(stages.getId());

        // 4. 重新排序同级环节
        reorderSiblingsAfterDeletion(deletedSortVal, deletedLevel, deletedParentId);

        // 5. 刷新显示
        refreshStageDisplay();
    }

    /**
     * 递归删除所有子环节
     */
    private void deleteAllChildrenRecursively(Integer parentId) {
        List<Stages> waitForDelete = new ArrayList<>();
        List<Stages> stagesList = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
            .eq(Stages::getParentId, parentId)
        );

        while (!stagesList.isEmpty()) {
            waitForDelete.addAll(stagesList);
            List<Integer> parentIds = stagesList.stream().map(Stages::getId).collect(Collectors.toList());
            stagesList = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .in(Stages::getParentId, parentIds)
            );
        }

        if (!waitForDelete.isEmpty()) {
            List<Integer> deleteIds = waitForDelete.stream().map(Stages::getId).collect(Collectors.toList());
            stagesMapper.deleteBatchIds(deleteIds);
        }
    }

    /**
     * 删除后重新排序同级环节
     */
    private void reorderSiblingsAfterDeletion(String deletedSortVal, Integer deletedLevel, Integer deletedParentId) {
        // 获取所有环节
        List<Stages> allStages = stagesMapper.selectList(new LambdaQueryWrapper<>());

        // 获取被删除环节的同级环节
        List<Stages> siblings;
        if (deletedLevel == 1) {
            // 顶级环节：parentId为null的所有环节
            siblings = allStages.stream()
                .filter(x -> x.getLevel() == 1 && x.getParentId() == null)
                .sorted(comparator)
                .collect(Collectors.toList());
        } else {
            // 子环节：相同parentId的所有环节
            siblings = allStages.stream()
                .filter(x -> x.getLevel().equals(deletedLevel) &&
                    Objects.equals(x.getParentId(), deletedParentId))
                .sorted(comparator)
                .collect(Collectors.toList());
        }

        // 重新分配序号
        if (deletedLevel == 1) {
            // 顶级环节：重新分配为 1, 2, 3, ...
            reorderTopLevelStages(siblings, allStages);
        } else {
            // 子环节：重新分配序号
            reorderChildLevelStages(siblings, deletedParentId, allStages);
        }
    }

    /**
     * 重新排序顶级环节
     */
    private void reorderTopLevelStages(List<Stages> topLevelSiblings, List<Stages> allStages) {
        for (int i = 0; i < topLevelSiblings.size(); i++) {
            Stages stage = topLevelSiblings.get(i);
            String oldSortVal = stage.getSortVal();
            String newSortVal = String.valueOf(i + 1);

            if (!oldSortVal.equals(newSortVal)) {
                System.out.println("顶级环节重新排序: " + oldSortVal + " -> " + newSortVal);

                // 更新当前环节
                stage.setSortVal(newSortVal);
                stagesMapper.updateById(stage);
                updateCurrentStage();
                // 递归更新所有子孙环节
                updateAllDescendants(allStages, stage.getId(), oldSortVal, newSortVal);
            }
        }
    }

    /**
     * 重新排序子级环节
     */
    private void reorderChildLevelStages(List<Stages> childSiblings, Integer parentId, List<Stages> allStages) {
        // 获取父环节信息
        Stages parentStage = allStages.stream()
            .filter(x -> x.getId().equals(parentId))
            .findFirst()
            .orElse(null);

        if (parentStage == null) {
            return;
        }

        String parentSortVal = parentStage.getSortVal();

        for (int i = 0; i < childSiblings.size(); i++) {
            Stages stage = childSiblings.get(i);
            String oldSortVal = stage.getSortVal();
            String newSortVal = parentSortVal + "." + (i + 1);

            if (!oldSortVal.equals(newSortVal)) {
                System.out.println("子环节重新排序: " + oldSortVal + " -> " + newSortVal);

                // 更新当前环节
                stage.setSortVal(newSortVal);
                stagesMapper.updateById(stage);
                updateCurrentStage();
                // 递归更新所有子孙环节
                updateAllDescendants(allStages, stage.getId(), oldSortVal, newSortVal);
            }
        }
    }

    /**
     * 刷新环节显示
     */
    private void refreshStageDisplay() {
        firstBox.getChildren().clear();
        secondBox.getChildren().clear();
        thirdBox.getChildren().clear();
        recorded.clear();
        showStageData();
    }

    private void clearClicked(){
        final ObservableList<Node> children = stageAddress.getChildren();
        for (Node child : children) {
            VBox box1 = (VBox) child;
            for (Node box1Child : box1.getChildren()) {
                if (!(box1Child instanceof HBox)) {
                    box1Child.getStyleClass().remove("stage-title-clicked");
                    continue;
                }
                HBox hBox1 = (HBox) box1Child;
                AnchorPane anchorPane = (AnchorPane) hBox1.getChildren().get(1);
                anchorPane.getChildren().get(0).getStyleClass().remove("stage-title-clicked");
                HBox b = (HBox) anchorPane.getChildren().get(1);
                b.setVisible(false);
            }
        }
    }

    private void showCurrentInfo(){
        if (current != null) {
            stageNoText.setText(current.getSortVal());
            nameInput.setText(current.getName());
            buzzDescInput.setText(current.getQdDescValue().toString());
            scoreInput.setText(current.getScorePerQ() + "");
            timeDescInput.setText(current.getDescTime() + "");
            showNumberSelect.setSelected(current.getShowNumber() == 1);
            selectGreySelect.setSelected(current.getSelectGrey() == 1);
            randomSort.setSelected(current.getRandomSort() == 1);
            stageShow.setSelected(current.getShow() == 1);
            model6Check.setSelected(current.getQuestionType() == 6 && current.getM6() == 1);
            randomSort2.setSelected(current.getrRandomSort() == 1);
            qdMode.setSelected(current.getOpenQd() == 1);
            qdDescScore.setSelected(current.getQdDesc() == 1);
            paChoice.setValue(current.getpAType());
            ruleChoice.setValue(current.getRuleType());
            timeOrder.setSelected(current.getOrderVal() == 1);
            countTotal.setSelected(current.getCountTotal() == 1);
            answerShow.setSelected(current.getAnswerSingle() == 1);
            freeAnswerSingle.setSelected(current.getAnswerSingle() == 1);
            desValue.setText(current.getDesValue().toString());
            ccType8Mode.setSelected(current.getQuestionType() == 8 && current.getBoardAnswer() == 1);
            ccType8AnswerShow.setSelected(current.getTeamAnswerSingle() == 1);
            for (Toggle toggle : ccType.getToggles()) {
                if (toggle.getUserData().toString().equals(current.getQuestionType().toString())) {
                    toggle.setSelected(true);
                    ccType.selectToggle(toggle);
                    break;
                }
            }
            for (Toggle toggle : timeCountType.getToggles()) {
                if (toggle.getUserData().toString().equals(current.getTimeCType().toString())) {
                    toggle.setSelected(true);
                    timeCountType.selectToggle(toggle);
                    break;
                }
            }
            ruleText.setVisible(true);
            ruleImg.setVisible(false);
            ruleText.setText(current.getRuleContent());
            previewImg.setVisible(false);
        }
    }

    public void handleUpload(MouseEvent event) {
        FileChooser imageChooser = new FileChooser();
        imageChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));
        final File file = imageChooser.showOpenDialog(null);
        if (file != null) {
            previewImg.setVisible(true);
            try {
                previewImg.setImage(new Image(new FileInputStream(file)));
                filePath = file.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void updateStage(){
        if (current == null) {
            return;
        }
        // 环节名称
        final String stageName = nameInput.getText();
        // 每题分
        final String score = scoreInput.getText();
        // 倒计时
        final String timeDesc = timeDescInput.getText();
        // 显示题号
        final boolean selected = showNumberSelect.isSelected();
        // 选后变灰色
        final boolean greySelect = selectGreySelect.isSelected();
        // 随机乱序 1
        final boolean randomSort = this.randomSort.isSelected();
        // 环节显示
        final boolean showSelect = stageShow.isSelected();
        // 随机乱序 2
        final boolean randomSort2Selected = randomSort2.isSelected();

        // 抢答方式
        final String paTypeValue = paChoice.getValue();

        //是否开启抢答模式
        final boolean openQD = qdMode.isSelected();

        final boolean qdDesc = qdDescScore.isSelected();

        // 环节规则类型
        final String value = "文字";
        String data = "";
        if ("文字".equals(value)) {
            data = ruleText.getText();
        } else {
            data = filePath;
        }

        // 环节规则内容

        // 计时方式
        final Object countData = timeCountType.getSelectedToggle().getUserData();
        // 出题方式
        final Object ccData = ccType.getSelectedToggle().getUserData();

        current.setScorePerQ(Integer.parseInt(score));
        current.setDescTime(Integer.parseInt(timeDesc));
        current.setName(stageName);
        current.setRandomSort(randomSort?1:0);
        current.setShowNumber(selected?1:0);
        current.setSelectGrey(greySelect?1:0);
        current.setTimeCType(Integer.parseInt(countData.toString()));
        current.setShow(showSelect?1:0);
        // 子环节强制继承顶层父环节的 questionType，顶层环节可自由设置并向下传播
        Integer newQuestionType = Integer.parseInt(ccData.toString());
        if (current.getParentId() != null) {
            newQuestionType = findRootQuestionType(current);
        }
        current.setQuestionType(newQuestionType);
        current.setpAType(paTypeValue);
        current.setM6(Objects.equals(6, newQuestionType) && model6Check.isSelected() ? 1 : 0);
        current.setRuleType(value);
        current.setRuleContent(data);
        current.setrRandomSort(randomSort2Selected?1:0);
        current.setOpenQd(openQD?1:0);
        current.setQdDesc(qdDesc?1:0);
        current.setOrderVal(timeOrder.isSelected()? 1: 0);
        current.setDesValue(Integer.parseInt(desValue.getText()));
        current.setCountTotal(countTotal.isSelected()? 1: 0);
        current.setAnswerSingle(Objects.equals(Stages.FREE_ANSWER_TYPE, newQuestionType)
                ? (freeAnswerSingle.isSelected() ? 1 : 0)
                : (Objects.equals(6, newQuestionType) && answerShow.isSelected() ? 1 : 0));
        current.setQdDescValue(Integer.parseInt(buzzDescInput.getText()));
        current.setTeamAnswerSingle(Objects.equals(8, newQuestionType) && ccType8AnswerShow.isSelected() ? 1 : 0);
        current.setBoardAnswer(Objects.equals(8, newQuestionType) && ccType8Mode.isSelected() ? 1 : 0);
        stagesMapper.updateById(current);
        syncDescendantsQuestionType(current.getId(), current.getQuestionType());
        updateCurrentStage();
        refreshStageDisplay();
//        PreviewPane previewPane = controlPane.getPreviewPane();
//        if (previewPane != null) {
//            previewPane.reload();
//        }
    }

    @FXML
    public void submitStage() {
        if (!add) {
            updateStage();
            return;
        }
        // 环节名称
        final String stageName = nameInput.getText();
        // 每题分
        final String score = scoreInput.getText();
        // 倒计时
        final String timeDesc = timeDescInput.getText();
        // 显示题号
        final boolean selected = showNumberSelect.isSelected();
        // 选后变灰色
        final boolean greySelect = selectGreySelect.isSelected();
        // 随机乱序 1
        final boolean randomSort = this.randomSort.isSelected();
        // 环节显示
        final boolean showSelect = stageShow.isSelected();
        // 随机乱序 2
        final boolean randomSort2Selected = randomSort2.isSelected();

        // 抢答方式
        final String paTypeValue = paChoice.getValue();

        //是否开启抢答模式
        final boolean openQD = qdMode.isSelected();

        // 环节规则类型
        final String value = ruleChoice.getValue();
        String data = "";
        if ("文字".equals(value)) {
            data = ruleText.getText();
        } else {
            data = filePath;
        }

        // 环节规则内容

        // 计时方式
        final Object countData = timeCountType.getSelectedToggle().getUserData();
        // 出题方式
        final Object ccData = ccType.getSelectedToggle().getUserData();

        Stages stages = new Stages();
        stages.setScorePerQ(Integer.parseInt(score));
        stages.setDescTime(Integer.parseInt(timeDesc));
        stages.setName(stageName);
        stages.setRandomSort(randomSort?1:0);
        stages.setShowNumber(selected?1:0);
        stages.setSelectGrey(greySelect?1:0);
        stages.setTimeCType(Integer.parseInt(countData.toString()));
        stages.setShow(showSelect?1:0);
        stages.setQuestionType(Integer.parseInt(ccData.toString()));
        stages.setpAType(paTypeValue);
        stages.setRuleType(value);
        stages.setRuleContent(data);
        int selectedQuestionType = Integer.parseInt(ccData.toString());
        stages.setM6(selectedQuestionType == 6 && model6Check.isSelected() ? 1 : 0);
        stages.setrRandomSort(randomSort2Selected?1:0);
        stages.setOpenQd(openQD?1:0);
        stages.setQdDesc(qdDescScore.isSelected()?1:0);
        stages.setDesValue(Integer.parseInt(desValue.getText()));
        stages.setCountTotal(countTotal.isSelected()? 1: 0);
        stages.setAnswerSingle(selectedQuestionType == Stages.FREE_ANSWER_TYPE
                ? (freeAnswerSingle.isSelected() ? 1 : 0)
                : (selectedQuestionType == 6 && answerShow.isSelected() ? 1 : 0));
        stages.setQdDescValue(Integer.parseInt(buzzDescInput.getText()));
        stages.setOrderVal(timeOrder.isSelected()? 1: 0);
        stages.setTeamAnswerSingle(selectedQuestionType == 8 && ccType8AnswerShow.isSelected() ? 1 : 0);
        stages.setBoardAnswer(selectedQuestionType == 8 && ccType8Mode.isSelected() ? 1 : 0);
        if (addLevel == 1) {
            int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .isNull(Stages::getParentId)).size() + 1;
            stages.setLevel(1);
            stages.setSortVal((size) + "");
        } else  if (addLevel == 2) {
            if (current.getLevel() == 1) {
                stages.setParentId(current.getId());
                stages.setLevel(current.getLevel() + 1);
                int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getParentId, current.getId())
                ).size() + 1;
                stages.setSortVal(current.getSortVal() + "." + (size));
            } else if (current.getLevel() == 2) {
                Stages p = stagesMapper.selectById(current.getParentId());
                stages.setParentId(p.getId());
                stages.setLevel(p.getLevel() + 1);
                int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getParentId, p.getId())
                ).size() + 1;
                stages.setSortVal(p.getSortVal() + "." + (size));
            }

        } else  if (addLevel == 3) {
            if (current.getLevel() == 2) {
                stages.setParentId(current.getId());
                stages.setLevel(current.getLevel() + 1);
                int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getParentId, current.getId())
                ).size() + 1;
                stages.setSortVal(current.getSortVal() + "." + (size));
            } else if (current.getLevel() == 3) {
                Stages p = stagesMapper.selectById(current.getParentId());
                stages.setParentId(p.getId());
                stages.setLevel(p.getLevel() + 1);
                int size = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .eq(Stages::getParentId, p.getId())
                ).size() + 1;
                stages.setSortVal(p.getSortVal() + "." + (size));
            }
        }

        // 子环节必须继承顶层父环节的 questionType
        if (stages.getParentId() != null) {
            Stages parent = stagesMapper.selectById(stages.getParentId());
            if (parent != null) {
                stages.setQuestionType(findRootQuestionType(parent));
            }
        }
        if (stages.isFreeAnswerMode()) {
            stages.setM6(0);
            stages.setBoardAnswer(0);
            stages.setTeamAnswerSingle(0);
            stages.setAnswerSingle(freeAnswerSingle.isSelected() ? 1 : 0);
        }
        stagesMapper.insert(stages);
        refreshStageDisplay();
    }

    private Integer findRootQuestionType(Stages stage) {
        Stages cur = stage;
        while (cur.getParentId() != null) {
            Stages parent = stagesMapper.selectById(cur.getParentId());
            if (parent == null) break;
            cur = parent;
        }
        return cur.getQuestionType();
    }

    private void syncDescendantsQuestionType(Integer parentId, Integer questionType) {
        List<Stages> children = stagesMapper.selectList(
            new LambdaQueryWrapper<Stages>().eq(Stages::getParentId, parentId)
        );
        for (Stages child : children) {
            child.setQuestionType(questionType);
            if (child.isFreeAnswerMode()) {
                child.setM6(0);
                child.setBoardAnswer(0);
                child.setTeamAnswerSingle(0);
            }
            stagesMapper.updateById(child);
            syncDescendantsQuestionType(child.getId(), questionType);
        }
    }

    private void updateCurrentStage(){
        Stages currentStage = controlPane.getCurrentStage();
        if (currentStage != null) {
            controlPane.updateCurrentStage(null);
        }
        PreviewStack.clear();
        // 清空运行时状态，避免残留到状态栏
        GlobalCache.showAnswer = false;
        QuestionSubmitRuntime.clear();
        TeamRuntime.resetStatus();
        for (Teams team : TeamRuntime.getTeams()) {
            team.setTeamSelect("");
        }
        controlPane.closeWindow();
    }
}
