package com.part3.team07.sb01deokhugamteam07.scheduler;

import com.part3.team07.sb01deokhugamteam07.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewUpdateScheduler {

    private final ReviewService reviewService;

    @Scheduled(cron = "0 */30 * * * *")
    public void runSyncJob() {
        log.info("리뷰 카운트 동기화 시작");
        reviewService.syncReviewCounts();
        log.info("리뷰 카운트 동기화 완료");
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void runFullSync() {
        log.info("전체 리뷰 카운트 배치 동기화 시작");
        reviewService.syncAllReviewsCountsInBatch(100); // 100개씩 처리
        log.info("전체 리뷰 카운트 배치 동기화 완료");
    }
}