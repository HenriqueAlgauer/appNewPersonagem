package com.example.appnewpersonagem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appnewpersonagem.data.AppDatabase
import com.example.appnewpersonagem.data.Mapper.toEntity
import com.example.appnewpersonagem.data.PersonagemDao
import com.example.appnewpersonagem.data.PersonagemEntity
import kotlinx.coroutines.launch
import personagem.Personagem

class PersonagemViewModel(application: Application) : AndroidViewModel(application) {
    private val personagemDao: PersonagemDao = AppDatabase.getDatabase(application).personagemDao()

    private val _personagens = MutableLiveData<List<PersonagemEntity>>()

    val personagens: LiveData<List<PersonagemEntity>> = _personagens

    init {
        obterTodosPersonagens()
    }

    fun inserirPersonagem(personagem: Personagem) {
        viewModelScope.launch {
            val personagemEntity = personagem.toEntity()
            personagemDao.inserirPersonagem(personagemEntity)
        }
    }

    fun obterTodosPersonagens() {
        viewModelScope.launch {
            val personagensEntity = personagemDao.obterTodosPersonagens()
            _personagens.postValue(personagensEntity) // Atualiza o LiveData
        }
    }


    fun atualizarPersonagem(personagem: PersonagemEntity) {
        viewModelScope.launch {
            personagemDao.atualizarPersonagem(personagem)
        }
    }

    fun deletarPersonagem(personagem: PersonagemEntity) {
        viewModelScope.launch {
            personagemDao.deletarPersonagem(personagem)
        }
    }

}
