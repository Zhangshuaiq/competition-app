package competition.util;

import java.io.File;

public class ImageResource {
    
    
    public static final String stageImg(String stageCode) {
        String res = BaseDir.BACKGROUND_DIR + File.separator + stageCode + ".png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + stageCode + ".jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + stageCode + ".jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 环节名称背景();
    }
    
    
    public static final String UP(){
        String res = BaseDir.SUCAI_DIR + File.separator + "上.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR + File.separator + "上.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR + File.separator + "上.jpeg";
        if (new File(res).exists()) {
            return res;
        }

        return 默认背景();
    }

    public static final String DOWN(){
        String res = BaseDir.SUCAI_DIR + File.separator + "下.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR + File.separator + "下.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR + File.separator + "下.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String DELETE(){
        String res = BaseDir.SUCAI_DIR + File.separator + "删除.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR + File.separator + "删除.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR + File.separator + "删除.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 添加(){
        String res = BaseDir.SUCAI_DIR  + "添加.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "添加.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "添加.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 播放(){
        String res = BaseDir.SUCAI_DIR  + "播放.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "播放.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "播放.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 暂停(){
        String res = BaseDir.SUCAI_DIR  + "暂停.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "暂停.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "暂停.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 快进(){
        String res = BaseDir.SUCAI_DIR  + "快进.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "快进.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "快进.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 快退(){
        String res = BaseDir.SUCAI_DIR  + "快退.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "快退.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.SUCAI_DIR  + "快退.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 风险题背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "风险题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "风险题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "风险题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 扩展屏打开背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "扩展屏打开背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "扩展屏打开背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "扩展屏打开背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }
    

    public static final String 总规则背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "总规则背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "总规则背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "总规则背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 环节背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "环节背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "环节背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "环节背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 环节名称背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "环节名称背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "环节名称背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "环节名称背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 环节规则背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "环节规则背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "环节规则背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "环节规则背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 答题背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "答题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "答题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "答题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 团队排名背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "团队排名背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "团队排名背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "团队排名背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 竞技排名背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "竞技排名背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "竞技排名背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "竞技排名背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 选手排名背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "选手排名背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "选手排名背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "选手排名背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 竞技模式背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "竞技模式背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "竞技模式背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "竞技模式背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 选题背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "选题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "选题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "选题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 滚动抽题背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "滚动抽题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "滚动抽题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "滚动抽题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 答案背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "答案背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "答案背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "答案背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return 默认背景();
    }

    public static final String 默认背景(){
        String res = BaseDir.BACKGROUND_DIR + File.separator + "默认背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "默认背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.BACKGROUND_DIR + File.separator + "默认背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }
}
