package com.example.appnewpersonagem.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appnewpersonagem.SecondActivity
import com.example.appnewpersonagem.data.PersonagemEntity
import com.example.appnewpersonagem.viewmodel.PersonagemViewModel

@Composable
fun ConsultarPersonagemScreen(viewModel: PersonagemViewModel) {
    LaunchedEffect(Unit) {
        viewModel.obterTodosPersonagens()
    }

    val personagens by viewModel.personagens.observeAsState(emptyList())
    val context = LocalContext.current

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
                    onEditClick = {
                        // Navegar para a tela de edição, passando o ID do personagem
                        val intent = Intent(context, SecondActivity::class.java).apply {
                            putExtra("personagemId", personagem.id)
                        }
                        context.startActivity(intent)
                    },
                    onConsultClick = {
                        // Exibir o diálogo de consulta com os detalhes do personagem
                        viewModel.obterPersonagemPorId(personagem.id)
                    }
                )
            }
        }
    }
}

@Composable
fun PersonagemItem(
    personagem: PersonagemEntity,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onConsultClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "Nome: ${personagem.nome}")
        Text(text = "Raça: ${personagem.raca}")
        Text(text = "Nível: ${personagem.nivel}")
        Text(text = "Vida: ${personagem.vida}")

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
            Button(onClick = { showDialog = true }) {
                Text("Consultar")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Detalhes do Personagem") },
                text = {
                    Column {
                        Text("Nome: ${personagem.nome}")
                        Text("Raça: ${personagem.raca}")
                        Text("Nível: ${personagem.nivel}")
                        Text("Vida: ${personagem.vida}")
                        Text("Força: ${personagem.forca} (Mod: ${calcularModificador(personagem.forca)})")
                        Text("Destreza: ${personagem.destreza} (Mod: ${calcularModificador(personagem.destreza)})")
                        Text("Constituição: ${personagem.constituicao} (Mod: ${calcularModificador(personagem.constituicao)})")
                        Text("Inteligência: ${personagem.inteligencia} (Mod: ${calcularModificador(personagem.inteligencia)})")
                        Text("Sabedoria: ${personagem.sabedoria} (Mod: ${calcularModificador(personagem.sabedoria)})")
                        Text("Carisma: ${personagem.carisma} (Mod: ${calcularModificador(personagem.carisma)})")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Fechar")
                    }
                }
            )
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