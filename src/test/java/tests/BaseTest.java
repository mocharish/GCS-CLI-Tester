package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.testng.annotations.BeforeClass;

public class BaseTest {

    // Path to the gcloud CLI executable depending on OS
    protected final String GCLOUD_PATH = getGcloudCommand();

     // GCS bucket to use for testing
    protected final String TEST_BUCKET = "gs://test-bucket-mend";

     // Base names for test files
    protected final String TEST_FILE_NAME = "testfile.txt";
    protected final String DOWNLOADED_FILE = "downloaded.txt";

    // Create a unique suffix per run to avoid collisions
    protected final String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
    protected final String uniqueTestFileName = TEST_FILE_NAME.replace(".txt", "-" + uniqueSuffix + ".txt");
    protected final String uniqueDownloadedFile = "downloaded-" + uniqueSuffix + ".txt";

      // Full GCS path to the test file
    protected final String uniqueBucketFilePath = TEST_BUCKET + "/" + uniqueTestFileName;

    // test file content
    protected final String TEST_FILE_CONTENT = "Sample test content for upload/download testing.";
    
    // Setup method that runs once before all tests
    @BeforeClass(alwaysRun = true)
    public void setup() throws IOException {
        printGcloudVersion();
        createLocalTestFileIfMissing(uniqueTestFileName, TEST_FILE_CONTENT);
    }

     /**
     * Uploads a local file to the specified GCS bucket path using the gcloud CLI.
     */
    protected void uploadFileToBucket(String localFile, String bucketPath) throws IOException, InterruptedException {
        System.out.println(" Uploading file " + localFile + " to " + bucketPath);
        String output = runCommand(GCLOUD_PATH, "storage", "cp", localFile, bucketPath);
        System.out.println("Upload output:\n" + output);

        if (!output.contains(localFile)) {
            throw new RuntimeException("Upload command output does not confirm file upload.");
        }
    }
    
    /**
    * Determines the correct gcloud command to run based on the operating system.
    */
    protected static String getGcloudCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:\\Users\\Mo\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\gcloud.cmd";
        } else {
            return "gcloud";
        }
    }

    /**
    * Prints the version of the installed gcloud CLI tool.
    */
    protected void printGcloudVersion() {
        System.out.println(" Checking gcloud version...");
        try {
            String output = runCommand(GCLOUD_PATH, "--version");
            System.out.println(output);
        } catch (Exception e) {
            System.err.println("Failed to get gcloud version: " + e.getMessage());
        }
    }
    
    /**
    * Runs a system command and returns the output. Retries up to 3 times on failure.
    */
    protected String runCommand(String... command) throws IOException, InterruptedException {
        int retries = 3;
        while (retries > 0) {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                retries--;
                if (retries == 0) {
                    throw new RuntimeException("Command failed with exit code " + exitCode + ":\n" + output);
                }
                System.err.println("Command failed, retrying... (" + retries + " retries left)");
                Thread.sleep(1000);
            }
        }
        throw new RuntimeException("Command failed after retries.");
    }

    /**
    * Creates a local text file with specified content if it doesn't already exist.
    */
    protected void createLocalTestFileIfMissing(String filename, String content) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            Files.writeString(path, content);
            System.out.println(" Created local test file: " + filename);
        }
    }
    

    /**
    * Deletes a file from local disk if it exists.
    */
    protected void deleteFileIfExists(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println(deleted ? " Deleted: " + filename : " Failed to delete: " + filename);
        }
    }
}
