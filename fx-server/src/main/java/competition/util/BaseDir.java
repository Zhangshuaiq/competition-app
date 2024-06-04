package competition.util;

import java.io.File;

public class BaseDir {
    
    public static final String IMAGE_DIR = System.getProperty("user.dir") +  File.separator + "资源" +  File.separator + "图片" +  File.separator;

    public static final String VIDEO_DIR = System.getProperty("user.dir") + File.separator + "资源" +  File.separator + "视频" +  File.separator;

    public static final String MUSIC_DIR = System.getProperty("user.dir") + File.separator + "资源" +  File.separator + "音频" +  File.separator;

    public static final String TIKU_DIR = System.getProperty("user.dir") + File.separator + "资源" +  File.separator + "题库" +  File.separator;

    public static final String TIKU_RES_DIR = System.getProperty("user.dir") + File.separator + "资源" +  File.separator + "题库" +  File.separator + "资源" +  File.separator;

    public static final String PB_RES_DIR = System.getProperty("user.dir") + File.separator + "资源" +  File.separator + "平板" +  File.separator ;

    public static final String TXT_DIR = System.getProperty("user.dir") + File.separator + "资源" +  File.separator + "文本" +  File.separator;
}
