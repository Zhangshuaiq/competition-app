package competition.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 团队成员字段的统一编解码入口。
 *
 * 当前写入仍使用历史逗号格式，避免影响现有数据库和业务流程；读取同时兼容
 * 历史逗号格式和 JSON 数组，为后续无停机迁移预留能力。
 */
public final class TeamMemberCodec {

    private TeamMemberCodec() {
    }

    public static List<String> decode(String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                List<String> members = JSON.parseArray(trimmed, String.class);
                return members == null ? Collections.emptyList() : members;
            } catch (JSONException ignored) {
                // It may be legacy text which happens to be wrapped in square brackets.
            }
        }

        String[] legacyMembers = value.split(",");
        List<String> members = new ArrayList<>(legacyMembers.length);
        Collections.addAll(members, legacyMembers);
        return members;
    }

    public static String[] split(String value) {
        List<String> members = decode(value);
        return members.toArray(new String[0]);
    }

    public static String encodeLegacy(List<String> members) {
        return members == null || members.isEmpty() ? "" : String.join(",", members);
    }

    public static String encodeJson(List<String> members) {
        return JSON.toJSONString(members == null ? Collections.emptyList() : members);
    }
}
