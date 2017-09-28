package com.globant.maguila.vertx.poc.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class UserServiceImpl implements UserService {

	@Override
	public UserService getUser(Long userId, Handler<AsyncResult<JsonObject>> handler) {
		
		handler.handle(Future.succeededFuture(new JsonObject().put("value", "name from service!")));
		
		return this;
	}

}
