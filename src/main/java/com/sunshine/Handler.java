package com.sunshine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		LOG.info("received: the request");
		String email = request.getPathParameters().get("email");
		List<User> users = new ArrayList<>();

		if(email.equals("123@gmail.com")) {
			User u1  = new User("123", "123@gmail.com","John Allien", "123123");
			users.add(u1);
		}
		else {
				User u2  = new User("124", "124@gmail.com","Joan Smith", "123123");
			users.add(u2);
		}
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String responseBody = objectMapper.writeValueAsString(users);
			response.setBody(responseBody);
		}
		catch(JsonProcessingException e) {
			LOG.error("Unable to marshal users array", e);

		}
		response.setStatusCode(200);
		return response;
	}
}
