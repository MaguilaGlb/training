package com.globant.maguila.vertx.poc.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

@VertxGen
@ProxyGen
public interface UserService {
	
	public static final String SERVICE_NAME = "UserService";
	
	@Fluent
	public UserService getUser(Long userId, Handler<AsyncResult<JsonObject>> handler);

}
