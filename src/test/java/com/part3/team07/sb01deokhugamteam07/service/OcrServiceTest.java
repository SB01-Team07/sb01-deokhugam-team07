package com.part3.team07.sb01deokhugamteam07.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.TextAnnotation;
import com.part3.team07.sb01deokhugamteam07.exception.ocr.OcrNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.ocr.OcrProcessingFailedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class OcrServiceTest {

  @Mock
  private ImageAnnotatorClient imageAnnotatorClient;

  @InjectMocks
  private OcrService ocrService;

  private MultipartFile validImageFile;
  private MultipartFile invalidImageFile;

  @BeforeEach
  void setUp() {
    validImageFile = new MockMultipartFile(
        "validImage.jpg",
        "validImage.jpg",
        "image/jpeg",
        "valid image content".getBytes()
    );

    invalidImageFile = new MockMultipartFile(
        "invalidImage.jpg",
        "invalidImage.jpg",
        "image/jpeg",
        "invalid image content".getBytes()
    );
  }

  @Nested
  @DisplayName("extractIsbn13")
  class extractIsbn13Test {
    @Test
    @DisplayName("extractIsbn13 성공")
    void extractIsbn13_success() throws Exception {
      // given
      MultipartFile mockFile = mock(MultipartFile.class);
      given(mockFile.getInputStream()).willReturn(new ByteArrayInputStream("test".getBytes()));

      BatchAnnotateImagesResponse mockResponse = mock(BatchAnnotateImagesResponse.class);
      AnnotateImageResponse innerResponse = mock(AnnotateImageResponse.class);
      TextAnnotation textAnnotation = TextAnnotation.newBuilder().setText("ISBN 978-89-01-12345-6").build();

      given(mockResponse.getResponses(0)).willReturn(innerResponse);
      given(innerResponse.getFullTextAnnotation()).willReturn(textAnnotation);
      given(imageAnnotatorClient.batchAnnotateImages(anyList())).willReturn(mockResponse);

      // when
      String result = ocrService.extractIsbn13(mockFile);

      // then
      assertEquals("9788901123456", result);
    }

    @Test
    @DisplayName("ISBN13이 없는 이미지에서 OcrNotFoundException 발생")
    void extractIsbn13_fail_WithoutIsbn_ThrowsOcrNotFoundException() throws IOException {
      // given
      String textWithoutIsbn = "This is just some text without any ISBN number.";

      TextAnnotation textAnnotation = TextAnnotation.newBuilder()
          .setText(textWithoutIsbn)
          .build();

      AnnotateImageResponse annotateImageResponse = AnnotateImageResponse.newBuilder()
          .setFullTextAnnotation(textAnnotation)
          .build();

      BatchAnnotateImagesResponse batchResponse = BatchAnnotateImagesResponse.newBuilder()
          .addResponses(annotateImageResponse)
          .build();

      given(imageAnnotatorClient.batchAnnotateImages((List<AnnotateImageRequest>) any())).willReturn(batchResponse);

      // when & then
      assertThrows(OcrNotFoundException.class, () -> {
        ocrService.extractIsbn13(validImageFile);
      });
      verify(imageAnnotatorClient).batchAnnotateImages((List<AnnotateImageRequest>) any());
    }

    @Test
    @DisplayName("OCR 처리 중 예외 발생시 OcrProcessingFailedException 발생")
    void extractIsbn13_fail_WhenOcrProcessingFails_ThrowsOcrProcessingFailedException() throws IOException {
      // given
      given(imageAnnotatorClient.batchAnnotateImages((List<AnnotateImageRequest>) any()))
          .willThrow(new RuntimeException("OCR 처리 중 오류 발생"));

      // when & then
      assertThrows(OcrProcessingFailedException.class, () -> {
        ocrService.extractIsbn13(invalidImageFile);
      });
      verify(imageAnnotatorClient).batchAnnotateImages((List<AnnotateImageRequest>) any());
    }
  }

  @Nested
  @DisplayName("parseIsbn13")
  class parseIsbn13Test {
    @Test
    @DisplayName("parseIsbn13 성공")
    void parseIsbn13_success() throws Exception {
      // given
      String ocrText = "Some random text...\nISBN 978-89-01-12345-6";

      // when
      Method method = OcrService.class.getDeclaredMethod("parseIsbn13", String.class);
      method.setAccessible(true);
      String result = (String) method.invoke(ocrService, ocrText);

      // then
      assertEquals("9788901123456", result);
    }

    @Test
    @DisplayName("ISBN13이 없는 이미지에서 OcrNotFoundException 발생")
    void parseIsbn13_fail_OcrNotFound() throws Exception {
      // given
      String ocrText = "이 텍스트에는 ISBN이 없습니다.";
      Method method = OcrService.class.getDeclaredMethod("parseIsbn13", String.class);
      method.setAccessible(true);

      // when & then
      InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
        method.invoke(ocrService, ocrText);
      });

      Throwable targetException = exception.getTargetException();
      assertTrue(targetException instanceof OcrNotFoundException);
    }
  }

}
