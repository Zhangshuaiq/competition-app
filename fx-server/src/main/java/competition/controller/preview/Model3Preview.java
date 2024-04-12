package competition.controller.preview;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.match.*;
import competition.entity.Model3Style;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.Model3Mapper;
import competition.mapper.StagesMapper;
import competition.util.*;
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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Shear;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

// 风险题
@Component
@Scope("prototype")
public class Model3Preview extends PreviewPane{
    
    @FXML
    private AnchorPane container;
    
    @FXML
    private VBox titleBox;
    
    @FXML
    private GridPane questionGrid;
    
    @Resource
    private Model3Mapper mapper;
    
    @Resource
    private StagesMapper stagesMapper;
    
    @Resource
    private ControlPane controlPane;
    
    private Integer parentStageId;
    
    private static Integer passedId;
    
    private List<Stages> children;
    

    public static void setPassedId(Integer passedId) {
        Model3Preview.passedId = passedId;
    }

    @Override
    public Node getPane() {
        return container;
    }
    
    @FXML
    public void initialize(){
        PreviewStack.addPreview(this);
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    Model3Attr.setReloadPane(controlPane.getQuestionPreview());
                    Navigator.navigatorTo("/view/match/Model3Attr.fxml", "", "/view/style.css", false, false);
                }
            }
        });
        
        // id 内部化
        this.parentStageId = passedId;
        children = stagesMapper.selectList(new LambdaQueryWrapper<Stages>()
                .eq(Stages::getParentId, parentStageId)
        );

        List<List<Questions>> qs = new ArrayList<>();
        for (Stages child : children) {
            List<Questions> stageQuestions = RuntimeQuestionStock.getStageQuestions(child.getSortVal());
            for (int i = 0; i < stageQuestions.size(); i++) {
                stageQuestions.get(i).setQuestionNumber(i + 1);
                // 每个题目分值
                stageQuestions.get(i).setScore(child.getScorePerQ());
            }
            qs.add(stageQuestions);
        }
        // 保存到当前的数据中
        CurrentQuestions.setM3Questions(qs, parentStageId);
        
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.选题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(Navigator.getWidth(), Navigator.getHeight(), false, false, false, false)
            )));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    
    
    private void show(){

        // 清除旧的数据
        titleBox.getChildren().clear();
        questionGrid.getChildren().clear();
        
        Model3Style model3Style = mapper.selectById(1);
        String titleStyle = model3Style.getTitleStyle();
        String qStyle = model3Style.getQStyle();
        
        
        String [] titleArr = {};
        if (!StringUtils.isEmpty(titleStyle)) {
            titleArr = titleStyle.split(ConfigSpliter.SPLITER);
        }

        String [] qArr = {};
        if (!StringUtils.isEmpty(qStyle)) {
            qArr = qStyle.split(ConfigSpliter.SPLITER);
        }
        
        for (int i = 0; i < children.size(); i++) {
            StackPane stackPane = new StackPane();
            Text title = new Text(children.get(i).getName());
            stackPane.getChildren().add(title);
            stackPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
            stackPane.setLayoutX(DEFAULT_X);
            stackPane.setLayoutY(DEFAULT_Y);
            stackPane.setPrefWidth(DEFAULT_W);
            stackPane.setPrefHeight(DEFAULT_H);
            titleBox.setLayoutX(DEFAULT_X);
            titleBox.setLayoutY(DEFAULT_Y);
            titleBox.setPrefWidth(DEFAULT_W);
            titleBox.setSpacing(0);

            title.setFill(Color.BLACK);
            title.setLayoutX(DEFAULT_X);
            title.setLayoutY(DEFAULT_Y);
            title.prefWidth(DEFAULT_W);
            title.prefHeight(DEFAULT_H);
            
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

                stackPane.setLayoutX(Integer.parseInt(titleArr[0]));
                stackPane.setLayoutY(Integer.parseInt(titleArr[1]));
                stackPane.setPrefWidth(Integer.parseInt(titleArr[2]));
                stackPane.setPrefHeight(Integer.parseInt(titleArr[3]));
                stackPane.setAlignment(AlignmentUtil.getAlign(titleArr[13]));
                titleBox.setLayoutX(Integer.parseInt(titleArr[0]));
                titleBox.setLayoutY(Integer.parseInt(titleArr[1]));
                titleBox.setPrefWidth(Integer.parseInt(titleArr[2]));
                titleBox.setSpacing(Integer.parseInt(titleArr[14]));

                if (Boolean.valueOf(titleArr[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(titleArr[6])) {
                        try {
                            stackPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(titleArr[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(Integer.parseInt(titleArr[2]), Integer.parseInt(titleArr[3]), false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 填充背景颜色
                    stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(titleArr[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                }

                title.setLayoutX(Integer.parseInt(titleArr[0]));
                title.setLayoutY(Integer.parseInt(titleArr[1]));
                title.prefWidth(Integer.parseInt(titleArr[2]));
                title.prefHeight(Integer.parseInt(titleArr[3]));
                title.setTextAlignment(AlignmentUtil.getTextAlign(titleArr[13]));
                Font font = Font.font(FontMap.getFont(titleArr[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(titleArr[8]));
                title.setFont(font);
                // 字体颜色
                title.setFill(Color.valueOf(titleArr[15]));
                title.getTransforms().clear();
                if (Boolean.valueOf(titleArr[11])) {
                    title.getTransforms().add(new Shear(-0.3, 0));
                }
                title.setUnderline(Boolean.valueOf(titleArr[12]));
                title.setStrikethrough(Boolean.valueOf(titleArr[9]));
            }
            titleBox.getChildren().add(stackPane);

        }
        
        for (int i = 0; i < CurrentQuestions.getM3Questions().size(); i++) {
            List<Questions> questionsList = CurrentQuestions.getM3Questions().get(i);
            for (int i1 = 0; i1 < questionsList.size(); i1++) {
                StackPane item = new StackPane();
                item.setPrefSize(50, 50);
                Questions questions = questionsList.get(i1);
                Text text = new Text(questions.getQuestionNumber().toString());
                if (CurrentQuestions.hasAnswer(questions.getStageNumber() + "-" + questions.getQuestionNumber())) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                }
                text.setFill(Color.BLACK);
                item.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                // 默认值
                questionGrid.setLayoutX(DEFAULT_X + DEFAULT_W + 50);
                questionGrid.setLayoutY(DEFAULT_Y);
                questionGrid.setHgap(10);
                questionGrid.setVgap(0);
                
                if (qArr.length > 0) {
                    String[] detailArr = qArr;

                    questionGrid.setLayoutX(Integer.parseInt(detailArr[0]));
                    questionGrid.setLayoutY(Integer.parseInt(detailArr[1]));
                    item.setPrefSize(Integer.parseInt(detailArr[2]), Integer.parseInt(detailArr[3]));
                    item.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                    item.setLayoutX(Integer.parseInt(detailArr[0]));
                    item.setLayoutY(Integer.parseInt(detailArr[1]));
                    questionGrid.setVgap(Integer.parseInt(detailArr[14]));
                    questionGrid.setHgap(Integer.parseInt(detailArr[16]));
                    if (Boolean.valueOf(detailArr[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(detailArr[6])) {
                            try {
                                item.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(Integer.parseInt(detailArr[2]), Integer.parseInt(detailArr[3]), false, false, false, false))));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // 填充背景颜色
                        item.setBackground(new Background(new BackgroundFill(Color.valueOf(detailArr[5]), CornerRadii.EMPTY, Insets.EMPTY)));
                    }


                    Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR, Integer.parseInt(detailArr[8]));
                    text.setFont(font);
                    if (Boolean.valueOf(detailArr[10])) {
                        text.setStroke(text.getFill());
                    } else {
                        text.setStroke(null);

                    }
                    text.getTransforms().clear();
                    if (Boolean.valueOf(detailArr[11])) {
                        text.getTransforms().add(new Shear(-0.3, 0));
                    }
                    text.setFill(Color.valueOf(detailArr[17]));
                    text.setUnderline(Boolean.valueOf(detailArr[12]));
                    text.setStrikethrough(Boolean.valueOf(detailArr[9]));
                }

                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() != MouseButton.PRIMARY){
                            return;
                        }
                        
                        // 进入题目详情
                        String tihao = questions.getStageNumber() + "-" +questions.getQuestionNumber();
                        Questions questions = CurrentQuestions.getQ(tihao);
                        CurrentQuestions.recordAnswer(tihao);
                        // 设置当前需要显示的题目
                        // 内屏更新
                        QuestionPreviewPreview.setCurrent(questions);
                        controlPane.setPreviewPane(
                                Navigator.preview("/view/preview/QuestionPreview.fxml", "", "/view/style.css", true, true)
                        );
                        // 控制面板选择题号
                        //controlPane.selectQuestion(tihao);
                        
                    }
                });
                
                item.getChildren().add(text);
                // 添加到对应的位置
                questionGrid.add(item, i1 , i );
            }
        }
        
    }

    public Integer getParentStageId() {
        return parentStageId;
    }
}
