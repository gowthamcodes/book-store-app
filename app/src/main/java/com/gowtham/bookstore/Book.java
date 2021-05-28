package com.gowtham.bookstore;

public class Book {

    private final String title;
    private final String author;
    private final String year;
    private final String pages;
    private final String url;
    private final String thumbnail;
    private final boolean isPdf;
    private final boolean isEpub;


    public Book(String title, String author, String year, String pages, String url, String thumbnail, boolean isPdf, boolean isEpub) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.pages = pages;
        this.url = url;
        this.thumbnail = thumbnail;
        this.isPdf = isPdf;
        this.isEpub = isEpub;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }

    public String getPages() {
        return pages;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public boolean isPdf() {
        return isPdf;
    }

    public boolean isEpub() {
        return isEpub;
    }


}
