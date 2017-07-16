package com.globant.maguila.misc;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import io.vertx.core.json.JsonObject;

@RunWith(BlockJUnit4ClassRunner.class)
public class Misc {
	
	

	@Before
	public void setup(){
		System.out.println("setup");
		
	}
	
	@After
	public void end(){
		System.out.println("end");
		JsonObject configObject = new JsonObject();
		UUID.randomUUID();
		throw new UnsupportedOperationException();
	}
	
	@Test
	public void test() throws Exception{
		JsonObject userData = new JsonObject().
				put("sessionKey", UUID.randomUUID().toString()).
				put("userInfo", new JsonObject().
						put("userName", "LatamBasic").
						put("attributes", new JsonObject().
								put("isFree", true).
								put("isBasic", true).
								put("isSubscribed", false).
								put("isSportAfa", false)));
		}



}
