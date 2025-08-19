## CI/CD: Build and Push Docker Image with GitHub Actions

This project includes a sample GitHub Actions workflow to build and push the Docker image to DockerHub on every push to `main`.

1. Set up your DockerHub credentials as GitHub repository secrets:
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`
2. On push to `main`, the workflow in `.github/workflows/docker-image.yml` will build and push the image tagged as `wiz-khalubernetes:latest`.

## Infrastructure: Provision MongoDB EC2 Instance with Terraform

Sample Terraform configuration is provided in the `terraform/` folder to create an Amazon EC2 instance and install MongoDB.

1. Install [Terraform](https://www.terraform.io/downloads.html).
2. Configure your AWS credentials:
    - You can set them in `terraform/terraform.tfvars` as `aws_access_key` and `aws_secret_key` (not recommended for production).
    - Or, set them as environment variables before running Terraform:
       ```sh
       export AWS_ACCESS_KEY_ID=your-access-key
       export AWS_SECRET_ACCESS_KEY=your-secret-key
       export AWS_DEFAULT_REGION=us-east-1
       ```
3. Edit `terraform/main.tf` to set your key name, region, and AMI, or use `terraform.tfvars`.
4. Initialize and apply Terraform:
   ```sh
   cd terraform
   terraform init
   terraform apply
   ```
5. After provisioning, use the output `ec2_public_ip` to connect your app to MongoDB:
   ```
   mongodb://<username>:<password>@<ec2_public_ip>:27017/<database>?authSource=admin
   ```
## API Endpoints (on EKS)

Assuming your app is exposed at `http://<EXTERNAL-IP>` via the LoadBalancer service:

| Endpoint                      | Method | Description                                 | Example URL                                 |
|-------------------------------|--------|---------------------------------------------|---------------------------------------------|
| `/api/quotes`                 | POST   | Submit a new Wiz Khalifa quote              | `http://<EXTERNAL-IP>/api/quotes`           |
| `/api/quotes/latest`          | GET    | Get the latest quote                        | `http://<EXTERNAL-IP>/api/quotes/latest`    |
| `/api/nodeinfo`               | GET    | Get node/system/application info            | `http://<EXTERNAL-IP>/api/nodeinfo`         |
| `/actuator/prometheus`        | GET    | Prometheus metrics endpoint                 | `http://<EXTERNAL-IP>/actuator/prometheus`  |

Replace `<EXTERNAL-IP>` with the public IP from your LoadBalancer service.
# Wiz Khalubernetes

A full-stack Spring Boot + React application for sharing your favorite Wiz Khalifa quotes, designed for Kubernetes deployment.

## Features
- Enter and display Wiz Khalifa quotes
- Stores quote, timestamp, IP address, and quote number in MongoDB
- Node/application info displayed on the page
- Prometheus metrics endpoint
- Ready for Kubernetes deployment

## Prerequisites
- Docker
- Access to a remote MongoDB instance
- (Optional) Kubernetes cluster for deployment

## Running with Docker (nginx + Spring Boot)

After building your Docker image, run:

```
docker run -p 80:80 -p 8080:8080 wiz-khalubernetes:latest
```

- Access the React UI at: [http://localhost/](http://localhost/)
- Access the backend API at: [http://localhost:8080/](http://localhost:8080/)


## Accessing the App in Your Browser

Once your Docker container is running, open your browser and go to:

- **React UI:** [http://localhost/](http://localhost/)
- **Backend API:** [http://localhost:8080/](http://localhost:8080/)

The React UI is served on port 80 by nginx, and the backend API is available on port 8080.


## Local Development & Testing


### 1. Build the Docker Image


#### For amd64 architecture (recommended for most cloud platforms)
```
docker buildx build --platform linux/amd64 -t wiz-khalubernetes .
```

#### Force Docker Build Without Cache
To rebuild all layers from scratch and avoid using cached files, use:
```
docker buildx build --no-cache --platform linux/amd64 -t wiz-khalubernetes .
```

#### For your local architecture
```
docker build -t wiz-khalubernetes .
```

### (Optional) Clean Up Docker Images and Build Cache
After building images, you can free up disk space by removing unused images and build cache:
```
docker image prune -f
docker builder prune -f
```
This will remove dangling images and build cache. Use with caution if you have other images you want to keep.


### 2. Run the Container

#### With an active MongoDB cluster (EC2 example)
Replace `<ec2-public-ip>`, `<username>`, `<password>`, and `<database>` with your actual values:
```
docker run -p 8080:8080 \
   -e MONGO_USER="yourMongoUser" \
   -e MONGO_PASSWORD="yourMongoPassword" \
   -e MONGODB_URI="mongodb://yourMongoUser:yourMongoPassword@<ec2-public-ip>:27017/<database>?authSource=admin" \
   wiz-khalubernetes
```


#### Without a MongoDB cluster (quick test)
You can run the app without a MongoDB connection. The app will load, but quote submission will show a friendly error message in the UI indicating the database is unavailable.
```
docker run -p 8080:8080 wiz-khalubernetes
```

#### Accessing the App in Your Browser
After running the container, open your browser and go to:

```
http://localhost:8080
```
You should see the Wiz Khalubernetes app homepage. If MongoDB is not available, quote submission will show a friendly error message.


### 2a. Local Frontend (NPM) Testing
To test the React frontend locally (without Docker):
1. Make sure your TypeScript version is compatible with react-scripts (TypeScript 4.x recommended).
2. Run the following commands:
```
cd frontend
npm install
npm start
```
If you see a TypeScript version error, run:
```
npm install typescript@4.9.5
npm install
```
This will start the frontend on [http://localhost:3000](http://localhost:3000). You can develop and test UI changes here before building the Docker image.

#### Without a MongoDB cluster (quick test)
You can run the app without a MongoDB connection. The app will load, but quote submission will show a friendly error message in the UI indicating the database is unavailable.
```
docker run -p 8080:8080 wiz-khalubernetes
```

### 3. Access the Application
- Open your browser at [http://localhost:8080](http://localhost:8080)
- Submit and view Wiz Khalifa quotes (if MongoDB is available)
- If MongoDB is not available, you will see a graceful error message when submitting quotes.

### 4. Prometheus Metrics
- Visit [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

## Kubernetes Deployment

1. Edit `deployment.yaml`:
    - Set the `image` field to your built/pushed Docker image
    - Set the `MONGODB_URI` environment variable in `deployment.yaml` to your remote MongoDB connection string, including credentials. For example:
       `mongodb://<username>:<password>@<ec2-public-ip>:27017/<database>?authSource=admin`
    - The backend reads this value from the environment; do not hardcode credentials in `application.properties`.
    - (Optional) Use Kubernetes secrets for sensitive values


2. Apply the deployment:
```
kubectl apply -f deployment.yaml
```

## Accessing the App on EKS (AWS Elastic Kubernetes Service)

After deploying, a LoadBalancer service will be created. To get the external URL:

1. Get the service details:
   ```
   kubectl get svc wiz-khalubernetes-lb
   ```
2. Look for the `EXTERNAL-IP` column. This is the public address of your app.
3. Access the app in your browser:
   ```
   http://<EXTERNAL-IP>
   ```
4. Prometheus metrics are available at:
   ```
   http://<EXTERNAL-IP>/actuator/prometheus
   ```


## Accessing wizexercise.txt in a Running Container

### Docker
To access `wizexercise.txt` inside a running Docker container:

1. List running containers to get the container ID:
   ```
   docker ps
   ```
2. Exec into the container shell:
   ```
   docker exec -it <container_id> sh
   ```
3. View the file:
   ```
   cat wizexercise.txt
   ```

### Kubernetes
To access `wizexercise.txt` in a running Kubernetes pod:

1. List pods to get the pod name:
   ```
   kubectl get pods
   ```
2. Exec into the pod shell:
   ```
   kubectl exec -it <pod_name> -- sh
   ```
3. View the file:
   ```
   cat wizexercise.txt
   ```

This file is included in the container for exercise/demo purposes.

## Development Notes
- Backend: Spring Boot (Java 17)
- Frontend: React + TypeScript
- MongoDB: Used for persistence
- Prometheus: Metrics exposed at `/actuator/prometheus`

---

Feel free to customize and extend Wiz Khalubernetes!
