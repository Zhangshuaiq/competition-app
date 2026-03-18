package competition.service;

import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class TeamService {

    @Resource
    private TeamsMapper teamsMapper;

    @Async("scoreTaskExecutor")
    public void updateTeamAsync(Teams team) {
        try {
            teamsMapper.updateById(team);
        } catch (Exception e) {
            log.error("异步更新队伍失败: {} - {}", team.getFullName(), e.getMessage());
        }
    }
}
