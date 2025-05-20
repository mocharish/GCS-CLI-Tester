package tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This test verifies that a signed URL can be successfully generated for a GCS object
 * and that the URL allows access to the file as expected (HTTP 200 response).
 */
public class SignUrlTest extends BaseTest {

    /**
     * Uploads a test file to the bucket before executing the signed URL test.
     */
    @BeforeClass
    public void uploadFileForSignUrl() {
        System.out.println("=== SignUrl Test Setup: Uploading unique file ===");
        try {
            // Upload the test file to the GCS bucket
            String output = runCommand(GCLOUD_PATH, "storage", "cp", TEST_FILE_NAME, TEST_BUCKET + "/");
            System.out.println("Upload Output:\n" + output);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file for sign-url test: " + e.getMessage());
        }
    }

    /**
     * Tests the generation and accessibility of a signed URL for the uploaded GCS object.
     */
    @Test
    public void testSignUrl() {
        System.out.println("=== Sign URL Test Started ===");

        String objectPath = TEST_BUCKET + "/" + TEST_FILE_NAME;

        // Prepare the gcloud command to generate a signed URL valid for 15 minutes
        ProcessBuilder builder = new ProcessBuilder(
            GCLOUD_PATH, "storage", "sign-url", objectPath, "--duration=15m"
        );

        String signedUrl = null;

        try {
            // Start the gcloud process to generate the signed URL
            System.out.println("Running: " + String.join(" ", builder.command()));
            Process process = builder.start();

            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;

            // Read STDOUT for signed URL
            System.out.println("--- STDOUT ---");
            while ((line = stdOut.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("signed_url: ")) {
                    signedUrl = line.substring("signed_url: ".length()).trim();
                }
            }

            // Read STDERR in case of errors
            System.out.println("--- STDERR ---");
            while ((line = stdErr.readLine()) != null) {
                System.err.println(line);
            }

            // Ensure the process exited successfully
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new AssertionError("Sign URL command failed with exit code: " + exitCode);
            }

            // Check that a valid signed URL was obtained
            if (signedUrl == null || signedUrl.isEmpty()) {
                throw new AssertionError("Signed URL not found in gcloud output.");
            }

            System.out.println("Extracted Signed URL: " + signedUrl);

            // Test HTTP access to the signed URL
            HttpURLConnection connection = (HttpURLConnection) new URL(signedUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10_000); // 10 seconds timeout
            connection.setReadTimeout(10_000);

            int responseCode = connection.getResponseCode();
            System.out.println("HTTP GET Response Code: " + responseCode);

            // Expect a 200 OK response for a valid signed URL
            if (responseCode != 200) {
                throw new AssertionError("Signed URL not accessible. HTTP code: " + responseCode);
            }

            System.out.println("Signed URL is accessible and returned HTTP 200 OK.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Sign URL test failed: " + e.getMessage());
        }

        System.out.println("=== Sign URL Test Finished ===");
    }

    /**
     * Cleans up by removing the uploaded file from the bucket after the test completes.
     */
    @AfterClass
    public void cleanup() {
        System.out.println("=== Sign URL Test Cleanup: Removing uploaded file ===");
        try {
            // Remove the test file from the bucket
            runCommand(GCLOUD_PATH, "storage", "rm", TEST_BUCKET + "/" + TEST_FILE_NAME);
            System.out.println("Deleted file from bucket: " + TEST_FILE_NAME);
        } catch (Exception e) {
            System.err.println("Failed to delete file from bucket: " + e.getMessage());
        }
    }
}
