package com.part3.team07.sb01deokhugamteam07.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.TextAnnotation;
import com.part3.team07.sb01deokhugamteam07.exception.ocr.OcrNotFoundException;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class OcrServiceTest {

  @Mock
  private ImageAnnotatorClient imageAnnotatorClient;

  @InjectMocks
  private OcrService ocrService;

  @Test
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
  void parseIsbn13_fail_OcrNotFound() throws Exception {
    // given
    String ocrText = "이 텍스트에는 ISBN이 없습니다.";
    Method method = OcrService.class.getDeclaredMethod("parseIsbn13", String.class);
    method.setAccessible(true);

    // when & then
    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      method.invoke(ocrService, ocrText);
    });

    // 실제 예외를 검사
    Throwable targetException = exception.getTargetException();
    assertTrue(targetException instanceof OcrNotFoundException);
  }
}
