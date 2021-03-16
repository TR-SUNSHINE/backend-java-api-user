package com.sunshine;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SaveUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOG = LogManager.getLogger(SaveUserHandler.class);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		LOG.info("received: the request");
		String email = request.getPathParameters().get("email");
		String requestBody = request.getBody();
		ObjectMapper objectMapper = new ObjectMapper();


		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);

		try {
			User u = objectMapper.readValue(requestBody,User.class);
			LOG.debug("Saved user"+ u.getUserName());
			response.setBody("User Saved");
		}
		catch (IOException e) {
			e.printStackTrace();
			LOG.error("Unable to marshal JSON for adding new user", email, e);

		}
		response.setStatusCode(200);
		return response;
	}
}
