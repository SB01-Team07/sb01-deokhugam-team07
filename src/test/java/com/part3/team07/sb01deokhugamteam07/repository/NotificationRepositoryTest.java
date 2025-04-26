package com.part3.team07.sb01deokhugamteam07.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class NotificationRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private NotificationRepository notificationRepository;
  
  @Test
  @DisplayName("일주일 이전에 생성되고 확인된 알림만 삭제되는지 테스트")
  void ddeleteAllByConfirmedTrueAndCreatedAtBeforeTest(){
    UUID userId = UUID.randomUUID();
    LocalDateTime oneweekAgo = LocalDateTime.now().minusWeeks(1);


    // 확인 + 일주일 지난 알림 생성
    Notification notification1 = Notification.builder()
        .userId(userId)
        .reviewId(UUID.randomUUID())
        .content("확인된 알림")
        .confirmed(true)
        .build();

    testEntityManager.persist(notification1);
    
    // 확인되지 않음 + 일주일 지난 알림 생성
    Notification notification2 = Notification.builder()
        .userId(userId)
        .reviewId(UUID.randomUUID())
        .content("확인되지 않았고 일주일 지난 알림")
        .confirmed(false)
        .build();

    testEntityManager.persist(notification2);

    // 삭제 조건에 걸리지 않는 알림 생성
    Notification notification3 = Notification.builder()
        .userId(userId)
        .reviewId(UUID.randomUUID())
        .content("일주일 지난 알림")
        .confirmed(false)
        .build();

    testEntityManager.persist(notification3);

    testEntityManager.flush();

    // flush() 이후 다시 flush()
    ReflectionTestUtils.setField(notification1, "createdAt", oneweekAgo.minusSeconds(1));
    ReflectionTestUtils.setField(notification2, "createdAt", oneweekAgo.minusSeconds(1));
    ReflectionTestUtils.setField(notification3, "createdAt", LocalDateTime.now());

    testEntityManager.flush();

    int firstCount = notificationRepository.findAll().size();
    notificationRepository.deleteAllByConfirmedTrueAndCreatedAtBefore(oneweekAgo);
    testEntityManager.flush();
    int secondCount = notificationRepository.findAll().size();
    List<Notification> remainNotifications = notificationRepository.findAll();

    assertThat(firstCount).isEqualTo(3);
    assertThat(secondCount).isEqualTo(2);
    assertThat(remainNotifications.get(0).isConfirmed()).isFalse();
    assertThat(remainNotifications.get(1).isConfirmed()).isFalse();
  }

}