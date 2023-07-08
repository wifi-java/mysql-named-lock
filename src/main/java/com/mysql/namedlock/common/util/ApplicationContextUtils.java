package com.mysql.namedlock.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    applicationContext = context;
  }

  /**
   * ApplicationContext 반환.
   *
   * @return ApplicationContext
   */
  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * ApplicationContext 내에서 특정한 클래스 유형의 빈을 찾아 반환.
   *
   * @param classType 찾을 클래스 유형
   * @return T 찾아낸 빈 객체
   */
  public static <T> T getBean(Class<T> classType) {
    return applicationContext.getBean(classType);
  }

  /**
   * ApplicationContext 내에서 특정한 클래스 유형의 빈을 찾아 반환.
   *
   * @param name      빈 이름
   * @param classType 찾을 클래스 유형
   * @return T 찾아낸 빈 객체
   */
  public static <T> T getBean(String name, Class<T> classType) {
    return applicationContext.getBean(name, classType);
  }
}
