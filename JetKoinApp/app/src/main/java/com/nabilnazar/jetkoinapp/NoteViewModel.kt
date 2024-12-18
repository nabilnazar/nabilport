package com.nabilnazar.jetkoinapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun addNote(note: Note) {
        viewModelScope.launch { noteDao.insert(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { noteDao.delete(note) }
    }
}