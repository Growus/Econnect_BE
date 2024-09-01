package com.growus.econnect.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Slf4j
@Service
public class S3Uploader {

    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3Uploader(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;
        log.info("fileName: " + fileName);

        // 파일을 S3에 직접 업로드
        return uploadToS3(multipartFile, fileName);
    }

    private String uploadToS3(MultipartFile multipartFile, String fileName) throws IOException {
        // 파일의 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }

        // 업로드된 파일의 URL 반환
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileName) {
        try {
            if (fileName != null) {
                // URL 디코딩을 통해 원래의 파일 이름을 가져옵니다.
                String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
                log.info("Deleting file from S3: " + decodedFileName);
                amazonS3.deleteObject(bucket, decodedFileName);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName) throws IOException {
        // 기존 파일 삭제
        log.info("S3 oldFileName: " + oldFileName);
        deleteFile(oldFileName);
        // 새 파일 업로드
        return upload(newFile, dirName);
    }
}
