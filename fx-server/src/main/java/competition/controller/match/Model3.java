package competition.controller.match;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.controller.preview.QuestionPreviewPreview;
import competition.entity.Model3Style;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.mapper.Model3Mapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 风险题
@Component
@Scope("prototype")
public class Model3 implements ReloadPane {
    
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
    private StagesMapper stagesMapper;
    
    private Integer parentStageId;
    
    private static Integer passedId;
    
    private List<Stages> children;


    public static void setPassedId(Integer passedId) {
        Model3.passedId = passedId;
    }

    @Override
    public Node getPane() {
        return container;
    }
    
    @FXML
    public void initialize(){
        // id 内部化
        this.parentStageId = passedId;
        
        
        try {
            container.setBackground(new Background(new BackgroundImage(
                    new Image(new FileInputStream(ImageResource.选题背景())), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    new BackgroundSize(ExtendedScreen.getWidth(), ExtendedScreen.getHeight(), false, false, false, false)
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


        Stages stages = stagesMapper.selectById(parentStageId);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("stages", children);
        dataMap.put("questions", qs);
        dataMap.put("pstage", stages);

        // 保存到当前的数据中
        CurrentQuestions.setM3Questions(qs, parentStageId);
        
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
        }
        
        String [] titleArr = {};
        if (!StringUtils.isEmpty(titleStyle)) {
            titleArr = titleStyle.split(ConfigSpliter.SPLITER);
        }

        String [] qArr = {};
        if (!StringUtils.isEmpty(qStyle)) {
            qArr = qStyle.split(ConfigSpliter.SPLITER);
        }

        Text head = new Text(stages.getName());
        headPane.getChildren().add(head);
        headPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), Insets.EMPTY)));
        headPane.setLayoutX(ScreenAdapter.calculateExX(HEAD_X));
        headPane.setLayoutY(ScreenAdapter.calculateExY(HEAD_Y));
        headPane.setPrefWidth(ScreenAdapter.calculateExW(HEAD_W));
        headPane.setPrefHeight(ScreenAdapter.calculateExH(HEAD_H));

        head.setFill(Color.BLACK);
        head.setLayoutX(ScreenAdapter.calculateExX(HEAD_X));
        head.setLayoutY(ScreenAdapter.calculateExY(HEAD_Y));
        head.prefWidth(ScreenAdapter.calculateExW(HEAD_W));
        head.prefHeight(ScreenAdapter.calculateExH(HEAD_H));
        
        // 显示标题
        if (headArr.length > 0) {
            headPane.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(headArr[0])));
            headPane.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(headArr[1])));
            headPane.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(headArr[2])));
            headPane.setPrefHeight(ScreenAdapter.calculateExH(Integer.parseInt(headArr[3])));
            headPane.setAlignment(AlignmentUtil.getAlign(headArr[13]));

            if (Boolean.valueOf(headArr[4])) {
                // 填充背景图片
                if (!StringUtils.isEmpty(headArr[6])) {
                    try {
                        headPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(headArr[6])),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(
                                    ScreenAdapter.calculateExW(Integer.parseInt(headArr[2])),
                                    ScreenAdapter.calculateExH(Integer.parseInt(headArr[3])), false, false, false, false))));
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

            head.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(headArr[0])));
            head.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(headArr[1])));
            head.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(headArr[2])));
            head.prefHeight(ScreenAdapter.calculateExH(Integer.parseInt(headArr[3])));
            head.setTextAlignment(AlignmentUtil.getTextAlign(headArr[13]));
            Font font = Font.font(FontMap.getFont(headArr[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                ScreenAdapter.calculateExW(Integer.parseInt(headArr[8]))
            );
            head.setFont(font);
            // 字体颜色
            head.setFill(Color.valueOf(headArr[15]));
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
            stackPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), Insets.EMPTY)));
            stackPane.setLayoutX(ScreenAdapter.calculateExX(DEFAULT_X));
            stackPane.setLayoutY(ScreenAdapter.calculateExY(DEFAULT_Y));
            stackPane.setPrefWidth(ScreenAdapter.calculateExW(DEFAULT_W));
            stackPane.setPrefHeight(ScreenAdapter.calculateExH(DEFAULT_H));
            titleBox.setLayoutX(ScreenAdapter.calculateExX(DEFAULT_X));
            titleBox.setLayoutY(ScreenAdapter.calculateExY(DEFAULT_Y));
            titleBox.setPrefWidth(ScreenAdapter.calculateExW(DEFAULT_W));
            titleBox.setSpacing(0);

            title.setFill(Color.BLACK);
            title.setLayoutX(ScreenAdapter.calculateExX(DEFAULT_X));
            title.setLayoutY(ScreenAdapter.calculateExY(DEFAULT_Y));
            title.prefWidth(ScreenAdapter.calculateExW(DEFAULT_W));
            title.prefHeight(ScreenAdapter.calculateExH(DEFAULT_H));
            
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

                stackPane.setLayoutX( ScreenAdapter.calculateExX(Integer.parseInt(titleArr[0])));
                stackPane.setLayoutY( ScreenAdapter.calculateExY(Integer.parseInt(titleArr[1])));
                stackPane.setPrefWidth( ScreenAdapter.calculateExW(Integer.parseInt(titleArr[2])));
                stackPane.setPrefHeight( ScreenAdapter.calculateExH(Integer.parseInt(titleArr[3])));
                stackPane.setAlignment(AlignmentUtil.getAlign(titleArr[13]));
                titleBox.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(titleArr[0])));
                titleBox.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(titleArr[1])));
                titleBox.setPrefWidth(ScreenAdapter.calculateExW(Integer.parseInt(titleArr[2])));
                titleBox.setSpacing(ScreenAdapter.calculateExH(Integer.parseInt(titleArr[14])));

                if (Boolean.valueOf(titleArr[4])) {
                    // 填充背景图片
                    if (!StringUtils.isEmpty(titleArr[6])) {
                        try {
                            stackPane.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(titleArr[6])),
                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                    new BackgroundSize(
                                        ScreenAdapter.calculateExW(Integer.parseInt(titleArr[2])),
                                        ScreenAdapter.calculateExH(Integer.parseInt(titleArr[3])),
                                        false, false, false, false))));
                        } catch (FileNotFoundException e) {
                            stackPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                        }
                    } else {
                        stackPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
                    }
                } else {
                    // 填充背景颜色
                    stackPane.setBackground(new Background(new BackgroundFill(Color.valueOf(titleArr[5]),new CornerRadii(10), Insets.EMPTY)));
                }

                title.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(titleArr[0])));
                title.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(titleArr[1])));
                title.prefWidth(ScreenAdapter.calculateExW(Integer.parseInt(titleArr[2])));
                title.prefHeight(ScreenAdapter.calculateExH(Integer.parseInt(titleArr[3])));
                title.setTextAlignment(AlignmentUtil.getTextAlign(titleArr[13]));
                Font font = Font.font(FontMap.getFont(titleArr[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                    ScreenAdapter.calculateExW(Integer.parseInt(titleArr[8]))
                );
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
            GridPane questionGrid = new GridPane();
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
                item.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), Insets.EMPTY)));
                // 默认值
                questionGrid.setLayoutX(ScreenAdapter.calculateExX(DEFAULT_X + DEFAULT_W + 50));
                questionGrid.setLayoutY(ScreenAdapter.calculateExY(DEFAULT_Y));
                questionGrid.setHgap(ScreenAdapter.calculateExW(10));
                questionGrid.setVgap(0);

                questionBox.setLayoutX(ScreenAdapter.calculateExX(DEFAULT_X + DEFAULT_W + 50));
                questionBox.setLayoutY(ScreenAdapter.calculateExY(DEFAULT_Y));
                questionBox.setSpacing(0);

                int cols = 6;
                if (qArr.length > 0) {
                    String[] detailArr = qArr;

                    questionBox.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                    questionBox.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
                    item.setPrefSize(ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])),
                        ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])));
                    item.setAlignment(AlignmentUtil.getAlign(detailArr[13]));
                    item.setLayoutX(ScreenAdapter.calculateExX(Integer.parseInt(detailArr[0])));
                    item.setLayoutY(ScreenAdapter.calculateExY(Integer.parseInt(detailArr[1])));
                    questionGrid.setVgap(ScreenAdapter.calculateExH(Integer.parseInt(detailArr[14])));
                    questionGrid.setHgap(ScreenAdapter.calculateExW(Integer.parseInt(detailArr[16])));
                    questionBox.setSpacing(ScreenAdapter.calculateExH(Integer.parseInt(detailArr[14])));
                    if (Boolean.valueOf(detailArr[4])) {
                        // 填充背景图片
                        if (!StringUtils.isEmpty(detailArr[6])) {
                            try {
                                item.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream(detailArr[6])),
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        new BackgroundSize(
                                            ScreenAdapter.calculateExW(Integer.parseInt(detailArr[2])),
                                            ScreenAdapter.calculateExH(Integer.parseInt(detailArr[3])),
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


                    Font font = Font.font(FontMap.getFont(detailArr[7]), FontWeight.NORMAL, FontPosture.REGULAR,
                        ScreenAdapter.calculateExW(Integer.parseInt(detailArr[8])));
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
                    cols = Integer.parseInt(detailArr[15]);
                }
                
                item.getChildren().add(text);
                // 添加到对应的位置
                questionGrid.add(item, i1 % cols , i1 / cols );
            }
            questionBox.getChildren().add(questionGrid);
        }
        
    }

    public Integer getParentStageId() {
        return parentStageId;
    }
}
