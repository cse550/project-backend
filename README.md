## Installing and Running Docker

1. **Install Docker Desktop:**
   - Download Docker Desktop from the [official website](https://www.docker.com/products/docker-desktop).
   - Follow the on-screen instructions for your operating system to complete the installation.

2. **Verify Installation:**
   - Open a terminal window (or command prompt/PowerShell on Windows) and run the following commands to ensure Docker and Docker Compose were installed successfully:
    ```bash
    docker --version
    docker compose version
    ```

3. **Run Docker:**
   - Launch Docker Desktop from your Applications folder, Start menu, or desktop icon, depending on your operating system. You should see the Docker icon in your menu bar or system tray indicating that Docker is running.

### Optional: Using Package Managers

If you prefer using package managers, macOS users can use [Homebrew](https://brew.sh/) with the command `brew install --cask docker`, and Windows users can use [Chocolatey](https://chocolatey.org/) with the command `choco install docker-desktop`.

## Running the Application

Follow these steps to run the application using Docker Compose:

1. **Build the Spring Boot Application**:
   Before running the application with Docker Compose, you'll need to build the Spring Boot application and create a Docker image named `project-backend`. 

    ```bash
    docker build -t project-backend .
    ```

2. **Start the Services**:
   With the Docker image created, you can now use Docker Compose to start the services defined in the `docker-compose.yml` file.

    ```bash
    docker-compose up
    ```

   This command will start the MongoDB and Spring Boot application containers. Docker Compose will use the `project-backend` image you built in the previous step for the `spring-boot-app` service.

3. **Accessing the Application**:
   Once the services are up and running, you can access the Spring Boot application by navigating to [http://localhost:8080](http://localhost:8080) in your web browser.

4. **Stopping the Services**:
   When you are done, you can stop the services by running the following command in the same directory as your `docker-compose.yml` file:

    ```bash
    docker-compose down
    ```

5. **(Optional) Cleaning Up**:
   If you want to remove the Docker image you created, you can do so with the following command:

    ```bash
    docker rmi project-backend
    ```
# Setting up JaCoCo Code Coverage Reporting with Maven

JaCoCo is a Java Code Coverage library that helps measure and report code coverage in Java projects.
The project is currently set up to exclude any file in the error or model folders.

- [JaCoCo documentation](https://www.jacoco.org/jacoco/trunk/index.html) - Documentation for JaCoCo usage

- [Maven](https://maven.apache.org/download.cgi) - Before you begin, make sure you have maven installed:


### Generating a report
to generate a report run the following command
```bash
mvn clean verify jacoco:report
```
After running the command, you can view the code coverage reports by opening the generated HTML files in your web browser. 

The main report can be found at: ```target/site/jacoco/index.html```

## PIT Mutation Testing

PIT is a state-of-the-art tool for mutation testing in Java. Mutation testing is a technique to assess the quality of your unit tests by introducing small modifications (mutations) in your source code and then checking if the tests detect these changes. PIT helps in identifying untested portions of your code and improves the overall robustness of your tests.

### Generating PIT Reports

To generate a PIT report, you can use the following bash command:

```bash
mvn pitest:mutationCoverage pitest:report
```

after the analysis is complete, PIT generates reports in the ```target/pit-reports/index.html``` directory
These reports provide detailed insights into the code coverage and the effectiveness of the unit tests.

