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
import java.util.*;

public class SaveUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOG = LogManager.getLogger(SaveUserHandler.class);
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		LOG.info("received: the request");
		String email = request.getPathParameters().get("email");

//		String userDetails = request.getPathParameters().get("email");
		String requestBody = request.getBody();
		List<User> users = new ArrayList<>();

		LOG.debug("will create user: "+ email);

		ObjectMapper objMapper = new ObjectMapper();
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);

		Map<String, String> headers = new HashMap<>();
		headers.put("Access-Control-Allow-Origin", "*");
		response.setHeaders(headers);

		try {
			User u = objMapper.readValue(requestBody,User.class);
			response.setBody("User created");
			LOG.debug("saved user: " + u.getUserName() + " " + u.getEmail());
			Class.forName("com.mysql.jdbc.Driver");
			User user;

			connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s",
					System.getenv("DB_HOST"),
					System.getenv("DB_NAME"),
					System.getenv("DB_USER"),
					System.getenv("DB_PASSWORD")));

			preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (?, ?, ?)");
			preparedStatement.setString(1, UUID.randomUUID().toString());
			preparedStatement.setString(2, u.getEmail());
			preparedStatement.setString(3, u.getUserName());
			preparedStatement.execute();

			preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				user = new User(resultSet.getString("id"),
						resultSet.getString("email"),
						resultSet.getString("userName"));
				users.add(user);
			}
			response.setBody("User selected back after inserted");
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
