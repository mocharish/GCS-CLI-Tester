package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class DownloadTest extends BaseTest {

    @Test
    public void testDownloadFileFromBucket() {
        System.out.println("===  Download Test Started ===");

        try {
            // Upload first to ensure file exists
            uploadFileToBucket(uniqueTestFileName, uniqueBucketFilePath);

            // Delete local downloaded file if it exists
            deleteFileIfExists(uniqueDownloadedFile);

            System.out.println("Downloading file from bucket...");
            String output = runCommand(GCLOUD_PATH, "storage", "cp", uniqueBucketFilePath, uniqueDownloadedFile);
            System.out.println("Download command output:\n" + output);

            // Check that downloaded file now exists
            File downloadedFile = new File(uniqueDownloadedFile);
            Assert.assertTrue(downloadedFile.exists(), "Downloaded file does not exist.");

            long fileSize = downloadedFile.length();
            System.out.println("Downloaded file size: " + fileSize + " bytes");
            Assert.assertTrue(fileSize > 0, "Downloaded file is empty.");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Download test failed: " + e.getMessage());
        }

        System.out.println("=== ✅ Download Test Finished ===");
    }
}
