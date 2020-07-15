package com.demo2.demo.dao;

import com.demo2.demo.entities.Department;
import com.demo2.demo.entities.Employee;
import com.demo2.demo.entities.UsersEntity;

import com.demo2.demo.respority.userResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EmployeeDao {


	@Autowired
	private userResp userResp;


	public UsersEntity check(String username,String password){
		UsersEntity user = userResp.findByUsername(username);
			if (user.getUsername().equals(username)&&user.getPassword().equals(password)){
				return user;
			}
		else {
				return null;
			}
	}


}
