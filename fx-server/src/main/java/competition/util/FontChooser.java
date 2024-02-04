package competition.util;

import competition.entity.FontAttr;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class FontChooser extends JPanel {
    private JList<String> listFontFamily;
    private JList<String> listFontStyle;
    private JList<String> listFontSize;
    private JLabel previewLabel;
    private Font currentFont;
    private JCheckBox underlineCheckBox;
    private JCheckBox strikethroughCheckBox;

    public FontChooser() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // 字体家族列表
        listFontFamily = new JList<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        listFontFamily.setSelectedIndex(0);
        listFontFamily.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFontFamily.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                applyFont();
            }
        });
        JScrollPane fontFamilyScrollPane = new JScrollPane(listFontFamily);

        // 字体样式列表
        String[] styles = {"常规", "粗体", "斜体", "粗斜体"};
        listFontStyle = new JList<>(styles);
        listFontStyle.setSelectedIndex(0);
        listFontStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFontStyle.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                applyFont();
            }
        });
        JScrollPane fontStyleScrollPane = new JScrollPane(listFontStyle);

        // 字体大小列表
        String[] sizes = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};
        listFontSize = new JList<>(sizes);
        listFontSize.setSelectedIndex(4);
        listFontSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFontSize.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                applyFont();
            }
        });
        JScrollPane fontSizeScrollPane = new JScrollPane(listFontSize);

        // 初始化当前字体
        currentFont = new Font(listFontFamily.getSelectedValue(), Font.PLAIN, Integer.parseInt(listFontSize.getSelectedValue()));

        // 预览标签
        previewLabel = new JLabel("预览字体");
        updatePreviewLabelFont();
        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewLabel.setBorder(BorderFactory.createTitledBorder("示例"));

        // 添加到布局
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(fontFamilyScrollPane, c);

        c.gridx = 1;
        this.add(fontStyleScrollPane, c);

        c.gridx = 2;
        this.add(fontSizeScrollPane, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 0.1;
        this.add(previewLabel, c);

        // 按钮
        underlineCheckBox = new JCheckBox("下划线");
        underlineCheckBox.addActionListener(e -> applyUnderline(underlineCheckBox.isSelected()));

        strikethroughCheckBox = new JCheckBox("删除线");
        strikethroughCheckBox.addActionListener(e -> applyStrikethrough(strikethroughCheckBox.isSelected()));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(underlineCheckBox);
        buttonPanel.add(strikethroughCheckBox);

        c.gridy = 2;
        c.weighty = 0;
        this.add(buttonPanel, c);
    }

    private void applyFont() {
        String fontFamily = listFontFamily.getSelectedValue();
        String fontStyle = listFontStyle.getSelectedValue();
        int fontSize = Integer.parseInt(listFontSize.getSelectedValue());

        int style = Font.PLAIN;
        if ("粗体".equals(fontStyle)) {
            style = Font.BOLD;
        } else if ("斜体".equals(fontStyle)) {
            style = Font.ITALIC;
        } else if ("粗斜体".equals(fontStyle)) {
            style = Font.BOLD | Font.ITALIC;
        }

        currentFont = new Font(fontFamily, style, fontSize);
        updatePreviewLabelFont();
    }

    private void applyUnderline(boolean addUnderline) {
        Map<TextAttribute, Object> attributes = new HashMap<>(currentFont.getAttributes());

        if (addUnderline) {
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        } else {
            attributes.remove(TextAttribute.UNDERLINE);
        }

        currentFont = new Font(attributes);
        updatePreviewLabelFont();
    }

    private void applyStrikethrough(boolean addStrikethrough) {
        Map<TextAttribute, Object> attributes = new HashMap<>(currentFont.getAttributes());

        if (addStrikethrough) {
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        } else {
            attributes.remove(TextAttribute.STRIKETHROUGH);
        }

        currentFont = new Font(attributes);
        updatePreviewLabelFont();
    }

    private void updatePreviewLabelFont() {
        previewLabel.setFont(currentFont);
        previewLabel.repaint();
    }
    
    public FontAttr getFontAttr(){
        FontAttr attr = new FontAttr();
        attr.setDeleteLine(strikethroughCheckBox.isSelected()? 1: 0);
        attr.setUnderLine(underlineCheckBox.isSelected()? 1: 0);
        attr.setColor("");
        attr.setType(listFontStyle.getSelectedValue());
        attr.setFamily(listFontFamily.getSelectedValue());
        attr.setSize(Integer.parseInt(listFontSize.getSelectedValue()));
        return attr;
    }
    

}
