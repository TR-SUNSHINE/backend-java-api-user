backend-java-api-reminder
A Java backend api created to serve the Sunshine frontend repository. 
The project uses the serverless framework to deploy the project to AWS & create AWS Lambda functions. 
The Lambda functions serve GET & POST requests for user interact with the Sunshine RDS database to retrieve and add from the user table.

To set up the project code:

Install the serverless cli on your machine: install serverless
Set up an AWS IAM user called 'serverless' with programmatic access set up IAM user
Copy the AWS key & secret or download the CSV file from the AWS IAM console

Credentials are saved locally in the aws credential file: ~/.aws/credentials

A config.dev.json file stores your database credentials in the following format: 
DB_HOST: <hostname> 
DB_NAME: <databasename> 
DB_USER: <database username> 
DB_PASSWORD: <database password>

To build & deploy:

mvn install
serverless deploy