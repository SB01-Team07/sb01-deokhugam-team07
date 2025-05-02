package com.part3.team07.sb01deokhugamteam07.scheduler;

import com.part3.team07.sb01deokhugamteam07.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookReviewScheduler {

  private final BookService bookService;

  @Scheduled(cron = "0 */5 * * * *")
  public void updateBookReviews() {
    bookService.updateReviewStats();
  }
}
