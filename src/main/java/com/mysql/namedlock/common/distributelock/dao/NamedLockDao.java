package com.mysql.namedlock.common.distributelock.dao;

public interface NamedLockDao {
    Integer getLock(String key);

    Integer releaseLock(String key);
}
