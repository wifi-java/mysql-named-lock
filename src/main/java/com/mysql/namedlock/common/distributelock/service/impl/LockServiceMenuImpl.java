package com.mysql.namedlock.common.distributelock.service.impl;

import com.mysql.namedlock.common.distributelock.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component("LockServiceMenuImpl")
@Slf4j
class LockServiceMenuImpl implements LockService {
  private static final String KEY_PREFIX = "LOCK_";

  @Override
//  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public List<String> lock(String key, String useType) {
    List<String> locks = new ArrayList<>();

    String lockKey = KEY_PREFIX + useType.toUpperCase() + "_" + key;
    locks.add(lockKey);

    return locks;
  }
}
