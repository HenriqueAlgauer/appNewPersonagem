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
                    onDeleteClick = { viewModel.deletarPersonagem(personagem) },
                    onEditClick = { viewModel.atualizarPersonagem(personagem) }
                )
            }
        }
    }
}

@Composable
fun PersonagemItem(
    personagem: PersonagemEntity,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "Nome: ${personagem.nome}")
        Text(text = "Raça: ${personagem.raca}")
        Text(text = "Nível: ${personagem.nivel}")
        Text(text = "Vida: ${personagem.vida}")

        // Exibe atributos e modificadores
        Text(text = "Atributos:")
        Text(text = "Força: ${personagem.forca} (Mod: ${calcularModificador(personagem.forca)})")
        Text(text = "Destreza: ${personagem.destreza} (Mod: ${calcularModificador(personagem.destreza)})")
        Text(text = "Constituição: ${personagem.constituicao} (Mod: ${calcularModificador(personagem.constituicao)})")
        Text(text = "Inteligência: ${personagem.inteligencia} (Mod: ${calcularModificador(personagem.inteligencia)})")
        Text(text = "Sabedoria: ${personagem.sabedoria} (Mod: ${calcularModificador(personagem.sabedoria)})")
        Text(text = "Carisma: ${personagem.carisma} (Mod: ${calcularModificador(personagem.carisma)})")

        // Botões para editar ou excluir
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onEditClick) {
                Text("Editar")
            }
            Button(onClick = onDeleteClick) {
                Text("Excluir")
            }
        }
    }
}

// Função para calcular modificadores
fun calcularModificador(valor: Int): Int {
    return when (valor) {
        1 -> -5
        2, 3 -> -4
        4, 5 -> -3
        6, 7 -> -2
        8, 9 -> -1
        10, 11 -> 0
        12, 13 -> 1
        14, 15 -> 2
        16, 17 -> 3
        18, 19 -> 4
        20, 21 -> 5
        22, 23 -> 6
        24, 25 -> 7
        26, 27 -> 8
        28, 29 -> 9
        30 -> 10
        else -> 0
    }
}
