package competition.controller.preview;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.*;
import competition.entity.Model3Style;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.Model3Mapper;
import competition.mapper.QuestionsMapper;
import competition.mapper.StagesMapper;
import competition.util.*;
import competition.ws.WsMessageHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

// 风险题
@Component
@Scope("prototype")
public class Model3Preview extends PreviewPane{
    
    @FXML
    private AnchorPane container;
    
    @FXML
    private VBox titleBox;
    
    @FXML
    private VBox questionBox;
    
    @FXML
    private StackPane headPane;
    
    @Resource
    private Model3Mapper mapper;
    
    @Resource
    private QuestionsMapper questionsMapper;
    
    @Resource
    private StagesMapper stagesMapper;
    
    @Resource
    private ControlPane controlPane;
    
    private Integer parentStageId;
    
    private static Integer passedId;
    
    private List<Stages> children;
    
    
    @Resource
    private WsMessageHandler messageHandler;

    public static void setPassedId(Integer passedId) {
        Model3Preview.passedId = passedId;
    }

    @Override
    public Node getPane() {
        return container;
    }
    
    @FXML
    public void initialize(){
        BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
        if (scoreBoardPreview != null) {
            scoreBoardPreview.getSelected().clear();
        }
        PreviewStack.addPreview(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    Navigator.navigatorTo("/view/match/Model3Attr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        
        // id 内部化
        this.parentStageId = passedId;
        
        
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.风险题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        }
        this.show();
    }

    @Override
    public void reload() {
        this.show();
    }
    
    // 默认位置
    public static int DEFAULT_X = 100;

    public static int DEFAULT_Y = 100;

    public static int DEFAULT_W = 100;

    public static int DEFAULT_H = 50;
    
    // 默认head位置
    public static int HEAD_X = 400;
    
    public static int HEAD_Y = 100;
    
    public static int HEAD_W = 600;
    
    public static int HEAD_H = 100;
    
    private void show(){

        // 外屏展示
        if (ExtendedScreen.hasShow()) {
            Model3.setPassedId(this.parentStageId);
            Object res = ExtendedScreen.showWithControl("/view/match/Model3.fxml");
            if (res != null) {
                controlPane.setQuestionPreview((Model3) res);
            }
        }
        
        children = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .eq(Stages::getParentId, parentStageId)
        );

        List<List<Questions>> qs = new ArrayList<>();
        for (Stages child : children) {
            //List<Questions> stageQuestions = RuntimeQuestionStock.getStageQuestions(child.getSortVal());
            List<Questions> stageQuestions = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getStageNumber, child.getSortVal())
            );
            for (int i = 0; i < stageQuestions.size(); i++) {
                // 每个题目分值
                stageQuestions.get(i).setScore(child.getScorePerQ());
            }
            qs.add(stageQuestions);
        }


        Stages stages = stagesMapper.selectById(parentStageId);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("stages", children);
        dataMap.put("questions", qs);
        dataMap.put("pstage", stages);
        messageHandler.notifyPage("model3", dataMap);
        
        // 保存到当前的数据中
       // CurrentQuestions.setM3Questions(qs, parentStageId);
        
        // 清除旧的数据
        
        titleBox.getChildren().clear();
        questionBox.getChildren().clear();
        headPane.getChildren().clear();
        
        Model3Style model3Style = mapper.selectById(1);
        String titleStyle = model3Style.getTitleStyle();
        String qStyle = model3Style.getQStyle();
        String headStyle = model3Style.getHeadStyle();
        
        String [] headArr = {};
        if (!StringUtils.isEmpty(headStyle)) {
            headArr = headStyle.split(ConfigSpliter.SPLITER);
        } else {
            headArr = "750%%150%%400%%100%%false%%0x990000ff%%%%默认.ttf%%40%%false%%false%%false%%false%%居中对齐%%0%%0xffffffff".split(ConfigSpliter.SPLITER);
        }
        
        String [] titleArr = {};
        if (!StringUtils.isEmpty(titleStyle)) {
            titleArr = titleStyle.split(ConfigSpliter.SPLITER);
        } else {
            titleArr = "250%%300%%150%%150%%false%%0x990000ff%%%%默认.ttf%%32%%false%%false%%false%%false%%居中对齐%%30%%0xffffffff"
                    .split(ConfigSpliter.SPLITER);
        }

        String [] qArr = {};
        if (!StringUtils.isEmpty(qStyle)) {
            qArr = qStyle.split(ConfigSpliter.SPLITER);
        } else {
            qArr = "500%%300%%60%%60%%false%%0x990000ff%%%%默认.ttf%%36%%false%%false%%false%%false%%居中对齐%%30%%8%%40%%0xffffffff".split(ConfigSpliter.SPLITER);
        }

        Text head = new Text(stages.getName());
        headPane.getChildren().add(head);
        headPane.setBackground(new Background(new BackgroundFill(Color.YELLOW,  new CornerRadii(10), Insets.EMPTY)));
        headPane.setLayoutX(ScreenAdapter.calculateMainX(HEAD_X));
        headPane.setLayoutY(ScreenAdapter.calculateMainY(HEAD_Y));
        headPane.setPrefWidth(ScreenAdapter.calculateMainX(HEAD_W));
        headPane.setPrefHeight(ScreenAdapter.calculateMainY(HEAD_H));

        head.setFill(Color.BLACK);
        head.setLayoutX(ScreenAdapter.calculateMainX(HEAD_X));
        head.setLayoutY(ScreenAdapter.calculateMainY(HEAD_Y));
        head.prefWidth(ScreenAdapter.calculateMainX(HEAD_W));
        head.prefHeight(ScreenAdapter.calculateMainY(HEAD_H));
        
        // 显示标题
        if (headArr.length > 0) {
            headPane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(headArr[0])));
            headPane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(headArr[1])));
            headPane.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(headArr[2])));
            headPane.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(headArr[3])));
            headPane.setAlignment(AlignmentUtil.getAlign(headArr[13]));

            if (Boolean.valueOf(headArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(headArr[6])) {
                    try {
                        headPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(headArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(
                                        ScreenAdapter.calculateMainX(Integer.parseInt(headArr[2])),
                                        ScreenAdapter.calculateMainY(Integer.parseInt(headArr[3])), 
                                        false, false, false, false))));
                    } catch (FileNotFoundException e) {
                        headPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    headPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                }
            } else {
                // 填充背景颜色
                headPane.setBackground(new Background(new BackgroundFill(Color.valueOf(headArr[5]), new CornerRadii(10), Insets.EMPTY)));
            }

            head.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(headArr[0])));
            head.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(headArr[1])));
            head.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(headArr[2])));
            head.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(headArr[3])));
            head.setTextAlignment(AlignmentUtil.getTextAlign(headArr[13]));
            Font font = FontMap.load(headArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(headArr[8])));
            
            head.setFont(font);
            // 字体颜色
            head.setFill(Color.valueOf(headArr[15]));
            if (Boolean.valueOf(headArr[10])) {
                head.setStroke(head.getFill());
                head.setStrokeWidth(1);
            } else {
                head.setStroke(null);
            }
            head.getTransforms().clear();
            if (Boolean.valueOf(headArr[11])) {
                head.getTransforms().add(new Shear(-0.3, 0));
            }
            head.setUnderline(Boolean.valueOf(headArr[12]));
            head.setStrikethrough(Boolean.valueOf(headArr[9]));
        }
        
        for (int i = 0; i < children.size(); i++) {
            StackPane stackPane = new StackPane();
            Text title = new Text(children.get(i).getName());
            stackPane.getChildren().add(title);
            stackPane.setBackground(new Background(new BackgroundFill(Color.YELLOW,  new CornerRadii(10), Insets.EMPTY)));
            stackPane.setLayoutX(ScreenAdapter.calculateMainX(DEFAULT_X));
            stackPane.setLayoutY(ScreenAdapter.calculateMainY(DEFAULT_Y));
            stackPane.setPrefWidth(ScreenAdapter.calculateMainX(DEFAULT_W));
            stackPane.setPrefHeight(ScreenAdapter.calculateMainY(DEFAULT_H));
            titleBox.setLayoutX(ScreenAdapter.calculateMainX(DEFAULT_X));
            titleBox.setLayoutY(ScreenAdapter.calculateMainY(DEFAULT_Y));
            titleBox.setPrefWidth(ScreenAdapter.calculateMainX(DEFAULT_W));
            titleBox.setSpacing(0);

            title.setFill(Color.BLACK);
            title.setLayoutX(ScreenAdapter.calculateMainX(DEFAULT_X));
            title.setLayoutY(ScreenAdapter.calculateMainY(DEFAULT_Y));
            title.prefWidth(ScreenAdapter.calculateMainX(DEFAULT_W));
            title.prefHeight(ScreenAdapter.calculateMainY(DEFAULT_H));
            
            if(titleArr.length > 0) {
                //  String detail = x + ConfigSpliter.SPLITER
                //                    + y + ConfigSpliter.SPLITER
                //                    + width + ConfigSpliter.SPLITER
                //                    + height + ConfigSpliter.SPLITER
                //                    + fillBackground + ConfigSpliter.SPLITER
                //                    + backgroundColor + ConfigSpliter.SPLITER
                //                    + backgroundImage + ConfigSpliter.SPLITER
                //                    + fontFamily + ConfigSpliter.SPLITER
                //                    + fsize + ConfigSpliter.SPLITER
                //                    + delete + ConfigSpliter.SPLITER
                //                    + bold + ConfigSpliter.SPLITER
                //                    + itic + ConfigSpliter.SPLITER
                //                    + underlne + ConfigSpliter.SPLITER
                //                    + alignValue + ConfigSpliter.SPLITER
                //                    + (StringUtils.isEmpty(nameSpaceInput.getText())?"0":nameSpaceInput.getText());

                stackPane.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[0])) );
                stackPane.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[1])));
                stackPane.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[2])));
                stackPane.setPrefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[3])));
                stackPane.setAlignment(AlignmentUtil.getAlign(titleArr[13]));
                titleBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[0])));
                titleBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[1])));
                titleBox.setPrefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[2])));
                titleBox.setSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[14])));

                if (Boolean.valueOf(titleArr[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(titleArr[6])) {
                        try {
                            stackPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(titleArr[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                            ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[2])),
                                            ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[3])), 
                                            false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            stackPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        stackPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(titleArr[5]), new CornerRadii(10), Insets.EMPTY)));
                }

                title.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[0])) );
                title.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[1])));
                title.prefWidth(ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[2])));
                title.prefHeight(ScreenAdapter.calculateMainY(Integer.parseInt(titleArr[3])));
               
                title.setTextAlignment(AlignmentUtil.getTextAlign(titleArr[13]));
                Font font = FontMap.load(titleArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(titleArr[8])));
                title.setFont(font);
                // 字体颜色
                title.setFill(Color.valueOf(titleArr[15]));
                title.getTransforms().clear();
                if (Boolean.valueOf(titleArr[10])) {
                    title.setStroke(Color.valueOf(titleArr[15]));
                    title.setStrokeWidth(1);
                } else {
                    title.setStroke(null);
                }
                if (Boolean.valueOf(titleArr[11])) {
                    title.getTransforms().add(new Shear(-0.3, 0));
                }
                title.setUnderline(Boolean.valueOf(titleArr[12]));
                title.setStrikethrough(Boolean.valueOf(titleArr[9]));
            }
            titleBox.getChildren().add(stackPane);
        }
        
        for (int i = 0; i < children.size(); i++) {
            Stages stages1 = children.get(i);
            List<Questions> questionsList = questionsMapper.selectList(new LambdaQueryWrapper<Questions>()
                    .eq(Questions::getStageNumber, stages1.getSortVal())
            );
            if (stages1.getRandomSort() == 1) {
                for (Questions questions : questionsList) {
                    questions.setOrder(questions.getViewSort());
                }
            } else {
                for (Questions questions : questionsList) {
                    questions.setOrder(questions.getQuestionNumber());
                }
            }
            questionsList.sort(Comparator.comparingInt(Questions::getOrder));
            GridPane questionGrid = new GridPane();
            for (int i1 = 0; i1 < questionsList.size(); i1++) {
                StackPane item = new StackPane();
                item.setPrefSize(50, 50);
                Questions questions = questionsList.get(i1);
                Text text = new Text(questions.getOrder().toString());
                if (questions.isHasAnswer()) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                }
                text.setFill(Color.BLACK);
                item.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), Insets.EMPTY)));
                // 默认值
                questionGrid.setLayoutX(ScreenAdapter.calculateMainX(DEFAULT_X + DEFAULT_W + 50));
                questionGrid.setLayoutY(ScreenAdapter.calculateMainY(DEFAULT_Y));
                questionGrid.setHgap(ScreenAdapter.calculateMainY(10));
                questionGrid.setVgap(0);

                questionBox.setLayoutX(ScreenAdapter.calculateMainX(DEFAULT_X + DEFAULT_W + 50));
                questionBox.setLayoutY(ScreenAdapter.calculateMainY(DEFAULT_Y));
                questionBox.setSpacing(0);

                int cols = 6;
                if (qArr.length > 0) {
                    String[] detailArr = qArr;

                    questionBox.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[0])));
                    questionBox.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[1])));
                    
                    item.setPrefSize(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])),  ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])));
                    item.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                    item.setLayoutX(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[0])));
                    item.setLayoutY(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[1])));
                    questionGrid.setVgap(ScreenAdapter.calculateMainY(10));
                    questionGrid.setHgap(ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[16])));
                    questionBox.setSpacing(ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[14])));
                    if (Boolean.valueOf(detailArr[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(detailArr[6])) {
                            try {
                                item.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(
                                                ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[2])),
                                                ScreenAdapter.calculateMainY(Integer.parseInt(detailArr[3])), 
                                                false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                item.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                            }
                        } else {
                            item.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        // 填充背景颜色
                        item.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), new CornerRadii(10), Insets.EMPTY)));
                    }


                    Font font = FontMap.load(detailArr[7], ScreenAdapter.calculateMainX(Integer.parseInt(detailArr[8])));
                    text.setFont(font);
                    text.getTransforms().clear();
                    if (Boolean.valueOf(detailArr[11])) {
                        text.getTransforms().add(new Shear(-0.3, 0));
                    }
                    text.setFill(Color.valueOf(detailArr[17]));
                    if (Boolean.valueOf(detailArr[10])) {
                        text.setStroke(text.getFill());
                        text.setStrokeWidth(1);
                    } else {
                        text.setStroke(null);

                    }
                    text.setUnderline(Boolean.valueOf(detailArr[12]));
                    text.setStrikethrough(Boolean.valueOf(detailArr[9]));
                    cols = Integer.parseInt(detailArr[15]);
                }

                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() != MouseButton.PRIMARY){
                            return;
                        }
                        // 展示对应的分值和时间
                        String stageNumber = questions.getStageNumber();
                        Stages belongStage = stagesMapper.selectOne(new LambdaQueryWrapper<Stages>()
                            .eq(Stages::getSortVal, stageNumber)
                        );
                        // 进入题目详情
                        questions.setHasAnswer(true);
                        questions.setShowNumber(belongStage.getShowNumber());
//                        Questions questions = CurrentQuestions.getQ(tihao);
//                        CurrentQuestions.recordAnswer(tihao);
                        // 设置当前需要显示的题目
                        // 内屏更新
                        questionsMapper.updateById(questions);
                        QuestionPreviewPreview.setCurrent(questions);
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true)
                        );
                        

                        controlPane.configTimeAndScore(belongStage, questions.getOrder().toString());
                        MusicPlayer.play(MusicResource.SHOW_QUESTION);
                        // 控制面板选择题号
                        //controlPane.selectQuestion(tihao);
                        
                    }
                });
                
                item.getChildren().add(text);
                // 添加到对应的位置
                questionGrid.add(item, i1 % cols , i1 / cols );
            }
            questionBox.getChildren().add(questionGrid);
        }
        
    }

    @Override
    public void reloadStyle() {
        this.reload();
    }

    public Integer getParentStageId() {
        return parentStageId;
    }
}
