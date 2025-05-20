package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UploadTest extends BaseTest {

    @Test
    public void testUploadFileToBucket() {
        System.out.println("=== 🚀 Upload Test Started ===");

        try {
            uploadFileToBucket(uniqueTestFileName, uniqueBucketFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Upload failed: " + e.getMessage());
        }

        System.out.println("=== ✅ Upload Test Finished ===");
    }
}
