package bitcamp.carrot_thunder;

import bitcamp.carrot_thunder.config.NcpConfig;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
public class NcpObjectStorageService {

  final AmazonS3 s3;

  public NcpObjectStorageService(NcpConfig ncpConfig) {
    System.out.println("✅NcpObjectStorageService() executed");
    s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                    ncpConfig.getEndPoint(), ncpConfig.getRegionName()))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                    ncpConfig.getAccessKey(), ncpConfig.getSecretKey())))
            .build();
  }

  public String uploadFile(String bucketName, String dirPath, MultipartFile part) {
    if (part.getSize() == 0) {
      return null;
    }

    try (InputStream fileIn = part.getInputStream()) {
      String filename = UUID.randomUUID().toString();

      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentType(part.getContentType());

      PutObjectRequest objectRequest = new PutObjectRequest(
              bucketName,
              dirPath + filename,
              fileIn,
              objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);

      s3.putObject(objectRequest);

      return filename;

    } catch (Exception e) {
      throw new RuntimeException("❗️파일 업로드 오류", e);
    }
  }

  public void deleteFile(String bucketName, String filePath) {
    try {
      s3.deleteObject(new DeleteObjectRequest(bucketName, filePath));
    } catch (Exception e) {
      throw new RuntimeException("❗️파일 삭제 오류", e);
    }
  }
}
