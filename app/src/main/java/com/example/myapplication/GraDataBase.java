package com.example.myapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Gra.class},version = 1)
public abstract class GraDataBase extends RoomDatabase {
    public abstract GraDAO zwrocGryDAO();
}
