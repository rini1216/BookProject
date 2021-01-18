package com.jica.bookproject.database;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(tableName = "bookEntity")   //엔티티 테이블을 만들어준다.
public class BookEntity implements Serializable { //Budle로 데이터를 한꺼번에 넘기고 싶다면 Serializable implement

    @PrimaryKey(autoGenerate = true) //자동으로 id를 생성해준다.
    int id;

    @ColumnInfo(name = "title") //이름을 따로 지정가능
    String title;
    @ColumnInfo(name = "author")
    String author;
    @ColumnInfo(name = "publisher")
    String publisher;
    @ColumnInfo(name = "pubdate")
    String pubdate;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "image")
    String image;
    @ColumnInfo(name = "rating")
    float rating;
    @ColumnInfo(name = "clear")
    boolean clear;
    @ColumnInfo(name = "paragraph")
    String paragraph;
    @ColumnInfo(name = "review")
    String review;
    @ColumnInfo(name = "photoPath")
    String photoPath;
    @ColumnInfo(name = "readDate")
    Date readDate;

    @ColumnInfo(name = "categoryId")
    int categoryId;

    //id는 자동으로 생성되기때문에 id는 인자로 받지않는다.
    public BookEntity(String title, String author, String publisher, String pubdate, String description, String image, float rating, boolean clear, String paragraph, String review, String photoPath, int categoryId, Date readDate) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubdate = pubdate;
        this.description = description;
        this.image = image;
        this.rating = rating;
        this.clear = clear;
        this.paragraph = paragraph;
        this.review = review;
        this.photoPath = photoPath;
        this.categoryId = categoryId;
        this.readDate = readDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }
}

