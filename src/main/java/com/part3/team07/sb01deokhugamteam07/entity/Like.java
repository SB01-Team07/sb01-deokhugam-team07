package com.part3.team07.sb01deokhugamteam07.entity;


import com.part3.team07.sb01deokhugamteam07.entity.base.BaseEntity;
import com.part3.team07.sb01deokhugamteam07.entity.base.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name ="likes")
@Entity
public class Like extends BaseSoftDeletableEntity {

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private UUID reviewId;
}
