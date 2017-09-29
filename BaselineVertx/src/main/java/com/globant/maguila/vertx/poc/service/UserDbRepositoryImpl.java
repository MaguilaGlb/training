package com.globant.maguila.vertx.poc.service;

import java.util.Arrays;
import java.util.List;

import com.globant.maguila.vertx.poc.User;

public class UserDbRepositoryImpl implements UserRepository{

	@Override
	public User findUserById(Long id) {
		return new User(1l, "tincho!");
	}

	@Override
	public List<User> findAllUsers() {
		return Arrays.asList(new User[]{new User(1l, "martin"), new User(2l, "jose")});
	}

}
