package competition.modal;

import competition.entity.Teams;
import lombok.Data;

import java.util.List;

@Data
public class BoardAnswerSelectInfo {

    private List<Teams> selectTeams;

    private int selectMemberIndex;
}
