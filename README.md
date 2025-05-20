# GCS Java Integration Tests

## Overview

This project is a Java-based application with automated tests that interact with Google Cloud Storage (GCS) using the `gcloud` CLI. It includes tests for uploading, downloading, listing, and signing URLs for files in a GCS bucket.

The main purpose of this project is to verify the integration and functionality of Google Cloud Storage operations programmatically and via the CLI.

---

## Features

- Automated Java tests run with Maven.
- Tests verify:
  - Uploading files to GCS buckets.
  - Downloading files from GCS.
  - Listing bucket contents.
  - Generating signed URLs for files.
- Uses Google Cloud SDK (`gcloud` CLI) installed inside Docker for testing.
- Runs tests in a fully isolated Docker container.
- Supports service account authentication using a credentials JSON file.

---

## Project Structure

├── Dockerfile # Docker setup to run tests with gcloud SDK
├── pom.xml # Maven project file with dependencies and plugins
├── testng.xml # TestNG suite definition
├── credentials.json # (Not committed) GCP service account key
└── src/
└── test/
└── java/
└── tests/ # Java test classes (TestNG-based)
├── UploadTest.java
├── DownloadTest.java
├── ListFilesTest.java
└── SignedUrlTest.java



---

## ▶️ Running Instructions

You can run the tests either **locally** or inside a **Docker container**.

### 🔧 Local Setup

1. Ensure you have:
   - Java 17+
   - Maven installed
   - GCP service account key (`credentials.json`) with permissions for the target bucket

2. Set the required environment variables (or export them):

```bash
export GOOGLE_APPLICATION_CREDENTIALS=path/to/credentials.json
export TEST_BUCKET_NAME=your-bucket-name
```



## 🐳 Docker Setup

### 1. Build the Docker image:

```bash
docker build -t gcs-tests .
```

### 2. Run the container:

```bash
docker run -e GOOGLE_APPLICATION_CREDENTIALS=/app/credentials.json \
           -e TEST_BUCKET_NAME=your-bucket-name \
           -v $PWD/credentials.json:/app/credentials.json \
           gcs-tests
```

# ✅ Expected Test Results
### When executed correctly, the output will be something like:

```yaml
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Each test class (Upload, Download, ListFiles, SignedUrl) will:

  -Interact with your configured GCS bucket.

  -Validate that the operations return expected results.

  -Fail gracefully if GCP credentials are missing or invalid.

# 🔍 Explanation of the Testing Process
### 1. UploadTest

-Uploads a test file to the GCS bucket.

-Asserts that the blob now exists in the bucket.

### 2. DownloadTest

Downloads an existing object from the bucket.

Verifies the file is correctly saved locally and optionally checks contents.

### 3. ListFilesTest

Lists all blobs in the specified GCS bucket.

Asserts the list is non-empty and contains expected filenames.

### 4. SignedUrlTest

Generates a signed URL for an object in the bucket.

Optionally makes a GET request to the signed URL and asserts a 200 OK response.

