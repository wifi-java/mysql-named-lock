package com.mysql.namedlock.common.distributelock;

import com.mysql.namedlock.common.distributelock.service.LockService;
import com.mysql.namedlock.common.util.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NamedLock {
  public enum UseType {
    MENU
  }

  public static LockService getLockService(String useType) throws Exception {
    LockService lockService = null;

    try {
      log.info("useType => {}", useType);
      UseType type = UseType.valueOf(useType.toUpperCase());

      switch (type) {
        case MENU:
          lockService = ApplicationContextUtils.getBean("LockServiceMenuImpl", LockService.class);
          break;
      }
    } catch (Exception e) {
      log.error("", e);
      throw new Exception("처리중 오류가 발생했습니다. 잠시 후에 다시 시도해주시기 바랍니다.");
    }

    return lockService;
  }
}
