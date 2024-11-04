package com.example.appnewpersonagem.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonagemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirPersonagem(personagem: PersonagemEntity)

    @Update
    suspend fun atualizarPersonagem(personagem: PersonagemEntity)

    @Delete
    suspend fun deletarPersonagem(personagem: PersonagemEntity)

    @Query("SELECT * FROM personagens")
    suspend fun obterTodosPersonagens(): List<PersonagemEntity>

}
