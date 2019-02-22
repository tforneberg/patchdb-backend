package de.tforneberg.patchdb.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class AWSS3Client {
	
    @Value("${aws.endpointUrl}")
    private String endpointUrl;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.accessKey}")
    private String accessKey;

    S3Client client;
	
	@PostConstruct
    private void initialize() {
       AwsCredentials credentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
       client = S3Client.builder()
				.region(Region.EU_CENTRAL_1)
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.build();
	}
	
	/**
	 * Uploads the given MultipartFile to the s3 storage. Returns the url. 
	 * @param multipartFile the file to save. 
	 * @return the url under which the file was saved. 
	 */
	public String uploadFile(MultipartFile multipartFile) {
	    String fileUrlForDatabase = "";
	    try {
	        File file = convertMultiPartToFile(multipartFile);
	        String fileName = generateFileName(multipartFile);
	        fileUrlForDatabase = endpointUrl + "/" + bucketName + "/" + fileName;
	        uploadFileTos3bucket(fileName, file);
	        file.delete();
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	    return fileUrlForDatabase;
	}
	
	public String deleteFileFromS3Bucket(String fileUrl) {
	    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
	    client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build());
	    return "Successfully deleted";
	}
	
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
	
	private String generateFileName(MultipartFile multiPart) {
	    return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}
	
	private boolean uploadFileTos3bucket(String fileName, File file) {
	    PutObjectResponse response = client.putObject(PutObjectRequest.builder()
	    		.bucket(bucketName)
	    		.key(fileName)
	    		.acl(ObjectCannedACL.PUBLIC_READ)
	    		.build(), 
	    		RequestBody.fromFile(file));
	    return response.sdkHttpResponse().isSuccessful();
	}
}
