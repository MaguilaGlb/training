package com.globant.maguila.vertx.poc.service;

import com.google.inject.Inject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class UserServiceImpl implements UserService {
	private UserRepository repo;
	
	@Inject
	public UserServiceImpl (UserRepository repo){
		this.repo = repo;
	}
	

	@Override
	public UserService getUser(Long userId, Handler<AsyncResult<JsonObject>> handler) {
		
		handler.handle(Future.succeededFuture(new JsonObject().put("value", repo.findUserById(userId).getName())));
		
		return this;
	}

}
