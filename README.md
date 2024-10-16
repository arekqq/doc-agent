# Doc Agent

This is a Maven-based Java project that provides a REST API for file uploads and interactions using OpenAI's services.

## Prerequisites
- **Java 17** or higher (for the Maven-based option)
- **Maven 3.9.9** or higher (for the Maven-based option)
- **Docker** and **Docker Compose** (for the Docker-based option)
- An **OpenAI API key**

## Setting up the OpenAI API Key

This project requires an OpenAI API key to interact with its services. You must set this key as an environment variable before running the application, regardless of the method you choose to run the app.

### Setting the Environment Variable

#### macOS / Linux
In your terminal session, use:
```
export OPENAI_API_KEY=your_openai_api_key_here
```

To make this persistent, you can add it to your shell configuration file (`~/.bash_profile`, `~/.zshrc`, etc.).

#### Windows

For PowerShell:
```
$env:OPENAI_API_KEY="your_openai_api_key_here"
```

For Command Prompt:
```
set OPENAI_API_KEY=your_openai_api_key_here
```

You can also set the environment variable permanently in Windows via the system environment variables menu.

Make sure to replace `your_openai_api_key_here` with your actual OpenAI API key.

---

## Running the Project

There are two alternative ways to run the project: with Docker or with Maven.

### Option 1: Running with Docker

This method uses Docker and Docker Compose to containerize and run the application.

#### 1. Clone the Repository

```
git clone https://github.com/your-username/your-repository.git
cd your-repository
```

#### 2. Start the Application

Once the `OPENAI_API_KEY` is set, you can start the application using Docker Compose:

```
docker-compose up --build
```

This command will build and run the Docker containers defined in the `docker-compose.yml` file.

#### 3. Access the Application

Once the containers are running, you can access the application via the provided URL (e.g., `http://localhost:8000`).

#### 4. Stopping the Application

To stop the application, use:

```
docker-compose down
```

This will stop and remove the containers, but the data volumes will be retained. If you want to remove all data, you can use:

```
docker-compose down -v
```

---

### Option 2: Running with Maven

If you prefer to run the project using Maven directly, follow these steps:

#### 1. Compile the Project

To compile the project, run:

```
mvn clean compile
```

#### 2. Run the Project

To run the project, use:

```
mvn spring-boot:run
```

Alternatively, you can build a JAR file and run it directly:

```
mvn clean package
java -jar target/your-project-name.jar
```

---

## REST API Endpoints

### 1. File Upload Endpoint

To upload a file, you need to use the `/upload` endpoint with a `MultipartFile`. Make sure to attach the file to your request as a `file` parameter.

#### Example Request
- **Endpoint**: `/upload`
- **Method**: `POST`
- **File Parameter**: `file`

#### Response
The server will return a JSON object with the status and a unique file identifier:

```
{
"status": "SUCCESS",
"id": "08d2e8c3-17db-4691-bdc2-d28687cb6216"
}
```

### 2. Chat with Document Endpoint

To interact with the uploaded file, use the `/chat` endpoint. You will need to provide the `documentId` (obtained from the `/upload` response) and your `question` in the request body.

#### Example Request
- **Endpoint**: `/chat`
- **Method**: `POST`
- **Request Body** (JSON):

```
{
"documentId": "08d2e8c3-17db-4691-bdc2-d28687cb6216",
"question": "What is the period when I can cancel booking?"
}
```

#### Example Response
The server will return the answer to your question in the following format:

```
{
"answer": "You can cancel a booking up to 7 days prior to the start of the booking period. However, if the booking period is less than 3 days, cancellations are not permitted."
}
```

---

## Additional Notes

- Ensure your API key is valid, as an invalid key will cause the requests to fail.
- Make sure the file you are uploading is in a supported format (pdf or txt currently).
- Follow standard REST API practices when making requests to the server.
