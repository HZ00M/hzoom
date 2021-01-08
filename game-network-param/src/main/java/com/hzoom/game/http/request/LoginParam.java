package com.hzoom.game.http.request;


import com.hzoom.common.error.GameCenterError;
import com.hzoom.game.http.common.AbstractRequestParam;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class LoginParam extends AbstractRequestParam {
    private String openId;
    private String sdkToken;
    private String ip;

    @Override
    protected void haveError() {
        if (StringUtils.isEmpty(openId)){
            this.error = GameCenterError.OPENID_IS_EMPTY;
        }else if (openId.length() > 128){
            this.error = GameCenterError.OPENID_LEN_ERROR;
        }else if (StringUtils.isEmpty(sdkToken)){
            this.error = GameCenterError.SDK_TOKEN_IS_EMPTY;
        }else if (sdkToken.length() > 128){
            this.error = GameCenterError.SDK_TOKEN_LEN_ERROR;
        }
    }
}
