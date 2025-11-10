package service;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class S3Service {
    private final S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();
    private final String BUCKET_NAME = System.getenv("S3_BUCKET_NAME");

    public void upload(File file, String fileName, String username){
        try{
            String outputFileName = "outbound/" + username + "/" + fileName;

            s3.putObject(PutObjectRequest.builder()
                    .bucket(this.BUCKET_NAME)
                    .key(outputFileName)
                    .contentType("text/plain; charset=utf-8")
                    .build(), file.toPath());
        }catch (S3Exception ex){
            throw new RuntimeException("Erro ao tentar adicionar arquivo traduzido no bucket." + ex.getMessage());
        }
    }

    public String get(String key){
        try{

            var responseBytes = s3.getObject(GetObjectRequest.builder()
                    .bucket(this.BUCKET_NAME)
                    .key(key)
                    .build(), ResponseTransformer.toBytes()
            );

            return new String(responseBytes.asByteArray(), StandardCharsets.UTF_8);
        }catch (S3Exception ex){
            throw new RuntimeException("Erro ao tentar acessar o arquivo no bucket.");
        }
    }
}
