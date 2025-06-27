package competition.controller.preview.data;

/**
 * 界面上展示的数据类
 */
public class PreviewData {

    // preview 页面的类型，不同的类型展示的数据不一样
    private Class<?> type;
    private int page;

    public PreviewData(Class<?> type) {
        this.type = type;
    }


    public static class NullData extends PreviewData {

        public NullData() {
            super(Void.class);
        }
    }
}
