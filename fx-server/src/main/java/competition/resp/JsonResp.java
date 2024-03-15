package competition.resp;

import lombok.Data;

@Data
public class JsonResp {
    
    private int code;
    
    private String msg;
    
    private Object data;

    public JsonResp() {
    }

    public JsonResp(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResp success() {
        return new JsonResp(200, "", null);
    }

    public static JsonResp success(Object data) {
        return new JsonResp(200, "", data);
    }
    
    public static JsonResp error(String msg) {
        return new JsonResp(500, "", null);
    }
}
