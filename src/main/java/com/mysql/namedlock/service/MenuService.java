package com.mysql.namedlock.service;

import com.mysql.namedlock.common.distributelock.annotation.DistributeLock;
import com.mysql.namedlock.dao.MenuDao;
import com.mysql.namedlock.model.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {
  private final MenuDao menuDao;

  @DistributeLock(key = "#idx", useType = "MENU")
  public void decreaseStockForDistributeLock(String idx) {
    Menu menu = menuDao.selectMenu(idx);
    menu.decreaseStock();
    menuDao.updateMenuStock(menu);
  }

  @Transactional
  public void decreaseStockForNotDistributeLock(String idx) {
    Menu menu = menuDao.selectMenu(idx);
    menu.decreaseStock();
    menuDao.updateMenuStock(menu);
  }
}
