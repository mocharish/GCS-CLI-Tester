package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This test verifies that a file can be successfully uploaded to the specified GCS bucket.
 */
public class UploadTest extends BaseTest {

    /**
     * Test method for uploading a file to a Google Cloud Storage bucket.
     * 
     * This test uses a unique file name and path defined in the BaseTest class.
     * It asserts that the upload operation completes without throwing any exceptions.
     */
    @Test
    public void testUploadFileToBucket() {
        System.out.println("=== 🚀 Upload Test Started ===");

        try {
            // Attempt to upload the file using utility method defined in BaseTest
            uploadFileToBucket(uniqueTestFileName, uniqueBucketFilePath);
        } catch (Exception e) {
            // If an exception occurs during upload, print the stack trace and fail the test
            e.printStackTrace();
            Assert.fail("Upload failed: " + e.getMessage());
        }

        System.out.println("=== ✅ Upload Test Finished ===");
    }
}
