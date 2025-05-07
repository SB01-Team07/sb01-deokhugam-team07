package com.part3.team07.sb01deokhugamteam07.repository.querydsl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LikeRepositoryCustom {

    Map<UUID, Long> countLikesByReviewIds(List<UUID> reviewIds);

}
