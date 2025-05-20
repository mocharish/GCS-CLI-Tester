package tests;

import org.testng.annotations.BeforeClass;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class BaseTest {

    protected final String GCLOUD_PATH = getGcloudCommand();
    protected final String TEST_BUCKET = "gs://test-bucket-mend";

    protected final String TEST_FILE_NAME = "testfile.txt";
    protected final String DOWNLOADED_FILE = "downloaded.txt";

    // Create a unique suffix per run to avoid collisions
    protected final String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
    protected final String uniqueTestFileName = TEST_FILE_NAME.replace(".txt", "-" + uniqueSuffix + ".txt");
    protected final String uniqueDownloadedFile = "downloaded-" + uniqueSuffix + ".txt";
    protected final String uniqueBucketFilePath = TEST_BUCKET + "/" + uniqueTestFileName;

    // Customizable test file content
    protected final String TEST_FILE_CONTENT = "Sample test content for upload/download testing.";

    @BeforeClass(alwaysRun = true)
    public void setup() throws IOException {
        printGcloudVersion();
        createLocalTestFileIfMissing(uniqueTestFileName, TEST_FILE_CONTENT);
    }

    protected void uploadFileToBucket(String localFile, String bucketPath) throws IOException, InterruptedException {
        System.out.println("⬆ Uploading file " + localFile + " to " + bucketPath);
        String output = runCommand(GCLOUD_PATH, "storage", "cp", localFile, bucketPath);
        System.out.println("Upload output:\n" + output);

        if (!output.contains(localFile)) {
            throw new RuntimeException("Upload command output does not confirm file upload.");
        }
    }

    protected static String getGcloudCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:\\Users\\Mo\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\gcloud.cmd";
        } else {
            return "gcloud";
        }
    }

    protected void printGcloudVersion() {
        System.out.println(" Checking gcloud version...");
        try {
            String output = runCommand(GCLOUD_PATH, "--version");
            System.out.println(output);
        } catch (Exception e) {
            System.err.println("Failed to get gcloud version: " + e.getMessage());
        }
    }

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

    protected void createLocalTestFileIfMissing(String filename, String content) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            Files.writeString(path, content);
            System.out.println(" Created local test file: " + filename);
        }
    }

    protected void deleteFileIfExists(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println(deleted ? " Deleted: " + filename : " Failed to delete: " + filename);
        }
    }
}
