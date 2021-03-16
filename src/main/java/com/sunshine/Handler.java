package com.sunshine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		User u1  = new User("123", "123@gmail.com","John Allien", "123123");
		User u2  = new User("124", "124@gmail.com","Joan Smith", "123123");
		List<User> users = new ArrayList<>();
		users.add(u1);
		users.add(u2);

		Response responseBody = new Response("Hello my to my first lambda function", input);
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(users)
				.build();
	}
}
