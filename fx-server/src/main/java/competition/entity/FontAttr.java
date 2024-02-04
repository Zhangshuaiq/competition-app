package competition.entity;

// 字体属性设置
public class FontAttr {
    private String color;
    private int size;
    private String type;
    private String family;
    private int underLine;
    private int deleteLine;

    public FontAttr(){
        
    }
    
    public FontAttr(String color, int size, String type, String family, int underLine, int deleteLine) {
        this.color = color;
        this.size = size;
        this.type = type;
        this.family = family;
        this.underLine = underLine;
        this.deleteLine = deleteLine;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public int getUnderLine() {
        return underLine;
    }

    public void setUnderLine(int underLine) {
        this.underLine = underLine;
    }

    public int getDeleteLine() {
        return deleteLine;
    }

    public void setDeleteLine(int deleteLine) {
        this.deleteLine = deleteLine;
    }

    @Override
    public String toString() {
        return "FontAttr{" +
                "color='" + color + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", family='" + family + '\'' +
                ", underLine=" + underLine +
                ", deleteLine=" + deleteLine +
                '}';
    }
}
