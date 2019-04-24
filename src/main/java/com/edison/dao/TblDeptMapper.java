package com.edison.dao;

import com.edison.entity.TblDept;

public interface TblDeptMapper {
    int deleteByPrimaryKey(Integer deptId);

    int insert(TblDept record);

    int insertSelective(TblDept record);

    TblDept selectByPrimaryKey(Integer deptId);

    int updateByPrimaryKeySelective(TblDept record);

    int updateByPrimaryKey(TblDept record);
}