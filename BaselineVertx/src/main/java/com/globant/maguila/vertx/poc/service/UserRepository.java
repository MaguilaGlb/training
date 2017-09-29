package com.globant.maguila.vertx.poc.service;

import java.util.List;

import com.globant.maguila.vertx.poc.User;

public interface UserRepository {
	
	public User findUserById(Long id);
	public List<User> findAllUsers();

}
