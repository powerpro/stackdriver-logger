package com.powerpro.stackdriverlogger;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StackdriverLoggerApplication {

  public static void main(String[] args) {
    SpringApplication.run(StackdriverLoggerApplication.class, args);
    log.debug("In application main");
  }

}
