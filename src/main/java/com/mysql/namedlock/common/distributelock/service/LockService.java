package com.mysql.namedlock.common.distributelock.service;

import java.util.List;

public interface LockService {

    List<String> lock(String key, String useType);
}
