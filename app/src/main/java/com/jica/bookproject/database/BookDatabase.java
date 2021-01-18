package com.jica.bookproject.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {BookEntity.class, CategoryEntity.class, UserEntity.class}, version = 1, exportSchema = false)
                    //엔티티 클래스 정보

@TypeConverters({Converter.class})
public abstract class BookDatabase extends RoomDatabase {


    //데이터베이스형 변수를 생성한다 ->만든 데이터베이스를 담는다. static으로 생성
    private static BookDatabase bookDatabase;


    public abstract BookDao bookDao(); //추상메소드 //BookDao를 리턴함

    public static BookDatabase getInstance(Context context){

        if(bookDatabase == null){
            bookDatabase = Room.databaseBuilder(context.getApplicationContext(),BookDatabase.class,"bookDB").build();
                            //데이터베이스를 만들어줌   //앱전체정보를 받아오기위해 //현재 데이터베이스의 클래스 정보, //데이터베이스이름
        }

        return bookDatabase;
    }
}
