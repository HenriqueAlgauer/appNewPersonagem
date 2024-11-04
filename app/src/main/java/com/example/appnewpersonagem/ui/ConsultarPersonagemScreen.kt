package com.example.appnewpersonagem.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appnewpersonagem.data.PersonagemEntity
import com.example.appnewpersonagem.viewmodel.PersonagemViewModel

@Composable
fun ConsultarPersonagemScreen(viewModel: PersonagemViewModel) {

    // Carrega a lista de personagens ao iniciar a tela
    LaunchedEffect(Unit) {
        viewModel.obterTodosPersonagens()
    }

    val personagens by viewModel.personagens.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lista de Personagens",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(personagens) { personagem ->
                PersonagemItem(
                    personagem = personagem,
                    onDeleteClick = { viewModel.deletarPersonagem(personagem) }
                )
            }
        }
    }
}

@Composable
fun PersonagemItem(personagem: PersonagemEntity, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = personagem.nome)
        Button(onClick = onDeleteClick) {
            Text("Excluir")
        }
    }
}
