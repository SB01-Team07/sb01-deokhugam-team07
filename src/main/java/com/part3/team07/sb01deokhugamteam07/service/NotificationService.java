package com.part3.team07.sb01deokhugamteam07.service;

import static com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType.REVIEW_COMMENTED;
import static com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType.REVIEW_LIKED;
import static com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType.REVIEW_RANKED;

import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.NotificationRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;

  /**
   * 알림을 생성합니다.
   * 
   * @param request 알림 생성 관련 정보를 담은 요청 객체
   **/
  public void create(NotificationCreateRequest request) {
    try {
      log.info("알림 생성 시작");
      User sender = userRepository.findById(request.getSenderId())
          .orElseThrow(() -> new UserNotFoundException(request.getSenderId()));

      Review review = reviewRepository.findById(request.getReviewId())
          .orElseThrow(() -> new NoSuchElementException(String.valueOf(request.getReviewId())));

      User receiver = review.getUser();

      String content = switch (request.getType()) {
        case REVIEW_LIKED -> "[" + sender.getNickname() + "]" + "님이 나의 리뷰를 좋아합니다.";
        case REVIEW_COMMENTED -> "[" + sender.getNickname() + "]" + "님이 나의 리뷰에 댓글을 남겼습니다.";
        case REVIEW_RANKED ->
            "나의 리뷰가" + request.getPeriod() + " 인기 리뷰" + request.getRank() + "위에 선정되었습니다.";
      };

      Notification notification = Notification.builder()
          .userId(receiver.getId())
          .reviewId(request.getReviewId())
          .content(content)
          .confirmed(false)
          .build();

      notificationRepository.save(notification);
      log.info("알림 생성 완료");
    }catch (Exception e){
      log.warn("알림 생성 실패: {}", e.getMessage(), e);
    }
  }
}
