package com.omega.config.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

//@Component
public class HerokuNotIdle {

  private static final Logger LOG = LoggerFactory.getLogger(HerokuNotIdle.class);

//  @Scheduled(fixedDelay = 120000)
//  public void herokuNotIdle() {
//    LOG.debug("Heroku not idle execution");
//    RestTemplate restTemplate = new RestTemplate();
//    restTemplate.getForObject("https://spring-quick-buy.herokuapp.com", String.class);
//  }
}
