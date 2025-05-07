package com.part3.team07.sb01deokhugamteam07.scheduler;

import com.part3.team07.sb01deokhugamteam07.batch.popularbook.PopularBookDashboardBatchService;
import com.part3.team07.sb01deokhugamteam07.batch.popularreview.PopularReviewDashboardBatchService;
import com.part3.team07.sb01deokhugamteam07.batch.poweruser.PowerUserDashboardBatchService;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.service.DashboardService;
import com.part3.team07.sb01deokhugamteam07.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationScheduler {
  private final PowerUserDashboardBatchService powerUserDashboardBatchService;
  private final PopularBookDashboardBatchService popularBookDashboardBatchService;
  private final PopularReviewDashboardBatchService popularReviewDashboardBatchService;
  private final NotificationService notificationService;
  private final DashboardService dashboardService;


  @Scheduled(cron = "0 30 12 * * *")
  public void deleteDashboard(){
    log.info("오전 3시 : 대시보드 일괄 삭제 시작");
    try {
      dashboardService.delete();
    }catch (Exception e){
      log.warn("대시보드 일괄 삭제 중 오류 발생: {}", e.getMessage(), e);
    }
  }


  @Scheduled(cron = "0 31 12 * * *")
  public void calculateAllDashboardData(){
    log.info("오전 3시 : 대시보드 데이터 일괄 계산 시작");
    // 순차 실행
    try {
      calculatePowerUsers();
    }catch (Exception e){
      log.warn("파워 유저 계산 중 오류 발생: {}", e.getMessage(), e);
    }

    try {
      calculatePopularBooks();
    }catch (Exception e){
      log.warn("인기 도서 계산 중 오류 발생: {}", e.getMessage(), e);
    }

    try {
      calculatePopularReview();
    }catch (Exception e){
      log.warn("인기 리뷰 계산 중 오류 발생: {}", e.getMessage(), e);
    }
  }


  public void calculatePowerUsers(){
    log.info("파워 유저 연산 시작합니다.");
    powerUserDashboardBatchService.savePowerUserDashboardData(Period.DAILY);
    powerUserDashboardBatchService.savePowerUserDashboardData(Period.WEEKLY);
    powerUserDashboardBatchService.savePowerUserDashboardData(Period.MONTHLY);
    powerUserDashboardBatchService.savePowerUserDashboardData(Period.ALL_TIME);
    log.info("유저 연산 완료되었습니다.");
  }

  public void calculatePopularBooks(){
    log.info("인기 도서 연산 시작합니다.");
    popularBookDashboardBatchService.savePopularBookDashboardData(Period.DAILY);
    popularBookDashboardBatchService.savePopularBookDashboardData(Period.WEEKLY);
    popularBookDashboardBatchService.savePopularBookDashboardData(Period.MONTHLY);
    popularBookDashboardBatchService.savePopularBookDashboardData(Period.ALL_TIME);
    log.info("인기 도서 연산 완료되었습니다.");
  }

  public void calculatePopularReview(){
    log.info("인기 리뷰 연산 시작합니다.");
    popularReviewDashboardBatchService.savePopularReviewDashboardData(Period.DAILY);
    popularReviewDashboardBatchService.savePopularReviewDashboardData(Period.WEEKLY);
    popularReviewDashboardBatchService.savePopularReviewDashboardData(Period.MONTHLY);
    popularReviewDashboardBatchService.savePopularReviewDashboardData(Period.ALL_TIME);
    log.info("인기 리뷰 연산 완료되었습니다.");

  }

  @Scheduled(cron = "0 0 4 * * *")
  public void deleteAllOldNotification(){
    log.info("오전 4시 : 확인된 알림 중 일주일 경과된 알림을 전체 삭제합니다.");
    notificationService.delete();
    log.info("확인된 알림 중 일주일 경과된 알림 전체 삭제 완료했습니다.");
  }
}
