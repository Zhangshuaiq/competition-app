package competition.util;

import java.io.File;

public class ImageResource {
    
    
    public static final String stageImg(String stageCode) {
        String res = BaseDir.IMAGE_DIR + File.separator + stageCode + ".png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + stageCode + ".jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + stageCode + ".jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }
    
    
    public static final String UP(){
        String res = BaseDir.IMAGE_DIR + File.separator + "up.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "up.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "up.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        
        return null;
    }

    public static final String DOWN(){
        String res = BaseDir.IMAGE_DIR + File.separator + "down.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "down.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "down.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String DELETE(){
        String res = BaseDir.IMAGE_DIR + File.separator + "delete.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "delete.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "delete.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 添加(){
        String res = BaseDir.IMAGE_DIR  + "add.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "add.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "add.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 播放(){
        String res = BaseDir.IMAGE_DIR  + "播放.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "播放.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "播放.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 暂停(){
        String res = BaseDir.IMAGE_DIR  + "暂停.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "暂停.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "暂停.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 快进(){
        String res = BaseDir.IMAGE_DIR  + "快进.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "快进.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "快进.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 快退(){
        String res = BaseDir.IMAGE_DIR  + "快退.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "快退.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR  + "快退.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 风险背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "风险环节背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "风险环节背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "风险环节背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 总规则背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "总规则背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "总规则背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "总规则背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 环节背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "环节背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "环节背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "环节背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 环节名称背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "环节名称背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "环节名称背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "环节名称背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 环节规则背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "环节规则背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "环节规则背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "环节规则背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 答题背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "答题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "答题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "答题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 团队排名背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "团队排名背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "团队排名背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "团队排名背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 环节排名背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "竞技模式背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "竞技模式背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "竞技模式背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 选手排名背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "选手排名背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "选手排名背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "选手排名背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 竞技模式背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "竞技模式背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "竞技模式背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "竞技模式背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 选题背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "选题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "选题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "选题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 滚动抽题背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "滚动抽题背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "滚动抽题背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "滚动抽题背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }

    public static final String 答案背景(){
        String res = BaseDir.IMAGE_DIR + File.separator + "答案背景.png";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "答案背景.jpg";
        if (new File(res).exists()) {
            return res;
        }

        res = BaseDir.IMAGE_DIR + File.separator + "答案背景.jpeg";
        if (new File(res).exists()) {
            return res;
        }
        return null;
    }
}
