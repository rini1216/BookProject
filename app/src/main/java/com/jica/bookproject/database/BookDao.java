package com.jica.bookproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jica.bookproject.BookItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//데이터베이스를 사용하기위해 ROOM이용
//총 세가지 요소가 필요
//Entity - 테이블 구성
//DAO -데이터베이스를 액세스하기위해 필요한 메서드를 포함하는 인터페이스
//Database - 데이터베이스를 만들어줄 메소드 public static BookDatabase getInstance(Context context)를 설정



@Dao                //Dao로 쓰기위함\
public interface BookDao {
    @Query("SELECT * FROM BOOKENTITY")
    List<BookEntity> getAll();

    //서재(Home)내부 검색
    @Query("SELECT * FROM BOOKENTITY WHERE TITLE LIKE '%' || :keyword || '%'")
    List<BookEntity> getHomeSearch(String keyword);

    @Insert
    void insert(BookEntity bookEntity);

    @Delete
    void delete(BookEntity bookEntity);

    @Update
    void update(BookEntity bookEntity);

    //카테고리
    @Query("SELECT * FROM CATEGORYENTITY")
    List<CategoryEntity> getCategoryAll();

    @Insert
    void categoryInsert(CategoryEntity categoryEntity);

    @Delete
    void categoryDelete(CategoryEntity categoryEntity);

    @Update
    void categoryUpdate(CategoryEntity categoryEntity);



    ////////수정예정 - 쿼리결과와 리턴값이 맞지않음
    @Query("SELECT * FROM CATEGORYENTITY INNER JOIN BOOKENTITY ON BOOKENTITY.categoryId = CATEGORYENTITY.id WHERE CATEGORYENTITY.name = :text")
    List<BookEntity> getSelectCategory(String text);


    //barChart lineChart
    @Query("SELECT * FROM BOOKENTITY WHERE clear = 1 AND readDate BETWEEN :startDate AND :endDate")
    List<BookEntity> getReadBookEntity(Date startDate, Date endDate);


    //PieChart
    @Query("SELECT name FROM CATEGORYENTITY")
    List<String> getCategoryName();

    @Query("SELECT COUNT(*) FROM CATEGORYENTITY INNER JOIN BOOKENTITY ON BOOKENTITY.categoryId = CATEGORYENTITY.id WHERE BOOKENTITY.clear = 1 AND CATEGORYENTITY.name = :name") //AND CATEGORYENTITY.name = :name "
    int getCountCetegoryRead(String name);

    //Profile
    @Query("SELECT * FROM USERENTITY")
    UserEntity getUserAll();

    @Insert
    void userInsert(UserEntity userEntity);

    @Delete
    void userDelete(UserEntity userEntity);

    @Update
    void userUpdate(UserEntity userEntity);



}
