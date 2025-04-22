package com.part3.team07.sb01deokhugamteam07.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableNotBlankValidator implements ConstraintValidator<NullableNotBlank, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null | !value.trim().isEmpty();
  }
}
