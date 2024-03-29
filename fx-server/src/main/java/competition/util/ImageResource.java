package competition.util;

import java.io.File;

public class ImageResource {
    
    
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
}
