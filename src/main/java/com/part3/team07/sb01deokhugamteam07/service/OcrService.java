package com.part3.team07.sb01deokhugamteam07.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.protobuf.ByteString;
import com.part3.team07.sb01deokhugamteam07.exception.ocr.OcrNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.ocr.OcrProcessingFailedException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

  public String extractIsbn13(MultipartFile file) {
    List<AnnotateImageRequest> requests = new ArrayList<>();
    try {
      log.info("OCR 처리 시작");

      ByteString imgBytes = ByteString.readFrom(file.getInputStream());

      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
      AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
          .addFeatures(feat)
          .setImage(img)
          .build();
      requests.add(request);

      try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
        log.debug("Google Vision API 요청 전송");

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        TextAnnotation annotation = response.getResponses(0).getFullTextAnnotation();
        String fullText = annotation.getText();

        log.debug("OCR 결과 텍스트: {}", fullText.replace("\n", " ").substring(0, Math.min(fullText.length(), 200)));

        Pattern isbnPattern = Pattern.compile("(97[89][\\s\\-]?[0-9]{1,5}[\\s\\-]?[0-9]+[\\s\\-]?[0-9]+[\\s\\-]?[0-9Xx])");
        Matcher matcher = isbnPattern.matcher(fullText);

        if (matcher.find()) {
          String rawIsbn = matcher.group();
          String numericIsbn = rawIsbn.replaceAll("[^0-9]", "");
          log.info("ISBN13 추출 성공: {}", numericIsbn);
          if (numericIsbn.length() == 13) {
            return numericIsbn;
          }
        }

        log.warn("ISBN13 추출 실패 - 패턴 일치 없음");
        throw new OcrNotFoundException();
      }
    } catch (Exception e) {
      log.error("OCR 처리 실패", e);
      throw new OcrProcessingFailedException();
    }
  }
}