package com.mingri.mybatissmart;

import com.mingri.mybatissmart.mapper.SmartMapper;

public class Test {

	
	private static class BaseUser{
		
	}
	
	private static class User extends BaseUser{
		
	}
	
	private static interface UserMapper extends SmartMapper<BaseUser>{
		
	}
	
	private UserMapper userMapper;
	
	
	public void testname() throws Exception {
		User u=new User();
		userMapper.updateById(u);
	}
}
