package com.part3.team07.sb01deokhugamteam07.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    type = SecuritySchemeType.APIKEY,
    name = "Deokhugam-Request-User-ID",
    description = "user id를 입력해주세요.",
    in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components())
        .info(apiInfo())
        .security(getSecurityRequirement());
  }

  private Info apiInfo() {
    return new Info()
        .title("Springdoc 테스트")
        .description("Springdoc을 사용한 Swagger UI 테스트")
        .version("1.0.0");
  }

  private List<SecurityRequirement> getSecurityRequirement() {
    List<SecurityRequirement> requirements = new ArrayList<>();
    requirements.add(new SecurityRequirement().addList("Deokhugam-Request-User-ID"));
    return requirements;
  }
}
