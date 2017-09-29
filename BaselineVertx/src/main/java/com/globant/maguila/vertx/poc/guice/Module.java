package com.globant.maguila.vertx.poc.guice;

import com.globant.maguila.vertx.poc.service.UserDbRepositoryImpl;
import com.globant.maguila.vertx.poc.service.UserRepository;
import com.globant.maguila.vertx.poc.service.UserService;
import com.globant.maguila.vertx.poc.service.UserServiceImpl;
import com.google.inject.AbstractModule;

public class Module extends AbstractModule {

	@Override
	protected void configure() {
		bind(UserService.class).to(UserServiceImpl.class);
		bind(UserRepository.class).to(UserDbRepositoryImpl.class);
	}

}
