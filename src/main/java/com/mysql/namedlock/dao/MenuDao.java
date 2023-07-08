package com.mysql.namedlock.dao;

import com.mysql.namedlock.model.Menu;

public interface MenuDao {
    void insertMenu(Menu menu);
    Menu selectMenu(String idx);
    void updateMenuStock(Menu menu);
    void deleteMenu();
}
