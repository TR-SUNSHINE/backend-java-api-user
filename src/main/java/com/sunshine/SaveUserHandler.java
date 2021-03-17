package com.sunshine;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunshine.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class SaveUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOG = LogManager.getLogger(SaveUserHandler.class);
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		LOG.info("received: the request");

		String userName = request.getPathParameters().get("userName");
		String email = request.getPathParameters().get("email1");
		LOG.debug("will create user: "+ userName + email);

		String requestBody = request.getBody();

		ObjectMapper objMapper = new ObjectMapper();
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);
		Map<String, String> headers = new HashMap<>();
		headers.put("Access-Control-Allow-Origin", "*");
		response.setHeaders(headers);


		try {
			User u = objMapper.readValue(requestBody,User.class);

			LOG.debug("Created user"+ userName + email);
			response.setBody("User created");

			Class.forName("com.mysql.jdbc.Driver");

			connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s",
					System.getenv("DB_HOST"),
					System.getenv("DB_NAME"),
					System.getenv("DB_USER"),
					System.getenv("DB_PASSWORD")));

			preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (?, ?, ?, ?)");
			preparedStatement.setString(1, UUID.randomUUID().toString());
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, userName);
			preparedStatement.setString(4, u.getPassword());
			preparedStatement.execute();
			connection.close();

		} catch (IOException e) {
			LOG.error("Unable to unmarshal JSON for adding a user", e);
		} catch (ClassNotFoundException e) {
			LOG.error("ClassNotFoundException", e);
		} catch (SQLException throwables) {
			LOG.error("SQL Exception", throwables);
		}
		finally {
			closeConnection();
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
