package com.java.birdme.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.java.birdme.bean.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
