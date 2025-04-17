package com.part3.team07.sb01deokhugamteam07.logging;

import com.p6spy.engine.spy.P6SpyOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {

  @PostConstruct
  public void setLogMessageFormat() {
    P6SpyOptions.getActiveInstance().setLogMessageFormat(MyP6spyFormattingStrategy.class.getName());
  }
}
