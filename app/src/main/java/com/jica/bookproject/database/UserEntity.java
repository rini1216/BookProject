package com.jica.bookproject.database;

import androidx.core.content.pm.PermissionInfoCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userEntity")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "age")
    String age;
    @ColumnInfo(name = "mybestbook")
    String mybestbook;


    public UserEntity(String name, String age, String mybestbook) {
        this.name = name;
        this.age = age;
        this.mybestbook = mybestbook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMybestbook() {
        return mybestbook;
    }

    public void setMybestbook(String mybestbook) {
        this.mybestbook = mybestbook;
    }
}
