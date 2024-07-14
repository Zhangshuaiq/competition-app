package competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import competition.entity.Questions;
import org.apache.ibatis.annotations.Mapper;

@Mapper 
public interface QuestionsMapper extends BaseMapper<Questions> {
}
