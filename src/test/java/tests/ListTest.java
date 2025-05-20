package tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ListTest extends BaseTest {

    @BeforeClass(alwaysRun = true)
    public void uploadFileForListing() {
        System.out.println("=== List Test Setup: Uploading unique file ===");
        try {
            uploadFileToBucket(uniqueTestFileName, uniqueBucketFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file for list test: " + e.getMessage());
        }
    }

    @Test
    public void testList() {
        System.out.println("=== List Test Started ===");
        try {
            String output = runCommand(GCLOUD_PATH, "storage", "ls", TEST_BUCKET + "/");
            System.out.println("--- gcloud ls output ---");
            System.out.println(output);
            Assert.assertTrue(output.contains(uniqueTestFileName),
                "Bucket listing does not contain the uploaded file '" + uniqueTestFileName + "'.");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("List test failed: " + e.getMessage());
        }
        System.out.println("=== ✅ List Test Finished ===");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        System.out.println("=== List Test Cleanup: Removing uploaded file ===");
        try {
            runCommand(GCLOUD_PATH, "storage", "rm", uniqueBucketFilePath);
            System.out.println("Deleted file from bucket: " + uniqueTestFileName);
        } catch (Exception e) {
            System.err.println("Failed to delete file from bucket: " + e.getMessage());
        }
    }
}
