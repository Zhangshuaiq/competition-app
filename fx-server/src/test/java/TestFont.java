import javafx.scene.text.Font;


public class TestFont {

    public static void main(String[] args) {
       // String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        // 显示字体列表
//        for (String fontName : fontNames) {
//            System.out.println(fontName);
//        }
//        System.out.println(fontNames.length);
        System.out.println("11111111111111111");
        System.out.println("Font Families:");
//        System.out.println(javafx.scene.text.Font.getFamilies());
        
        
        
        
        // 获取具体字体名称列表（包括样式）
        System.out.println("\nFont Names:");
        System.out.println(javafx.scene.text.Font.getFontNames().size());

        Font font = Font.font(23);
        System.out.println(font.getStyle());

    }
}
