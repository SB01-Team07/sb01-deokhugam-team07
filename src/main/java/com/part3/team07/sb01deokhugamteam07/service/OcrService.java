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
import java.io.IOException;
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

  private final ImageAnnotatorClient imageAnnotatorClient;

  public String extractIsbn13(MultipartFile file) {
    try {
      log.info("OCR 처리 시작");
      String fullText = extractTextFromImage(file);
      log.debug("OCR 전체 텍스트: {}", previewText(fullText));

      String isbn13 = parseIsbn13(fullText);
      log.info("ISBN13 추출 성공: {}", isbn13);
      return isbn13;

    } catch (OcrNotFoundException e) {
      log.warn("ISBN13 추출 실패 - 패턴 일치 없음");
      throw e;

    } catch (Exception e) {
      log.error("OCR 처리 중 예외 발생", e);
      throw new OcrProcessingFailedException();
    }
  }

  private String extractTextFromImage(MultipartFile file) throws IOException {
    ByteString imgBytes = ByteString.readFrom(file.getInputStream());
    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

    AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
        .addFeatures(feat)
        .setImage(img)
        .build();

    BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(List.of(request));
    TextAnnotation annotation = response.getResponses(0).getFullTextAnnotation();
    return annotation.getText();
  }

  private String parseIsbn13(String text) {
    Pattern isbnPattern = Pattern.compile("(97[89][\\s\\-]?[0-9]{1,5}[\\s\\-]?[0-9]+[\\s\\-]?[0-9]+[\\s\\-]?[0-9Xx])");
    Matcher matcher = isbnPattern.matcher(text);

    if (matcher.find()) {
      String rawIsbn = matcher.group();
      String numericIsbn = rawIsbn.replaceAll("[^0-9]", "");

      if (numericIsbn.length() == 13) {
        return numericIsbn;
      }
    }

    throw new OcrNotFoundException();
  }

  private String previewText(String text) {
    return text.replace("\n", " ").substring(0, Math.min(text.length(), 200));
  }
}
