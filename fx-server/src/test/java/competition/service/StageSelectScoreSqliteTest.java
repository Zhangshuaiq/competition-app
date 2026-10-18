package competition.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import competition.entity.*;
import competition.mapper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StageSelectScoreSqliteTest {
    @TempDir Path temp;

    @Test
    void realMappersCommitZeroAndPositiveTotalsAndRollbackFailedRecord() throws Exception {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:sqlite:" + temp.resolve("scores.db"));
        dataSource.setDriverClassName("org.sqlite.JDBC");
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("create table t_teams (id integer primary key,short_name text not null,full_name text not null,code text not null,members text,team_score integer,member_score text,password text)");
        jdbc.execute("create table t_stage_member_score (id integer primary key autoincrement,team text,stage text,member text,question text,time text,add_score text,des_score text,score text,total_score text,answer_text text)");
        jdbc.update("insert into t_teams(id,short_name,full_name,code,team_score) values(1,'测试','测试队','1',-10)");
        MybatisConfiguration config = new MybatisConfiguration();
        config.addMapper(TeamsMapper.class);
        config.addMapper(StageMemberScoreMapper.class);
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(config);
        SqlSessionTemplate session = new SqlSessionTemplate(factory.getObject());
        TeamsMapper teams = session.getMapper(TeamsMapper.class);
        ScoreService records = new ScoreService();
        ReflectionTestUtils.setField(records,"scoreMapper",session.getMapper(StageMemberScoreMapper.class));
        ReflectionTestUtils.setField(records,"hardwareService",mock(HardwareService.class));
        StageSelectScoreService service = new StageSelectScoreService();
        ReflectionTestUtils.setField(service,"teamsMapper",teams);
        ReflectionTestUtils.setField(service,"scoreService",records);
        ReflectionTestUtils.setField(service,"transactionManager",new DataSourceTransactionManager(dataSource));
        Teams team = teams.selectById(1);
        Stages stage = new Stages(); stage.setId(8); stage.setName("自选"); stage.setCountTotal(1);
        Questions q = new Questions(); q.setId(1); q.setQuestion("第一题");
        service.apply(stage,q,team,10,"A");
        assertEquals(0,teams.selectById(1).getTeamScore());
        service.apply(stage,q,team,10,"A");
        assertEquals(1,jdbc.queryForObject("select count(*) from t_stage_member_score",Integer.class));
        q.setId(2);
        service.apply(stage,q,team,10,"A");
        assertEquals(10,teams.selectById(1).getTeamScore());
        assertEquals("10",jdbc.queryForObject("select total_score from t_stage_member_score order by id desc limit 1",String.class));
        // 模拟计分明细写入失败，验证总分不能部分提交。
        jdbc.execute("create trigger fail_score before insert on t_stage_member_score begin select raise(abort,'test failure'); end");
        q.setId(3);
        assertThrows(RuntimeException.class,()->service.apply(stage,q,team,10,"A"));
        assertEquals(10,team.getTeamScore());
        assertEquals(10,teams.selectById(1).getTeamScore());
        jdbc.execute("drop trigger fail_score");
        service.apply(stage,q,team,10,"A");
        assertEquals(20,teams.selectById(1).getTeamScore());
        service.beginQuestionRound(stage,q);
        service.apply(stage,q,team,10,"A");
        service.apply(stage,q,team,10,"A");
        assertEquals(30,teams.selectById(1).getTeamScore());
        assertEquals(4,jdbc.queryForObject("select count(*) from t_stage_member_score",Integer.class));
    }
}
