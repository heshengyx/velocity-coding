package com.velocity.coding.dao;

import java.util.List;

import com.velocity.coding.entity.Terminal;
import com.velocity.coding.param.TerminalQueryParam;


public interface ITerminalDao {

    /**
     * 新增
     * @param terminal
     * @return
     */
    int save(Terminal terminal);
    /**
     * 批量新增
     * @param terminals
     * @return
     */
    int saveBatch(List<Terminal> terminals);
    
    /**
     * 修改
     * @param terminal
     */
    int update(Terminal terminal);
    
    /**
     * 根据ID删除
     * @param id
     */
    int deleteById(String id);
    
    /**
     * 多条件删除
     * @param terminal
     */
    int delete(Terminal terminal);
    
    /**
     * 根据ID批量删除
     * @param id
     */
    int deleteByIds(List<String> ids);
    
    /**
     * 根据ID查找
     * @param id
     * @return
     */
    Terminal getById(String id);
    
    /**
     * 多条件查找
     * @param terminal
     * @return
     */
    Terminal getData(Terminal terminal);

    int count(TerminalQueryParam param);
    /**
     * 分页查询
     * @param param
     * @param start
     * @param end
     * @return
     */
    List<Terminal> query(TerminalQueryParam param, int start, int end);
    
    /**
     * 查找全部
     * @param param
     * @return
     */
    List<Terminal> queryAll(TerminalQueryParam param);
}
