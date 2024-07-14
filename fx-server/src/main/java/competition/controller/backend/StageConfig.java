package competition.controller.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.entity.Stages;
import competition.mapper.StagesMapper;
import competition.util.ImageResource;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
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
    
    
    @FXML
    private CheckBox answerShow;

    @Resource
    private StagesMapper stagesMapper;

    @FXML
    private HBox stageAddress;

    @FXML
    private ComboBox<String> paChoice, ruleChoice;

    @FXML
    private CheckBox countTotal;
    
    @FXML
    private TextField desValue;

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
    RadioButton ccType1, ccType2, ccType3, ccType4, ccType5, ccType6, ccType7;

    @FXML
    CheckBox randomSort, randomSort2, stageShow;

    @FXML
    TextArea ruleText;

    @FXML
    Label ruleImg;

    @FXML
    ImageView previewImg;
    
    @FXML
    CheckBox model6Check;
    
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
    
    @FXML
    public void initialize() {
        
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
        timeCountType = new ToggleGroup();
        timeCountType.getToggles().addAll(timeType1, timeType2);

        ccType = new ToggleGroup();
        ccType.getToggles().addAll(ccType1, ccType2, ccType3, ccType4, ccType5, ccType6, ccType7);


        ccType1.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(false);
                }
            }
        });

        ccType2.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(false);
                }
            }
        });

        ccType3.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(false);
                }
            }
        });

        ccType4.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(false);
                }
            }
        });

        ccType5.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(false);
                }
            }
        });
        
        ccType6.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(true);
                }
            }
        });

        ccType7.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    model6Check.setVisible(false);
                }
            }
        });

        paChoice.getItems().addAll("直接抢答",
                "1秒后第一个抢答成功,提前为违规",
                "3秒后第一个抢答成功,提前为违规",
                "4秒后第一个抢答成功,提前为违规",
                "5秒后第一个抢答成功,提前为违规");

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
        final List<Stages> firstStages = stages.stream().filter(x -> x.getParentId() == null).sorted(Comparator.comparing(Stages::getSortVal)).collect(Collectors.toList());
        try {
            nextStageData(firstStages, firstBox, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void nextStageData(List<Stages> firstStages, VBox firstBox, int level) throws FileNotFoundException {
//        if (CollectionUtils.isEmpty(firstStages)) {
//            return;
//        }
       
        for (int i = 0; i < firstStages.size(); i++) {
            Stages stage = firstStages.get(i);
//            if (recorded.contains(stage.getId())) {
//                continue;
//            }
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
                            .sorted(Comparator.comparing(Stages::getSortVal)).collect(Collectors.toList());
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
    
    private void sortStage(Stages stages, boolean up){
        Integer level = stages.getLevel();
        List<Stages> list = stagesMapper.selectList(new LambdaQueryWrapper<>());
        List<Stages> filtered = list.stream().filter(x -> x.getLevel().equals(level)).sorted(Comparator.comparing(Stages::getSortVal)).collect(Collectors.toList());
        if (up) {
            if (filtered.get(0).getSortVal().equals(stages.getSortVal())) {
                return;
            }
            List<Stages> collect = filtered.stream().filter(x -> x.getSortVal().compareTo(stages.getSortVal()) < 0).collect(Collectors.toList());
            String sortVal = stages.getSortVal();
            Stages changed = collect.get(collect.size() - 1);
          
            // update children's sort value

            List<Stages> nextLevelStages = list.stream().filter(x -> x.getLevel() >= (level + 1)).collect(Collectors.toList());
            for (Stages levelStage : nextLevelStages) {
                if (levelStage.getSortVal().startsWith(changed.getSortVal())) {
                    levelStage.setSortVal(levelStage.getSortVal().replace(changed.getSortVal(), sortVal));
                    stagesMapper.updateById(levelStage);
                }

                if (levelStage.getSortVal().startsWith(sortVal)) {
                    levelStage.setSortVal(levelStage.getSortVal().replace(sortVal, changed.getSortVal()));
                    stagesMapper.updateById(levelStage);
                }
            }
            

            stages.setSortVal(changed.getSortVal());
            changed.setSortVal(sortVal);
            stagesMapper.updateById(changed);
            stagesMapper.updateById(stages);
        } else {
            if (filtered.get(filtered.size() - 1).getSortVal().equals(stages.getSortVal())) {
                return;
            }
            List<Stages> collect = filtered.stream().filter(x -> x.getSortVal().compareTo(stages.getSortVal()) > 0).collect(Collectors.toList());
            String sortVal = stages.getSortVal();
            Stages changed = collect.get(0);

            List<Stages> nextLevelStages = list.stream().filter(x -> x.getLevel() >= (level + 1)).collect(Collectors.toList());
            for (Stages levelStage : nextLevelStages) {
                if (levelStage.getSortVal().startsWith(changed.getSortVal())) {
                    levelStage.setSortVal(levelStage.getSortVal().replace(changed.getSortVal(), sortVal));
                    stagesMapper.updateById(levelStage);
                }

                if (levelStage.getSortVal().startsWith(sortVal)) {
                    levelStage.setSortVal(levelStage.getSortVal().replace(sortVal, changed.getSortVal()));
                    stagesMapper.updateById(levelStage);
                }
            }
            
            stages.setSortVal(changed.getSortVal());
            changed.setSortVal(sortVal);
            stagesMapper.updateById(changed);
            stagesMapper.updateById(stages);
        }
        firstBox.getChildren().clear();
        secondBox.getChildren().clear();
        thirdBox.getChildren().clear();
        showStageData();
    }
    
    private void deleteStage(Stages stages) {
        stagesMapper.deleteById(stages.getId());
        List<Stages> waitForDelete = new ArrayList<>();
        List<Stages> stagesList = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .eq(Stages::getParentId, stages.getId())
        );
        while (!stagesList.isEmpty()) {
            waitForDelete.addAll(stagesList);
            List<Integer> parentId = stagesList.stream().map(x -> x.getId()).collect(Collectors.toList());
            stagesList = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                    .in(Stages::getParentId, parentId)
            );
        }
        if (!waitForDelete.isEmpty()) {
            List<Integer> deleteIds = waitForDelete.stream().map(x -> x.getId()).collect(Collectors.toList());
            stagesMapper.deleteBatchIds(deleteIds);
        }
       
        firstBox.getChildren().clear();
        secondBox.getChildren().clear();
        thirdBox.getChildren().clear();
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
            scoreInput.setText(current.getScorePerQ() + "");
            timeDescInput.setText(current.getDescTime() + "");
            showNumberSelect.setSelected(current.getShowNumber() == 1);
            selectGreySelect.setSelected(current.getSelectGrey() == 1);
            randomSort.setSelected(current.getRandomSort() == 1);
            stageShow.setSelected(current.getShow() == 1);
            model6Check.setSelected(current.getM6() == 1);
            randomSort2.setSelected(current.getrRandomSort() == 1);
            paChoice.setValue(current.getpAType());
            ruleChoice.setValue(current.getRuleType());
            timeOrder.setSelected(current.getOrderVal() == 1);
            countTotal.setSelected(current.getCountTotal() == 1);
            answerShow.setSelected(current.getAnswerSingle() == 1);
            desValue.setText(current.getDesValue().toString());
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
//            if (current.getRuleType().equals("文字")){
//                ruleText.setVisible(true);
//                ruleImg.setVisible(false);
//                ruleText.setText(current.getRuleContent());
//                previewImg.setVisible(false);
//            } else {
//                
//                if (StringUtils.isEmpty(current.getRuleContent())) {
//                    ruleText.setVisible(false);
//                    ruleImg.setVisible(true);
//                    previewImg.setVisible(false);
//                } else {
//                    ruleText.setVisible(false);
//                    ruleImg.setVisible(false);
//                    previewImg.setVisible(true);
//                    try {
//                        previewImg.setImage(new Image(new FileInputStream(current.getRuleContent())));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//              
//            }
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
        current.setQuestionType(Integer.parseInt(ccData.toString()));
        current.setpAType(paTypeValue);
        current.setM6(model6Check.isSelected()? 1: 0);
        current.setRuleType(value);
        current.setRuleContent(data);
        current.setrRandomSort(randomSort2Selected?1:0);
        current.setOrderVal(timeOrder.isSelected()? 1: 0);
        current.setDesValue(Integer.parseInt(desValue.getText()));
        current.setCountTotal(countTotal.isSelected()? 1: 0);
        current.setAnswerSingle(answerShow.isSelected()?1: 0);
        stagesMapper.updateById(current);
        
        firstBox.getChildren().clear();
        secondBox.getChildren().clear();
        thirdBox.getChildren().clear();
        showStageData();
        controlPane.getPreviewPane().reload();
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
        stages.setM6(model6Check.isSelected()? 1: 0);
        stages.setrRandomSort(randomSort2Selected?1:0);
        stages.setDesValue(Integer.parseInt(desValue.getText()));
        stages.setCountTotal(countTotal.isSelected()? 1: 0);
        stages.setAnswerSingle(answerShow.isSelected()?1: 0);
        stages.setOrderVal(timeOrder.isSelected()? 1: 0);
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
        
        stagesMapper.insert(stages);
        firstBox.getChildren().clear();
        secondBox.getChildren().clear();
        thirdBox.getChildren().clear();
        showStageData();
    }
}
