# Use Maven + JDK 17 image
FROM maven:3.9.6-eclipse-temurin-17

# Install gcloud CLI
RUN apt-get update && \
    apt-get install -y curl apt-transport-https gnupg && \
    echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" \
    | tee /etc/apt/sources.list.d/google-cloud-sdk.list && \
    curl https://packages.cloud.google.com/apt/doc/apt-key.gpg \
    | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add - && \
    apt-get update && apt-get install -y google-cloud-sdk && \
    gcloud version

WORKDIR /app

# Copy all source code and config
COPY . .

# Activate gcloud service account
RUN gcloud auth activate-service-account --key-file=credentials.json && \
    gcloud config set project gcs-cli-test-460213

# Run tests when container runs
CMD ["mvn", "test"]
