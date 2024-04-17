package utils;

public class AWSUploader {
    private static final String BUILD_REPORTS_GATLING = "build/reports/gatling";

    public static void main(String[] args) {
        AmazonS3Upload amazonS3Upload = new AmazonS3Upload();
        amazonS3Upload.uploadFilesInDiretory(BUILD_REPORTS_GATLING);
    }
}