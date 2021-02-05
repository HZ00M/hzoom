package com.hzoom.game.common.service.impl;

import com.hzoom.game.common.entity.TUser;
import com.hzoom.game.common.mapper.TUserMapper;
import com.hzoom.game.common.service.TUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzoom
 * @since 2021-02-04
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {
}
