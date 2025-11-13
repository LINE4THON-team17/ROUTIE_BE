// com.example.routie_be.domain.route.service.S3Uploader

package com.example.routie_be.domain.route.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3Uploader {

    @Autowired(required = false)
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket:${CLOUD_AWS_S3_BUCKET:${CLOUD_AWS_S3_BUCKET_NAME:}}}")
    private String bucket;

    private void ensureAvailable() {
        if (amazonS3 == null || bucket == null || bucket.isBlank()) {
            throw new IllegalStateException(
                    "S3 설정 없음: 현재 환경에서 파일 업로드/삭제 불가. "
                            + "필요하다면 'cloud.aws.s3.bucket' 또는 'CLOUD_AWS_S3_BUCKET' 또는 'CLOUD_AWS_S3_BUCKET_NAME' 및 "
                            + "AWS 자격증명을 설정하세요.");
        }
    }

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        ensureAvailable();

        String originalFileName = multipartFile.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;

        log.info("[S3Uploader] Upload start. bucket={}, key={}", bucket, fileName);

        File uploadFile = convert(multipartFile);

        String uploadImageUrl = putS3(uploadFile, fileName);

        log.info("[S3Uploader] Upload success. url={}", uploadImageUrl);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private File convert(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    public void deleteFile(String fileName) {
        ensureAvailable();

        try {
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("Deleting file from S3: {}", decodedFileName);
            amazonS3.deleteObject(bucket, decodedFileName);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName)
            throws IOException {

        ensureAvailable();

        log.info("S3 oldFileName: {}", oldFileName);
        deleteFile(oldFileName);

        return upload(newFile, dirName);
    }
}
