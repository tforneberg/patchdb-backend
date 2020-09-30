package de.tforneberg.patchdb.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class AWSS3Client {
	
	private static final String THUMBNAIL_PREFIX = "s_";
	
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
	
	public String uploadPatchImages(MultipartFile multipartFile) {
		return uploadImageAndThumbnail(multipartFile, "patches", 1500, 300);
	}
	
	public String uploadUserImages(MultipartFile multipartFile) {
		return uploadImageAndThumbnail(multipartFile, "users", 1000, 200);
	}
	
	/**
	 * Uploads an image to the s3 storage after extracting it from the given MultipartFile. Returns the url. 
	 * Additionaly, a smaller image is uploaded (thumbnail version) with the name  /url suffix "_s".
	 * The max size of the uploaded images is 1500x1500 and 300x300 (thumbnail).
	 * @param multipartFile the file to save. 
	 * @return the url under which the file was saved. 
	 */
	public String uploadImageAndThumbnail(MultipartFile multipartFile, String entityPath, int imageMaxSize, int thumbnailMaxSize) {
		String fileUrlForDatabase = null;
	    try {
	    	String fileName = generateTimeStampFileName(multipartFile);
			String fileNameSmall = THUMBNAIL_PREFIX+fileName;
		    fileUrlForDatabase = endpointUrl + "/" + bucketName + "/"+entityPath+"/" + fileName;
	    	
	        File originalFile = convertMultiPartToFile(multipartFile);
	        File bigFileToUpload = new File(fileName);
	        File smallFileToUpload = new File(fileNameSmall);

            BufferedImage image = ImageIO.read(originalFile);
            int height = image.getHeight();
            int width = image.getWidth();
            
            //create big file to upload with max dimensions imageMaxSize x imageMaxSize
            if (height > imageMaxSize || width > imageMaxSize) {
                Thumbnails.of(image).size(imageMaxSize, imageMaxSize).toFile(bigFileToUpload);
            } else {
            	bigFileToUpload = originalFile;
            }
            
            //create small file to upload with dimensions thumbnailMaxSize x thumbnailMaxSize
            Thumbnails.of(image).size(thumbnailMaxSize, thumbnailMaxSize).toFile(smallFileToUpload);

	        uploadFileTos3bucket(entityPath+"/"+fileName, bigFileToUpload);
	        uploadFileTos3bucket(entityPath+"/"+fileNameSmall, smallFileToUpload);
			originalFile.delete();
	        bigFileToUpload.delete();
	        smallFileToUpload.delete();
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	    return fileUrlForDatabase;
	}
	
	public boolean deleteFileFromS3BucketByUrl(String fileUrl) {
	    String fileName = getFilenameFromUrl(fileUrl);
	    return deleteFileFromS3Bucket(fileName);
	}
	
	public String getFilenameFromUrl(String fileUrl) {
		return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
	}
	
	private boolean deleteFileFromS3Bucket(String fileName) {
	    DeleteObjectResponse response = client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build());
	    return response.sdkHttpResponse().isSuccessful();
	}
	
	public boolean deleteImageAndThumbnailFromBucket(String fileUrl) {
	    String fileName = getFilenameFromUrl(fileUrl);
		boolean bigImageDeleted = deleteFileFromS3Bucket(fileName);
		return bigImageDeleted ? deleteFileFromS3Bucket(THUMBNAIL_PREFIX+fileName) : false;
	}
	
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
	
	private String generateFileName(MultipartFile multiPart) throws UnsupportedEncodingException {
		String origFileName = multiPart.getOriginalFilename();
	    return URLEncoder.encode(new Date().getTime() + "-" + origFileName, "UTF-8");
	}
	
	private String generateTimeStampFileName(MultipartFile multiPart) {
		String origFileName = multiPart.getOriginalFilename();
		String dataType = origFileName.substring(origFileName.lastIndexOf("."));
		long timeStamp = new Date().getTime();
		return timeStamp + dataType;
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
