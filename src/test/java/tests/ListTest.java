package tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class tests the ability to list files in a GCS (Google Cloud Storage) bucket
 * and verifies that a previously uploaded file appears in the listing.
 */
public class ListTest extends BaseTest {

    /**
     * This method runs before the test to ensure a known file is uploaded to the bucket,
     * which we will later look for in the listing output.
     */
    @BeforeClass(alwaysRun = true)
    public void uploadFileForListing() {
        System.out.println("=== List Test Setup: Uploading unique file ===");
        try {
            // Upload a uniquely named file to the test bucket
            uploadFileToBucket(uniqueTestFileName, uniqueBucketFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file for list test: " + e.getMessage());
        }
    }

    /**
     * The main test that checks if the uploaded file appears in the bucket listing.
     */
    @Test
    public void testList() {
        System.out.println("=== List Test Started ===");
        try {
            // Execute gcloud command to list contents of the test bucket
            String output = runCommand(GCLOUD_PATH, "storage", "ls", TEST_BUCKET + "/");

            // Print the output of the command for visibility
            System.out.println("--- gcloud ls output ---");
            System.out.println(output);

            // Verify that the output contains the uploaded file name
            Assert.assertTrue(output.contains(uniqueTestFileName),
                "Bucket listing does not contain the uploaded file '" + uniqueTestFileName + "'.");
        } catch (Exception e) {
            // If anything goes wrong, fail the test with the exception message
            e.printStackTrace();
            Assert.fail("List test failed: " + e.getMessage());
        }
        System.out.println("=== ✅ List Test Finished ===");
    }

    /**
     * This method runs after the test to clean up the uploaded file from the bucket.
     */
    @AfterClass(alwaysRun = true)
    public void cleanup() {
        System.out.println("=== List Test Cleanup: Removing uploaded file ===");
        try {
            // Remove the test file from the GCS bucket after the test
            runCommand(GCLOUD_PATH, "storage", "rm", uniqueBucketFilePath);
            System.out.println("Deleted file from bucket: " + uniqueTestFileName);
        } catch (Exception e) {
            System.err.println("Failed to delete file from bucket: " + e.getMessage());
        }
    }
}
