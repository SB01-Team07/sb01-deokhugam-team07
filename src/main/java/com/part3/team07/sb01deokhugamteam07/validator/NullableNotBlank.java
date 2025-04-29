package com.part3.team07.sb01deokhugamteam07.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullableNotBlankValidator.class)
public @interface NullableNotBlank {
  String message() default "값이 비어 있을 수 없습니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
