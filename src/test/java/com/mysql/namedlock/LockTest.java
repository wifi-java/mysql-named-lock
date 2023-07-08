package com.mysql.namedlock;

import com.mysql.namedlock.dao.MenuDao;
import com.mysql.namedlock.model.Menu;
import com.mysql.namedlock.service.MenuService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class LockTest {
  /*
  CREATE TABLE `tb_menu` (
  	`IDX` INT(10) NOT NULL AUTO_INCREMENT,
  	`NAME` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
  	`STOCK` INT(10) NULL DEFAULT NULL,
  	PRIMARY KEY (`IDX`) USING BTREE
  )
  COLLATE='utf8mb4_0900_ai_ci'
  ENGINE=InnoDB;
  */

  @Autowired
  private MenuService menuService;

  @Autowired
  private MenuDao menuDao;

  @Test
  @Order(1)
  public void 분산락_사용_재고_동시성_테스트() {
    // given
    // 재고 100개짜리 상품 하나가 있다.
    Menu menu = Menu.builder()
            .name("테스트 메뉴")
            .stock(100)
            .build();

    menuDao.insertMenu(menu);

    menu = menuDao.selectMenu(menu.getIdx());
    String idx = menu.getIdx();

    // when
    // 100명이 동일한 상품을 동시에 구매한다.
    int numberOfThreads = 100;
    ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    for (int i = 0; i < numberOfThreads; i++) {
      service.execute(() -> {
        menuService.decreaseStockForDistributeLock(idx);
        latch.countDown();
      });
    }

    try {
      latch.await(10, TimeUnit.SECONDS);

      // then
      // 재고가 0개가 되어야 한다.
      Menu menu2 = menuDao.selectMenu(idx);
      menuDao.deleteMenu();
      Assertions.assertEquals(0, menu2.getStock());

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(2)
  public void 분산락_사용안함_재고_동시성_테스트() {
    // given
    // 재고 100개짜리 상품 하나가 있다.
    Menu menu = Menu.builder()
            .name("테스트 메뉴")
            .stock(100)
            .build();

    menuDao.insertMenu(menu);
    menu = menuDao.selectMenu(menu.getIdx());
    String idx = menu.getIdx();

    // when
    // 100명이 동일한 상품을 동시에 구매한다.
    int numberOfThreads = 100;
    ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    for (int i = 0; i < numberOfThreads; i++) {
      service.execute(() -> {
        menuService.decreaseStockForNotDistributeLock(idx);
        latch.countDown();
      });
    }

    try {
      latch.await(10, TimeUnit.SECONDS);

      // then
      // 재고가 0개가 아닌 임의의 재고가 남는다.
      Menu menu2 = menuDao.selectMenu(idx);
      menuDao.deleteMenu();
      Assertions.assertNotEquals(0, menu2.getStock());

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}