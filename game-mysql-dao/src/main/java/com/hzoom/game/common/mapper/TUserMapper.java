package com.hzoom.game.common.mapper;

import com.hzoom.core.datasource.anatation.DataSource;
import com.hzoom.core.datasource.enums.DataSourceType;
import com.hzoom.game.common.entity.TUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzoom
 * @since 2021-02-23
 */
public interface TUserMapper extends BaseMapper<TUser> {
    @DataSource(value = "cloud", type = DataSourceType.WRITE)
    @Insert("INSERT INTO t_user ( username, password, nickname ) VALUES ( #{user.username}, #{user.password}, #{user.nickname} ) ")
    int inserts(@Param("user") TUser entity);

    @Select("select * from t_user")
    @DataSource(value = "cloud", type = DataSourceType.WRITE)
    List<TUser> select();
}
