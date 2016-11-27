package com.velocity.coding.service;

import java.util.List;
import com.velocity.coding.entity.Style;
import com.velocity.coding.param.StyleQueryParam;
import com.velocity.coding.page.IPage;
import com.velocity.coding.data.StyleData;


public interface IStyleService {

    /**
     * 新增
     * @param style
     */
    void save(Style style);
    /**
     * 批量新增
     * @param styles
     * @return
     */
    void saveBatch(List<Style> styles);
    /**
     * 批量新增
     * @param data
     * @return
     */
    void saveBatchData(StyleData data);
    
    /**
     * 修改
     * @param Style
     */
    void update(Style style);
    
    /**
     * 根据ID删除
     * @param id
     */
    void deleteById(String id);
    
    /**
     * 多条件删除
     * @param style
     */
    void delete(Style style);
    
    /**
     * 根据ID批量删除
     * @param id
     */
    void deleteByIds(List<String> ids);
    
    /**
     * 根据ID查找
     * @param id
     * @return
     */
    Style getById(String id);
    
    /**
     * 多条件查找
     * @param style
     * @return
     */
    Style getData(Style style);
    
    /**
     * 保存或修改
     * @param style
     */
    void saveOrUpdate(Style style);
    
    /**
     * 分页查询
     * @param param
     * @param page
     * @param rows
     * @return
     */
    IPage<Style> query(StyleQueryParam param);
}