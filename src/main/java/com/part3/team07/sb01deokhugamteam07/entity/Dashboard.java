package com.part3.team07.sb01deokhugamteam07.entity;


import java.math.BigDecimal;
import java.util.UUID;
import com.part3.team07.sb01deokhugamteam07.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "dashboards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dashboard extends BaseEntity {

  @Column(name = "\"key\"", nullable = false)
  private UUID key;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private KeyType keyType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Period period;

  @Column(name = "\"value\"", nullable = false)
  private BigDecimal value;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ValueType valueType;

  @Column
  private Integer rank;

}
