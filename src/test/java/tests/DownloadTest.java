package tests;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests the ability to download a file from a GCS (Google Cloud Storage) bucket
 * using the gcloud CLI and verifies the downloaded file exists and is non-empty.
 */

public class DownloadTest extends BaseTest {

    @Test
    public void testDownloadFileFromBucket() {
        System.out.println("===  Download Test Started ===");

        try {
            // Upload first to ensure file exists
            uploadFileToBucket(uniqueTestFileName, uniqueBucketFilePath);

            // Delete local downloaded file if it exists
            deleteFileIfExists(uniqueDownloadedFile);
            
            //Run the gcloud command to download the file from the bucket
            System.out.println("Downloading file from bucket...");
            String output = runCommand(GCLOUD_PATH, "storage", "cp", uniqueBucketFilePath, uniqueDownloadedFile);
            System.out.println("Download command output:\n" + output);

            // Check that downloaded file now exists
            File downloadedFile = new File(uniqueDownloadedFile);
            Assert.assertTrue(downloadedFile.exists(), "Downloaded file does not exist.");
            // Check that the file is not empty
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
