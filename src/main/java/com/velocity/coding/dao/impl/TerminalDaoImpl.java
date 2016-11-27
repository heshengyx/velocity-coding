package com.velocity.coding.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.velocity.coding.dao.ITerminalDao;
import com.velocity.coding.entity.Terminal;
import com.velocity.coding.mapper.ITerminalMapper;
import com.velocity.coding.param.TerminalQueryParam;


@Repository
public class TerminalDaoImpl implements ITerminalDao {

    @Autowired
    private ITerminalMapper TerminalMapper;
    
    @Override
    public int save(Terminal terminal) {
        return TerminalMapper.save(terminal);
    }

    @Override
    public int saveBatch(List<Terminal> terminals) {
        return TerminalMapper.saveBatch(terminals);
    }
    
    @Override
    public int update(Terminal terminal) {
        return TerminalMapper.update(terminal);
    }

    @Override
    public int deleteById(String id) {
        return TerminalMapper.deleteById(id);
    }

    @Override
    public int delete(Terminal terminal) {
        return TerminalMapper.delete(terminal);
    }
    
    @Override
    public int deleteByIds(List<String> ids) {
        return TerminalMapper.deleteByIds(ids);
    }

    @Override
    public Terminal getById(String id) {
        return TerminalMapper.getById(id);
    }

    @Override
    public Terminal getData(Terminal terminal) {
        return TerminalMapper.getData(terminal);
    }

    @Override
    public int count(TerminalQueryParam param) {
        return TerminalMapper.count(param);
    }

    @Override
    public List<Terminal> query(TerminalQueryParam param, int start,
            int end) {
        return TerminalMapper.query(param, start, end);
    }

    @Override
    public List<Terminal> queryAll(TerminalQueryParam param) {
        return TerminalMapper.queryAll(param);
    }
}
