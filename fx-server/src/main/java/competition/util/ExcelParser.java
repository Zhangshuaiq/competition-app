package competition.util;

import com.alibaba.excel.EasyExcel;
import competition.entity.QuestionStyle;
import competition.entity.Questions;

import java.io.File;
import java.util.List;

public class ExcelParser {


    public static final String Q_PATH = BaseDir.TIKU_DIR  + "题目.xlsx";
    public static final String S_PATH = BaseDir.YANGSHI_DIR  + "样式.xlsx";


    public static <T> List<T> parse(String path, Class<T> t) {
        List<T> list = EasyExcel.read(path).head(t).sheet().doReadSync();
        return list;
    }
    
    
    
    public static <T>  void write(String path,  List<T> data, Class<T> t) {
        EasyExcel.write(path).sheet().head(t).doWrite(data);
    }
}
