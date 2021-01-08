package com.hzoom.game.http.request;

import com.hzoom.common.error.GameCenterError;
import com.hzoom.game.http.common.AbstractRequestParam;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class CreatePlayerParam extends AbstractRequestParam {
    private String zoneId = "0";// 如果是分区游戏，需要传区id
    private String nickName;
    @Override
    protected void haveError() {
        if (StringUtils.isEmpty(zoneId)){
            this.error = GameCenterError.ZONE_ID_IS_EMPTY;
        }else if (StringUtils.isEmpty(nickName)){
            this.error = GameCenterError.NICKNAME_IS_EMPTY;
        }else if (nickName.length()>10||nickName.length()<2){
            this.error = GameCenterError.NICKNAME_LEN_ERROR;
        }
    }
}
