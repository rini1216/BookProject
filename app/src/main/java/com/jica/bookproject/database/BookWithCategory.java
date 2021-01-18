package com.jica.bookproject.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BookWithCategory {
    @Embedded public CategoryEntity categoryEntity;
    @Relation(
            parentColumn =  "id",
            entityColumn =  "categoryId"
    )
    public List<BookEntity> bookEntities;
}
