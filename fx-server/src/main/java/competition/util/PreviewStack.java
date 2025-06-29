package competition.util;

import competition.controller.preview.PreviewPane;

import java.util.Stack;

public class PreviewStack {
    
    
    private static final Stack<PreviewPane> previewStack = new Stack<>();

    public static final void addPreview (PreviewPane pane) {
        previewStack.add(pane);
    }
    

    public static final void removePreview() {
        if (previewStack.isEmpty()) {
            return ;
        }
        PreviewPane pop = previewStack.pop();
    }
    
    
    
    public static final void reloadStyle(){
        if (previewStack.isEmpty()) {
            return ;
        }
        PreviewPane pop = previewStack.peek();
        pop.reloadStyle();
    }

    public static final void reload() {
        if (previewStack.isEmpty()) {
            return ;
        }
        PreviewPane pop = previewStack.peek();
        pop.reload();
    }
    
    public static final PreviewPane getLast(){
        if (previewStack.isEmpty()) {
            return null;
        }
        PreviewPane pop = previewStack.pop();
        pop.destroy();
        return pop;
    }

    public static final void reload(int time, String timeTxt) {
        if (previewStack.isEmpty()) {
            return ;
        }
        PreviewPane pop = previewStack.peek();
        pop.reload(time, timeTxt);
    }
    
}
