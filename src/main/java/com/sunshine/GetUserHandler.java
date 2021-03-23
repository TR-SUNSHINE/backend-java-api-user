package com.sunshine;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunshine.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOG = LogManager.getLogger(GetUserHandler.class);

	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		LOG.info("received: the request");

		String email = request.getPathParameters().get("email");
		List<User> users = new ArrayList<>();
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);
		User user;

		Map<String, String> headers = new HashMap<>();
		headers.put("Access-Control-Allow-Origin", "*");
		response.setHeaders(headers);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			LOG.debug(String.format("Connecting to database on %s",System.getenv("DB_HOST")));
			connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s" +
							"&password=%s",
					System.getenv("DB_HOST"),
					System.getenv("DB_NAME"),
					System.getenv("DB_USER"),
					System.getenv("DB_PASSWORD")));

			preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User(resultSet.getString("id"),
						resultSet.getString("email"),
						resultSet.getString("userName"));
				users.add(user);
			}
		}

		catch (Exception e) {
			LOG.error(String.format("Unable to query database for email %s",email),e);
		}
		finally {
			closeConnection();
		}


		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String responseBody = objectMapper.writeValueAsString(users);
			response.setBody(responseBody);
		}
		catch(JsonProcessingException e) {
			LOG.error("Unable to marshall tasks array", e);
		}

		return response;

	}
		private void closeConnection() {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (preparedStatement != null) {
					preparedStatement.close();
				}

				if (connection != null) {
					connection.close();
				}
			}
			catch (Exception e) {
				LOG.error("Unable to close connections to MySQL - {}", e.getMessage());
			}
		}

	}
