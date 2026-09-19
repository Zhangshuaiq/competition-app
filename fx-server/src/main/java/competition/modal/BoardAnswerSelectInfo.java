package competition.modal;

import competition.entity.Teams;
import lombok.Data;

import java.util.List;

@Data
public class BoardAnswerSelectInfo {

    private List<Teams> selectTeams;

    private int selectMemberIndex;

    // 是否将本模式下指定队员的累计答题时间用于同分排序
    private boolean accumulateMemberTime;
}
