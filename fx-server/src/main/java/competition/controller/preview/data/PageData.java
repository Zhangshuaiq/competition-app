package competition.controller.preview.data;

/**
 * 界面上展示的数据类
 */
public class PageData {

    // preview 页面的类型，不同的类型展示的数据不一样
    public Class<?> type;

    public PageData(Class<?> type) {
        this.type = type;
    }


    public static class NullData extends PageData {

        public NullData() {
            super(Void.class);
        }
    }
}
