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

    private val _personagemSelecionado = MutableLiveData<PersonagemEntity?>()
    val personagemSelecionado: LiveData<PersonagemEntity?> = _personagemSelecionado

    fun obterPersonagemPorId(id: Int) {
        viewModelScope.launch {
            val personagem = personagemDao.obterPersonagemPorId(id)
            _personagemSelecionado.postValue(personagem)
        }
    }

    fun inserirPersonagem(personagem: Personagem) {
        viewModelScope.launch {
            val personagemEntity = personagem.toEntity()
            personagemDao.inserirPersonagem(personagemEntity)
            obterTodosPersonagens() // Atualiza a lista após inserir
        }
    }

    fun obterTodosPersonagens() {
        viewModelScope.launch {
            val personagensEntity = personagemDao.obterTodosPersonagens()
            _personagens.postValue(personagensEntity)
        }
    }

    fun editarPersonagem(personagem: PersonagemEntity) {
        viewModelScope.launch {
            personagemDao.editarPersonagem(personagem)
            obterTodosPersonagens() // Atualiza a lista após editar
            limparPersonagemSelecionado() // Limpa o personagem selecionado após a edição
        }
    }

    fun deletarPersonagem(personagem: PersonagemEntity) {
        viewModelScope.launch {
            personagemDao.deletarPersonagem(personagem)
            obterTodosPersonagens()
        }
    }

    // Função para limpar o personagem selecionado após uso
    private fun limparPersonagemSelecionado() {
        _personagemSelecionado.postValue(null)
    }
}
