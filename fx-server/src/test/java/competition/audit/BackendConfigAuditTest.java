package competition.audit;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import competition.controller.ControlPane;
import competition.controller.backend.*;
import competition.controller.preview.StagePreviewPreview;
import competition.mapper.*;
import competition.util.Navigator;
import competition.ws.WsMessageHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Opt-in desktop backend audit. Only a disposable SQLite snapshot/resources are changed. */
@EnabledIfSystemProperty(named="backend.audit", matches="true")
class BackendConfigAuditTest {
    static final Path ROOT = Paths.get(System.getProperty("backend.audit.root", "/private/tmp/fx-backend-audit"));
    SqlSessionTemplate session;
    @BeforeAll static void toolkit() throws Exception {
        assertTrue(Files.exists(ROOT.resolve("baseline.db")), "Prepare a SQLite backup first");
        System.setProperty("user.dir", ROOT.toString());
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {Platform.setImplicitExit(false); ready.countDown();});
        assertTrue(ready.await(15, TimeUnit.SECONDS));
    }
    @BeforeEach void database() throws Exception {
        Path db = Files.createTempFile(ROOT, "case-", ".db");
        Files.copy(ROOT.resolve("baseline.db"), db, StandardCopyOption.REPLACE_EXISTING);
        MybatisConfiguration config = new MybatisConfiguration();
        config.addMappers("competition.mapper");
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(new DriverManagerDataSource("jdbc:sqlite:" + db));
        factory.setConfiguration(config);
        session = new SqlSessionTemplate(factory.getObject());
    }
    <T> T controller(Class<T> type) {
        try {
            T bean = type.getDeclaredConstructor().newInstance();
            for (Class<?> cls=type; cls!=null; cls=cls.getSuperclass()) for (Field f:cls.getDeclaredFields()) {
                if (!f.isAnnotationPresent(Resource.class) && !f.isAnnotationPresent(org.springframework.beans.factory.annotation.Autowired.class)) continue;
                Object value = f.getType().getPackageName().equals("competition.mapper") ? session.getMapper(f.getType()) : mock(f.getType());
                f.setAccessible(true); f.set(bean, value);
            }
            return bean;
        } catch (Exception e) {throw new RuntimeException(e);}
    }
    <T> T load(Class<T> type, String path) throws Exception {
        T bean = controller(type);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setControllerFactory(ignored -> bean);
        loader.load();
        return bean;
    }
    void fx(Callable<Void> work) throws Exception {
        FutureTask<Void> task = new FutureTask<>(work); Platform.runLater(task);
        try {task.get(40,TimeUnit.SECONDS);} catch (ExecutionException e) {
            Throwable c=e.getCause(); if(c instanceof Exception) throw (Exception)c; if(c instanceof Error) throw (Error)c; throw e;
        }
    }
    TextField text(Object c, String field) {return (TextField)ReflectionTestUtils.getField(c,field);}
    @Test void loadAllNineNonHardwareBackendScreens() throws Exception {fx(() -> {
        for(Class<?> type : new Class<?>[]{StageConfig.class,PageConfig.class,TeamConfig.class,BuzzConfig.class,ScoreConfig.class,QuestionStock.class,GradeMng.class,WebPageConfig.class,PaneConfig.class}) {
            load(type,"/view/backend/"+type.getSimpleName()+".fxml");
            System.out.println("AUDIT LOAD PASS "+type.getSimpleName());
        } return null;
    });}
    StagePreviewPreview matchPreview() throws Exception {
        StagePreviewPreview p=load(StagePreviewPreview.class,"/view/preview/StagePreview.fxml");
        p.initPage(); return p;
    }
    @Test void normalWebSaveOnlyTouches15AndMatchStageStillLoads() throws Exception {fx(() -> {
        ConfigsMapper mapper=session.getMapper(ConfigsMapper.class);
        String before=mapper.selectById(1).getConfig(); String stage=mapper.selectById(2).getConfig();
        matchPreview();
        WebPageConfig web=load(WebPageConfig.class,"/view/backend/WebPageConfig.fxml");
        text(web,"model6PageRow").setText("6"); web.saveConfig();
        assertEquals(before,mapper.selectById(1).getConfig()); assertEquals(stage,mapper.selectById(2).getConfig());
        assertTrue(mapper.selectById(15).getConfig().contains("\"row\":\"6\""));
        matchPreview(); return null;
    });}
    @Test void webNumericGarbageIsPersisted() throws Exception {fx(() -> {
        WebPageConfig web=load(WebPageConfig.class,"/view/backend/WebPageConfig.fxml");
        text(web,"model6PageRow").setText("abc");text(web,"model7PageCol").setText("0"); web.saveConfig();
        String value=session.getMapper(ConfigsMapper.class).selectById(15).getConfig();
        assertTrue(value.contains("\"row\":\"abc\"")); assertTrue(value.contains("\"col\":\"0\"")); return null;
    });}
    @Test void pageSaveCommitsInvalidCoordinateBeforeRefreshFails() throws Exception {fx(() -> {
        PageConfig p=load(PageConfig.class,"/view/backend/PageConfig.fxml");
        for (TextField field:p.getAllTextFields()) { if(field.getText().isEmpty()) {System.out.println("AUDIT EMPTY PAGE FIELD "+field.getId()); field.setText("1");}}
        text(p,"dwx").setText("999999999999999");
        NumberFormatException error=assertThrows(NumberFormatException.class,p::updateConfig);
        assertEquals("999999999999999",session.getMapper(ConfigsMapper.class).selectById(1).getConfig().split("%%")[17]);
        System.out.println("AUDIT PAGE SAVE "+error); return null;
    });}
    @Test void teamNonNumericCodeCommitsBeforeSortFailure() throws Exception {fx(() -> {
        TeamConfig c=load(TeamConfig.class,"/view/backend/TeamConfig.fxml");
        javafx.scene.control.TableView<?> table=(javafx.scene.control.TableView<?>)ReflectionTestUtils.getField(c,"tableView");
        table.getSelectionModel().select(0); c.clickTable();
        Object selected=table.getSelectionModel().getSelectedItem(); Integer id=(Integer)ReflectionTestUtils.getField(selected,"id");
        text(c,"codeInput").setText("abc");
        assertThrows(NumberFormatException.class,c::updateTeamInfo);
        assertEquals("abc",session.getMapper(TeamsMapper.class).selectById(id).getCode()); return null;
    });}
    @Test void enablingShowStageListSavesSuccessfullyButBlanksMatch() throws Exception {fx(() -> {
        StagePreviewPreview before=matchPreview();
        assertFalse(((javafx.scene.layout.Pane)before.getPane()).getChildren().isEmpty());
        PageConfig p=load(PageConfig.class,"/view/backend/PageConfig.fxml");
        ((javafx.scene.control.CheckBox)ReflectionTestUtils.getField(p,"xshjlb")).setSelected(true);
        p.updateConfig();
        assertEquals("true",session.getMapper(ConfigsMapper.class).selectById(2).getConfig().split("%%")[9]);
        StagePreviewPreview after=matchPreview();
        assertTrue(((javafx.scene.layout.Pane)after.getPane()).getChildren().isEmpty());
        System.out.println("AUDIT CONFIRMED show-stage-list=true produces empty match pane"); return null;
    });}
    @Test void stageCoordinateOverflowSavesButBreaksMatchLoad() throws Exception {fx(() -> {
        PageConfig p=load(PageConfig.class,"/view/backend/PageConfig.fxml");
        text(p,"hjx").setText("999999999999999");
        p.updateConfig();
        assertEquals("999999999999999",session.getMapper(ConfigsMapper.class).selectById(2).getConfig().split("%%")[4]);
        assertThrows(NumberFormatException.class,this::matchPreview);return null;
    });}
    @Test void pageDefaultSaveReportsEmptyFields() throws Exception {fx(() -> {
        PageConfig p=load(PageConfig.class,"/view/backend/PageConfig.fxml");
        assertFalse(p.getAllTextFields().isEmpty());
        assertTrue(p.getAllTextFields().stream().noneMatch(field -> field.getText().isEmpty()));
        return null;
    });}
    @Test void buzzDefaultSaveRoundtrip() throws Exception {fx(() -> {
        load(BuzzConfig.class,"/view/backend/BuzzConfig.fxml").updateConfig();
        load(BuzzConfig.class,"/view/backend/BuzzConfig.fxml"); return null;
    });}
    @Test void stageDefaultUpdateWithoutSelection() throws Exception {fx(() -> {
        java.util.List<competition.entity.Stages> before=session.getMapper(StagesMapper.class).selectList(null);
        load(StageConfig.class,"/view/backend/StageConfig.fxml").updateStage();
        assertEquals(before,session.getMapper(StagesMapper.class).selectList(null)); return null;
    });}
    @Test void webSaveDoesNotReportDatabaseFailure() throws Exception {fx(() -> {
        WebPageConfig web=load(WebPageConfig.class,"/view/backend/WebPageConfig.fxml");
        ConfigsMapper fail=mock(ConfigsMapper.class);
        when(fail.selectById(15)).thenThrow(new IllegalStateException("AUDIT simulated database failure"));
        ReflectionTestUtils.setField(web,"configsMapper",fail);
        assertDoesNotThrow(web::saveConfig); return null;
    });}
    @Test void pageDatabaseFailureLeavesEarlierSectionsCommitted() throws Exception {fx(() -> {
        PageConfig p=load(PageConfig.class,"/view/backend/PageConfig.fxml");
        text(p,"dtx").setText("321");
        try(java.sql.Connection db=session.getConfiguration().getEnvironment().getDataSource().getConnection();java.sql.Statement st=db.createStatement()) {
            st.execute("create trigger audit_fail before update on t_configs when new.id=2 begin select raise(abort,'AUDIT failed second section'); end");
        }
        assertThrows(RuntimeException.class,p::updateConfig);
        assertEquals("321",session.getMapper(ConfigsMapper.class).selectById(1).getConfig().split("%%")[5]);
        return null;
    });}
    @Test void pageAnswerPrefixDelimiterCorruptsFieldPositions() throws Exception {fx(() -> {
        PageConfig p=load(PageConfig.class,"/view/backend/PageConfig.fxml");
        text(p,"daqz").setText("答案%%提示");p.updateConfig();
        String[] fields=session.getMapper(ConfigsMapper.class).selectById(1).getConfig().split("%%");
        assertEquals(46,fields.length);assertEquals("提示",fields[5]);
        assertThrows(javafx.fxml.LoadException.class,()->load(PageConfig.class,"/view/backend/PageConfig.fxml"));return null;
    });}
    @Test void gradeClearAndExcelExportOnCopy() throws Exception {fx(() -> {
        GradeMng c=load(GradeMng.class,"/view/backend/GradeMng.fxml");
        java.util.List<competition.entity.StageMemberScore> scores=session.getMapper(StageMemberScoreMapper.class).selectList(null);
        Path exported=Files.createTempFile(ROOT,"grade-export-",".xlsx");
        competition.util.ExcelParser.write(exported.toString(),scores,competition.entity.StageMemberScore.class);
        assertEquals(scores.size(),competition.util.ExcelParser.parse(exported.toString(),competition.entity.StageMemberScore.class).size());
        c.clearData();assertEquals(0L,session.getMapper(StageMemberScoreMapper.class).selectCount(null));return null;
    });}
    @Test void questionImportInsertFailureErasesPreviousStock() throws Exception {fx(() -> {
        QuestionStock q=load(QuestionStock.class,"/view/backend/QuestionStock.fxml");
        assertTrue(session.getMapper(QuestionsMapper.class).selectCount(null)>0);
        ReflectionTestUtils.setField(q,"changeStock",false);
        try(java.sql.Connection db=session.getConfiguration().getEnvironment().getDataSource().getConnection();java.sql.Statement st=db.createStatement()) {
            st.execute("create trigger audit_fail before insert on t_questions begin select raise(abort,'AUDIT failed import'); end");
        }
        assertThrows(RuntimeException.class,q::useStock);
        assertEquals(0L,session.getMapper(QuestionsMapper.class).selectCount(null));return null;
    });}
    @Test void stageExistingSelectionNormalUpdate() throws Exception {fx(() -> {
        StageConfig c=load(StageConfig.class,"/view/backend/StageConfig.fxml");
        competition.entity.Stages stage=session.getMapper(StagesMapper.class).selectList(null).get(0);
        ReflectionTestUtils.setField(c,"current",stage);
        ReflectionTestUtils.invokeMethod(c,"showCurrentInfo");
        text(c,"nameInput").setText("AUDIT stage rename");c.updateStage();
        assertEquals("AUDIT stage rename",session.getMapper(StagesMapper.class).selectById(stage.getId()).getName());return null;
    });}
    @Test void scoreAndTabletDefaultSaveRoundtrip() throws Exception {fx(() -> {
        load(ScoreConfig.class,"/view/backend/ScoreConfig.fxml").saveConfig();
        load(ScoreConfig.class,"/view/backend/ScoreConfig.fxml");
        load(PaneConfig.class,"/view/backend/PaneConfig.fxml").updateConfig();
        load(PaneConfig.class,"/view/backend/PaneConfig.fxml");return null;
    });}
    @Test void secondCompetitiveStageAndRestartShowFreshRoundImmediately() throws Exception {fx(() -> {
        competition.entity.Teams team = new competition.entity.Teams();
        team.setId(1); team.setFullName("竞技回归测试队"); team.setShortName("测试队"); team.setTeamScore(130);
        java.util.List<competition.entity.Teams> teams = java.util.Collections.singletonList(team);
        competition.util.TeamRuntime.setTeams(teams);
        competition.ws.Model7RuntimeData.setUIUpdateCallback(null);
        competition.ws.Model7RuntimeData.setTeams(teams);
        QuestionsMapper questions = session.getMapper(QuestionsMapper.class);
        questions.delete(null);
        for (int index=1; index<=2; index++) {
            competition.entity.Questions q = new competition.entity.Questions();
            q.setStageNumber(String.valueOf(index)); q.setQuestionNumber(1); q.setViewSort(1);
            q.setQuestionType("单选题"); q.setQuestion("第"+index+"环节题目");q.setAnswer("A");
            questions.insert(q);
        }
        competition.entity.Stages first = new competition.entity.Stages();
        first.setId(1);first.setName("竞技答题");first.setSortVal("1");first.setQuestionType(7);
        first.setScorePerQ(10);first.setDesValue(0);first.setShowNumber(1);first.setrRandomSort(0);
        competition.entity.Stages second = new competition.entity.Stages();
        org.springframework.beans.BeanUtils.copyProperties(first,second);second.setId(2);second.setSortVal("2");
        competition.controller.preview.Model7Preview preview = load(competition.controller.preview.Model7Preview.class,"/view/preview/Model7Preview.fxml");
        ControlPane control=(ControlPane)ReflectionTestUtils.getField(preview,"controlPane");
        when(control.getCurrentStage()).thenReturn(first);preview.setData(first);preview.initPage();
        competition.ws.Model7RuntimeData.ModelData old = new competition.ws.Model7RuntimeData.ModelData();
        old.setQuestionNumber(1);old.setQuestion("上一轮旧题目");old.setAnswer("A");old.setUserSelect("A");old.setScore(10);old.setDesValue(0);
        competition.ws.Model7RuntimeData.getRuntimeData().get(0).add(old);
        preview.reload(83,"01:23");
        assertTrue(labels(preview.getPane()).stream().anyMatch(value->value.contains("01:23")));
        competition.ws.Model7RuntimeData.setTeams(teams);
        when(control.getCurrentStage()).thenReturn(second);preview.destroy();preview.setData(second);preview.initPage();
        java.util.List<String> clean = labels(preview.getPane());
        assertTrue(clean.stream().anyMatch(value->value.contains("完成0题 答对0题|00:00")));
        assertTrue(clean.contains("0分")); assertFalse(clean.stream().anyMatch(value->value.contains("01:23") || value.contains("上一轮旧题目")));
        preview.reload(20,"00:20");
        competition.entity.Model7Status stale=new competition.entity.Model7Status();
        stale.setTeamId(1);stale.setQuestionText("上一轮旧题目");stale.setScore(10);stale.setCurrentNo(1);
        session.getMapper(Model7StatusMapper.class).insert(stale);
        ControlPane starting=controller(ControlPane.class);
        ReflectionTestUtils.setField(starting,"previewPane",preview);
        ReflectionTestUtils.invokeMethod(starting,"prepareModel7Round");
        assertEquals(0L,session.getMapper(Model7StatusMapper.class).selectCount(null));
        assertTrue(labels(preview.getPane()).stream().anyMatch(value->value.contains("完成0题 答对0题|00:00")));
        assertEquals(130,team.getTeamScore());
        return null;
    });}

    private java.util.List<String> labels(javafx.scene.Node node) {
        java.util.List<String> values=new java.util.ArrayList<>();
        if (node instanceof javafx.scene.control.Label && node.isVisible()) values.add(((javafx.scene.control.Label)node).getText());
        if (node instanceof javafx.scene.Parent && node.isVisible())
            for (javafx.scene.Node child:((javafx.scene.Parent)node).getChildrenUnmodifiable()) values.addAll(labels(child));
        return values;
    }

}
