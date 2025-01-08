package competition.util;

import java.io.File;

public class MusicResource {
    
    public static final String SECOND_PREFIX = BaseDir.MUSIC_DIR + "倒计时" +  File.separator;
    
    public static final String SECOND_5 = BaseDir.MUSIC_DIR +  "倒计时" +  File.separator +  "5.mp3";

    public static final String SECOND_4 = BaseDir.MUSIC_DIR +  "倒计时" +  File.separator +  "4.mp3";

    public static final String SECOND_3 = BaseDir.MUSIC_DIR + "倒计时" +  File.separator +  "3.mp3";

    public static final String SECOND_2 = BaseDir.MUSIC_DIR + "倒计时" +  File.separator +  "2.mp3";

    public static final String SECOND_1 = BaseDir.MUSIC_DIR +  "倒计时" +  File.separator +  "1.mp3";

    public static final String SECOND_0 = BaseDir.MUSIC_DIR +  "倒计时" +  File.separator +  "时间到.mp3";
    
    public static final String ADD_SCORE =  BaseDir.MUSIC_DIR + "加分.mp3";

    public static final String DES_SCORE =  BaseDir.MUSIC_DIR + "减分.mp3";
    
    public static final String SHOW_ANSWER = BaseDir.MUSIC_DIR + "答案显示.mp3";

    public static final String SHOW_QUESTION = BaseDir.MUSIC_DIR + "试题显示.mp3";

    public static final String BUZZ_SUCCESS(String teamCode) {
        return BaseDir.MUSIC_DIR + "抢答" +  File.separator +  "成功" + File.separator + teamCode + "cg.mp3";
    }

    public static final String BUZZ_FAIL(String teamCode) {
        return BaseDir.MUSIC_DIR + "抢答" +  File.separator +  "失败" + File.separator + teamCode + "fg.mp3";
    }

    public static final String BUZZ_TIME(int second) {
        return BaseDir.MUSIC_DIR + "抢答" +  File.separator +  second + ".mp3";
    }


   // public static final String BUZZ_SUCCESS = BaseDir.MUSIC_DIR + "抢答" +  File.separator +  "成功.mp3";

   // public static final String BUZZ_FAIL = BaseDir.MUSIC_DIR + "抢答" +  File.separator +  "违规.mp3";

    public static final String BUZZ_START = BaseDir.MUSIC_DIR + "抢答" +  File.separator +  "开始.mp3";

    public static final String BUZZ_STOP = BaseDir.MUSIC_DIR + "抢答" +  File.separator +  "时间到请停止回答.mp3";
}
