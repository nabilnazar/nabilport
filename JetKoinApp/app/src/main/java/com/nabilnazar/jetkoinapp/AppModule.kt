package com.nabilnazar.jetkoinapp

import androidx.room.Room
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "note_database")
            .build()
    }
    single { get<AppDatabase>().noteDao() }


    viewModelOf(::NoteViewModel)



}