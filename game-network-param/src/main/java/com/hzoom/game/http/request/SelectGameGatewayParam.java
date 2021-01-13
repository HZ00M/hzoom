package com.hzoom.game.http.request;

import com.hzoom.game.error.GameCenterError;
import com.hzoom.game.http.common.AbstractRequestParam;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class SelectGameGatewayParam extends AbstractRequestParam {
    private String openId; // 第三方用户唯一id
    private long playerId; // 角色id
    private long userId; // 用户id
    private String zoneId = "0"; // 选择的区id
    @Override
    protected void haveError() {
        if (StringUtils.isEmpty(openId)) {
            this.error = GameCenterError.OPENID_IS_EMPTY;
        } else if (openId.length() > 128) {
            this.error = GameCenterError.OPENID_LEN_ERROR;
        } else if (StringUtils.isEmpty(this.zoneId)) {
            this.error = GameCenterError.ZONE_ID_IS_EMPTY;
        }
    }
}
