package utils;


import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AmazonS3Upload {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String bucketName = "my-bucket-name";
    private AmazonS3 s3;
    List<String> filesListInDir = new ArrayList<>();

    private static final String BUILD_TMP = "build/tmp/";
    private static final String S3_DATA_PATH_FORMAT = "yyyy/MM/dd/";

    private SimpleDateFormat reportPathFormat = new SimpleDateFormat(S3_DATA_PATH_FORMAT);

    public void uploadFilesInDiretory(String dirStr) {
        try {
            logger.info("Started sequence Upload");
            awsSetUp();
            File dir = new File(dirStr);
            String fileName = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss'.zip'").format(new Date());
            String zipDirName = BUILD_TMP + fileName;
            zipDirectory(dir, zipDirName);
            uploadFile(zipDirName);

        } catch (Exception e) {
            logger.severe(e.toString());
        } finally {
            logger.info("Sequence Upload has finished");
        }
    }

    private void awsSetUp() {
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider("saml"))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    private void uploadFile(String fileName) {
        try {
            logger.info("Uploading: " + fileName + " \nto S3 bucket: " + bucketName);
            String keyName = Paths.get(fileName).getFileName().toString();
            String objKey = reportPathFormat.format(new Date()) + keyName;
            s3.putObject(bucketName, objKey, new File(fileName));
            logger.info("Uploaded file: " + fileName);
        } catch (Exception e) {
            logger.severe("Error uploading file: " + fileName + "\n" + e);
        }
    }

    /**
     * This method zips the directory
     *
     * @param dir
     * @param zipDirName
     */
    private void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                logger.info("Zipping file: " + filePath);
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method populates all the files in a directory to a List
     *
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile())
                filesListInDir.add(file.getAbsolutePath());
            else
                populateFilesList(file);
        }
    }
}
