package com.example.booklisting;

import android.graphics.Bitmap;

/**
 * A {@link Book} object contains information related to a single book.
 */
public class Book {

    /** Title of the book */
    private String mTitle;

    /** Author of the book */
    private String mAuthor;

    /** Year of publication of the book */
    private String mYear;

    /** InfoLink of the book */
    private String mInfoLink;

    /** Image of the book */
    private Bitmap mImage;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param title is the title of the book
     * @param author is the name of the author of the book
     * @param year is the year of publication of the book
     * @param infoLink is the info link of the book
     * @param image is the thumbnail of the book
     */
    public Book(String title, String author, String year, String infoLink, Bitmap image) {
        mTitle = title;
        mAuthor = author;
        mYear = year;
        mInfoLink = infoLink;
        mImage = image;
    }

    /**
     * Returns the title of the book.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the name of the author of the book.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the year of publication of the book.
     */
    public String getYear() {
        return mYear;
    }

    /**
     * Returns the info link of the book.
     */
    public String getInfoLink() {
        return mInfoLink;
    }

    /**
     * Returns the image link of the book.
     */
    public Bitmap getImage() {
        return mImage;
    }


}