## CI/CD: Build and Push Docker Image with GitHub Actions

This project includes a sample GitHub Actions workflow to build and push the Docker image to DockerHub on every push to `main`.

1. Set up your DockerHub credentials as GitHub repository secrets:
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`
2. On push to `main`, the workflow in `.github/workflows/docker-image.yml` will build and push the image tagged as `wiz-khalubernetes:latest`.

## Infrastructure: Provision MongoDB EC2 Instance with Terraform

Sample Terraform configuration is provided in the `terraform/` folder to create an Amazon EC2 instance and install MongoDB.



# Wiz Khalubernetes

A full-stack Spring Boot + React app for sharing Wiz Khalifa quotes, designed for cloud-native deployment and flexible database options.

---

## 1. What the App Is
- **Backend:** Spring Boot (Java 17)
- **Frontend:** React + TypeScript
- **Default DB:** Embedded H2 (local/dev)
- **Production DB:** MongoDB (remote, e.g., EC2)
- **Metrics:** Prometheus endpoint
- **Deployment:** Docker, Kubernetes, GitHub Actions

---

## 2. How to Build Locally (No Docker)

### Backend (Spring Boot)
```sh
cd backend
mvn clean package
java -jar target/wiz-khalubernetes-backend-0.0.1-SNAPSHOT.jar
```
- Default: Uses embedded H2 DB
- To use MongoDB, set `spring.data.mongodb.uri` in `src/main/resources/application.properties`

### Frontend (React)
```sh
cd frontend
npm install
npm start
```
- Runs at [http://localhost:3000](http://localhost:3000)

---

## 3. How to Build with Docker on a MacBook

### Build Docker Image
```sh
docker build -t wiz-khalubernetes .
```
- For amd64: `docker buildx build --platform linux/amd64 -t wiz-khalubernetes .`
- No cache: `docker buildx build --no-cache --platform linux/amd64 -t wiz-khalubernetes .`
- Prune images: `docker image prune -f && docker builder prune -f`


### Run Container
```sh
# For local H2 (default, fast startup)
docker run -p 80:80 -p 8080:8080 wiz-khalubernetes
# or explicitly
# docker run -p 80:80 -p 8080:8080 -e REMOTE_DB=false wiz-khalubernetes

# For remote MongoDB
docker run -p 80:80 -p 8080:8080 -e REMOTE_DB=true -e MONGODB_URI="mongodb://<username>:<password>@<host>:27017/<database>?authSource=admin" wiz-khalubernetes
```
- Access frontend: [http://localhost](http://localhost) (served by nginx on port 80)
- Access backend API: [http://localhost:8080](http://localhost:8080)

> **Note:** If you only map port 8080, the frontend will not be available. Always map port 80 for the UI.
> **Default behavior:** If REMOTE_DB is not set, the app uses local H2 for fastest startup.

---

## 4. Changing the Connection String (Local/Remote MongoDB)

- **Default:** Embedded H2 DB (no config needed)
- **Remote MongoDB:**
   - In `backend/src/main/resources/application.properties`, uncomment the line:
      ```properties
      spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/dummy}
      # Uncomment this property to use MongoDB (remote or local). Leave commented to use embedded H2 for local/dev.
      ```
   - Or set as environment variable:
      ```sh
      export SPRING_DATA_MONGODB_URI="mongodb://<username>:<password>@<host>:27017/<database>?authSource=admin"
      ```
   - Example for EC2:
      ```properties
      spring.data.mongodb.uri=mongodb://admin:password@ec2-xx-xx-xx-xx.compute.amazonaws.com:27017/wizquotes?authSource=admin
      ```

---

## 5. GitHub Actions Used to Build and Create the Mongo Instance



- **CI/CD:**
   - `.github/workflows/docker-image.yml` builds and pushes Docker images to DockerHub on every push to `main`.
   - Secrets required: `DOCKER_USERNAME`, `DOCKER_PASSWORD`


### Switching Between Local DB (H2) and Remote MongoDB
- Use the `REMOTE_DB` environment variable in `deployment.yaml`:
   - `REMOTE_DB: "false"` (default) uses embedded H2 (no external DB required)
   - `REMOTE_DB: "true"` uses remote MongoDB (set `MONGODB_URI` accordingly)
- To switch, edit `deployment.yaml` and redeploy:
   ```sh
   kubectl apply -f deployment.yaml
   ```
- Use Kubernetes secrets for sensitive values

---

## 7. How to Access wizexercise.txt Locally in Docker and via Remote Kubernetes

### In Docker Container
```sh
docker ps  # Get container ID
docker exec -it <container_id> sh
cat wizexercise.txt
```

### In Kubernetes Pod
```sh
kubectl get pods  # Get pod name
kubectl exec -it <pod_name> -- sh
cat wizexercise.txt
```

This file is included in the container for exercise/demo purposes.

---

## 8. API Endpoints
| Endpoint                      | Method | Description                                 |
|-------------------------------|--------|---------------------------------------------|
| `/api/quotes`                 | POST   | Submit a new Wiz Khalifa quote              |
| `/api/quotes/latest`          | GET    | Get the latest quote                        |
| `/api/nodeinfo`               | GET    | Get node/system/application info            |
| `/api/dbstatus`               | GET    | Get current DB connection status/type       |
| `/actuator/prometheus`        | GET    | Prometheus metrics endpoint                 |

---



## Development Notes
- Backend: Spring Boot (Java 17)
- Frontend: React + TypeScript
- MongoDB: Used for persistence
- Prometheus: Metrics exposed at `/actuator/prometheus`

---

Feel free to customize and extend Wiz Khalubernetes!
