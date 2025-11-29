package competition.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.util.*;

/**
 * 支持分组的 ComboBox 组件
 * @param <T> 数据项的类型
 */
public class GroupedComboBox<T> extends ComboBox<GroupedComboBox.GroupItem<T>> {

    private final Map<String, List<T>> groupedData = new LinkedHashMap<>();
    private final ObjectProperty<T> selectedValue = new SimpleObjectProperty<>();

    /**
     * 分组项数据结构
     */
    public static class GroupItem<T> {
        private final String groupName;
        private final T value;
        private final boolean isHeader;

        // 分组头构造函数
        public GroupItem(String groupName) {
            this.groupName = groupName;
            this.value = null;
            this.isHeader = true;
        }

        // 普通项构造函数
        public GroupItem(String groupName, T value) {
            this.groupName = groupName;
            this.value = value;
            this.isHeader = false;
        }

        public String getGroupName() {
            return groupName;
        }

        public T getValue() {
            return value;
        }

        public boolean isHeader() {
            return isHeader;
        }

        @Override
        public String toString() {
            return isHeader ? groupName : String.valueOf(value);
        }
    }

    /**
     * 分组样式配置类
     */
    public static class GroupStyle {
        private String headerStyle =
                "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        "-fx-background-color: #e8e8e8; " +
                        "-fx-text-fill: #333; " +
                        "-fx-padding: 5 5 5 5;";

        private String itemStyle =
                "-fx-padding: 3 5 3 25; " +
                        "-fx-font-size: 12px;";

        public String getHeaderStyle() {
            return headerStyle;
        }

        public void setHeaderStyle(String headerStyle) {
            this.headerStyle = headerStyle;
        }

        public String getItemStyle() {
            return itemStyle;
        }

        public void setItemStyle(String itemStyle) {
            this.itemStyle = itemStyle;
        }
    }

    private GroupStyle groupStyle = new GroupStyle();
    private StringConverter<T> itemConverter;

    public GroupedComboBox() {
        super();
        initialize();
    }

    private void initialize() {
        // 设置默认的字符串转换器
        this.itemConverter = new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };

        // 设置 Cell Factory
        setCellFactory(lv -> new ListCell<GroupItem<T>>() {
            @Override
            protected void updateItem(GroupItem<T> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    setDisable(false);
                } else {
                    if (item.isHeader()) {
                        setText(item.getGroupName());
                        setStyle(groupStyle.getHeaderStyle());
                        setDisable(true);
                    } else {
                        setText(itemConverter.toString(item.getValue()));
                        setStyle(groupStyle.getItemStyle());
                        setDisable(false);
                    }
                }
            }
        });

        // 设置 Button Cell（选中后显示的内容）
        setButtonCell(new ListCell<GroupItem<T>>() {
            @Override
            protected void updateItem(GroupItem<T> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isHeader()) {
                    setText(null);
                } else {
                    setText(itemConverter.toString(item.getValue()));
                }
            }
        });

        // 监听选择变化
        valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isHeader()) {
                selectedValue.set(newVal.getValue());
            }
        });
    }

    /**
     * 添加一个分组及其数据
     * @param groupName 分组名称
     * @param items 分组下的数据项
     */
    public void addGroup(String groupName, List<T> items) {
        groupedData.put(groupName, new ArrayList<>(items));
        rebuildItems();
    }

    /**
     * 添加一个分组及其数据（可变参数）
     * @param groupName 分组名称
     * @param items 分组下的数据项
     */
    @SafeVarargs
    public final void addGroup(String groupName, T... items) {
        addGroup(groupName, Arrays.asList(items));
    }

    /**
     * 设置所有分组数据
     * @param data 分组数据 Map，key 为分组名，value 为该分组的数据列表
     */
    public void setGroupedData(Map<String, List<T>> data) {
        groupedData.clear();
        groupedData.putAll(data);
        rebuildItems();
    }

    /**
     * 清空所有数据
     */
    public void clearGroups() {
        groupedData.clear();
        getItems().clear();
        selectedValue.set(null);
    }

    /**
     * 移除指定分组
     * @param groupName 分组名称
     */
    public void removeGroup(String groupName) {
        groupedData.remove(groupName);
        rebuildItems();
    }

    /**
     * 重建 ComboBox 的显示项
     */
    private void rebuildItems() {
        ObservableList<GroupItem<T>> items = FXCollections.observableArrayList();

        for (Map.Entry<String, List<T>> entry : groupedData.entrySet()) {
            String groupName = entry.getKey();
            List<T> groupItems = entry.getValue();

            // 添加分组头
            items.add(new GroupItem<>(groupName));

            // 添加分组项
            for (T item : groupItems) {
                items.add(new GroupItem<>(groupName, item));
            }
        }

        getItems().setAll(items);
    }

    /**
     * 获取选中的值（不包括分组头）
     * @return 选中的值
     */
    public T getSelectedValue() {
        return selectedValue.get();
    }

    /**
     * 设置选中的值
     * @param value 要选中的值
     */
    public void setSelectedValue(T value) {
        for (GroupItem<T> item : getItems()) {
            if (!item.isHeader() && Objects.equals(item.getValue(), value)) {
                setValue(item);
                break;
            }
        }
    }

    /**
     * 获取选中值的属性
     * @return 选中值的属性
     */
    public ObjectProperty<T> selectedValueProperty() {
        return selectedValue;
    }

    /**
     * 设置自定义的分组样式
     * @param groupStyle 分组样式配置
     */
    public void setGroupStyle(GroupStyle groupStyle) {
        this.groupStyle = groupStyle;
        // 触发样式更新
        setCellFactory(getCellFactory());
    }

    /**
     * 设置项的字符串转换器
     * @param converter 转换器
     */
    public void setItemConverter(StringConverter<T> converter) {
        this.itemConverter = converter;
    }

    /**
     * 获取所有分组数据
     * @return 分组数据 Map
     */
    public Map<String, List<T>> getGroupedData() {
        return new LinkedHashMap<>(groupedData);
    }

    /**
     * 获取指定分组的数据
     * @param groupName 分组名称
     * @return 该分组的数据列表
     */
    public List<T> getGroupItems(String groupName) {
        return groupedData.getOrDefault(groupName, Collections.emptyList());
    }

    /**
     * 获取所有分组名称
     * @return 分组名称列表
     */
    public List<String> getGroupNames() {
        return new ArrayList<>(groupedData.keySet());
    }
}