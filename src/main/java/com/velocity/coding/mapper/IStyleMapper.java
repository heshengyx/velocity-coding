package com.velocity.coding.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.velocity.coding.entity.Style;
import com.velocity.coding.param.StyleQueryParam;


public interface IStyleMapper {

    /**
     * 新增
     * @param style
     * @return
     */
    int save(@Param("param") Style style);
    /**
     * 批量新增
     * @param styles
     * @return
     */
    int saveBatch(@Param("list") List<Style> styles);
    
    /**
     * 修改
     * @param style
     */
    int update(@Param("param") Style style);
    
    /**
     * 根据ID删除
     * @param id
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 多条件删除
     * @param style
     */
    int delete(@Param("param") Style style);
    
    /**
     * 根据ID批量删除
     * @param id
     */
    int deleteByIds(@Param("ids") List<String> ids);
    
    /**
     * 根据ID查找
     * @param id
     * @return
     */
    Style getById(@Param("id") String id);
    
    /**
     * 多条件查找
     * @param style
     * @return
     */
    Style getData(@Param("param") Style style);
    
    int count(@Param("param") StyleQueryParam param);
    List<Style> query(@Param("param") StyleQueryParam param,
            @Param("start") int start, @Param("end") int end);
    
    /**
     * 查找全部
     * @param param
     * @return
     */
    List<Style> queryAll(@Param("param") StyleQueryParam param);
}

