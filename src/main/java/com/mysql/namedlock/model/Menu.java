package com.mysql.namedlock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
  private String idx;
  private String name;
  private int stock;

  private void checkStock() {
    if (stock < 1) {
      throw new IllegalArgumentException("재고가 부족합니다.");
    }
  }

  public void decreaseStock() {
    checkStock();
    stock = stock - 1;
  }
}
