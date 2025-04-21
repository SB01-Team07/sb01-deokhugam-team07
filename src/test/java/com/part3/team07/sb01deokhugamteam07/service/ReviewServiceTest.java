package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    @DisplayName("도서 별 1개의 리뷰만 등록할 수 있다.")
    @Test
    void review_crate_success() {
        //given
        User user = User.builder()
                .email("abc@abc.com")
                .nickname("닉네임")
                .password("password1234")
                .build();

        Book book = Book.builder()
                .author("author")
                .isbn("isbn")
                .description("description")
                .build();

        //ReviewCreateRequest request = new ReviewCreateRequest("bookId","userId","contnet",1);


        //when
        //ReviewDto result = reviewService.create(request);


        ReviewDto reviewDto = new ReviewDto(null,null,null,null,null,null,
                null,0,0,0,true,null,null);

        //then
        //assertThat(result).isEqualTo(reviewDto);

    }

}