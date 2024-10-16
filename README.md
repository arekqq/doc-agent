# Doc Agent

This is a Maven-based Java project that provides a REST API for file uploads and interactions using OpenAI's services.

## Prerequisites
- **Java 17** or higher
- **Maven 3.6** or higher
- An **OpenAI API key**

## 0. Running the Project

This project is built with Maven, which makes it easy to compile, test, and run. Below are the commands you will need:

### Compile the Project
To compile the project, run:
```
mvn clean compile
```

### Run the Project
To run the project, use:
```
mvn spring-boot:run
```

Alternatively, you can build a JAR file and run it directly:
```
mvn clean package
java -jar target/your-project-name.jar
```

## 1. OpenAI API Key Setup

This project requires an OpenAI API key to interact with its services. You must set this key as an environment variable before running the application.

### Setting the Environment Variable
For Linux/macOS:
```
export OPENAI_API_KEY=your_openai_api_key_here
```

For Windows:
```
set OPENAI_API_KEY=your_openai_api_key_here
```

Make sure to replace `your_openai_api_key_here` with your actual OpenAI API key.

## 2. File Upload Endpoint

To upload a file, you need to use the `/upload` endpoint with a `MultipartFile`. Make sure to attach the file to your request as a `file` parameter.

### Example Request
- **Endpoint**: `/upload`
- **Method**: `POST`
- **File Parameter**: `file`

### Response
The server will return a JSON object with the status and a unique file identifier:
```
{
"status": "SUCCESS",
"id": "08d2e8c3-17db-4691-bdc2-d28687cb6216"
}
```

## 3. Chat with Document Endpoint

To interact with the uploaded file, use the `/chat` endpoint. You will need to provide the `documentId` (obtained from the `/upload` response) and your `question` in the request body.

### Example Request
- **Endpoint**: `/chat`
- **Method**: `POST`
- **Request Body** (JSON):
  ```
  {
  "documentId": "08d2e8c3-17db-4691-bdc2-d28687cb6216",
  "question": "What is the period when I can cancel booking?"
  }
  ```

### Example Response
The server will return the answer to your question in the following format:
```
{
"answer": "You can cancel a booking up to 7 days prior to the start of the booking period. However, if the booking period is less than 3 days, cancellations are not permitted."
}
```

## Additional Notes

- Ensure your API key is valid, as an invalid key will cause the requests to fail.
- Make sure the file you are uploading is in a supported format. (pdf or txt currently)
- Follow standard REST API practices when making requests to the server.
