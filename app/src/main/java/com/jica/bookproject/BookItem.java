package com.jica.bookproject;

public class BookItem {

    String title;
    String author;
    String image;
    String publisher;
    String pubdate;
    String description;


    static int total;

    public BookItem() {
    }


    public BookItem(String title, String author, String image, String publisher, String pubdate, String description) {
        this.title = title;
        this.author = author;
        this.image = image;
        this.publisher = publisher;
        this.pubdate = pubdate;
        this.description = description;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }



    @Override
    public String toString() {
        return "BookItem{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", image='" + image + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pubdate='" + pubdate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
