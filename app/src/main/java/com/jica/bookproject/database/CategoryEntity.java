package com.jica.bookproject.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categoryEntity")
public class CategoryEntity {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "name") //카테고리 이름
    String name;

    public CategoryEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
