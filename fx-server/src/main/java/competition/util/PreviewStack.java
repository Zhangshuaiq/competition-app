package competition.util;

import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;

import java.util.Stack;

public class PreviewStack {
    
    
    private static final Stack<PreviewPane> previewStack = new Stack<>();

    public static final void addPreview (PreviewPane pane) {
        if (RuntimeUtil.addLast) {
            previewStack.add(pane);
        }
    }
    

    public static final void removePreview() {
        if (previewStack.isEmpty()) {
            return ;
        }
        PreviewPane pop = previewStack.pop();
    }
    
    
    
    public static final void reloadStyle(){
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        PreviewPane previewPane = controlPane.getPreviewPane();
        if (previewPane != null) {
            previewPane.reloadStyle();
        }
    }

    public static final void reload() {
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        PreviewPane previewPane = controlPane.getPreviewPane();
        if (previewPane != null) {
            previewPane.reload();
        }
    }


    public static final PreviewPane last(){
        if (previewStack.isEmpty()) {
            return null;
        }
        PreviewPane pop = previewStack.peek();
        //  pop.destroy();
        return pop;
    }
    
    public static final PreviewPane getLast(){
        if (previewStack.isEmpty()) {
            return null;
        }
        PreviewPane pop = previewStack.pop();
      //  pop.destroy();
        return pop;
    }

    public static final void reload(int time, String timeTxt) {
        if (previewStack.isEmpty()) {
            return ;
        }
        PreviewPane pop = previewStack.peek();
        pop.reload(time, timeTxt);
    }

    public static final void clear(){
        previewStack.clear();
    }
    
}
