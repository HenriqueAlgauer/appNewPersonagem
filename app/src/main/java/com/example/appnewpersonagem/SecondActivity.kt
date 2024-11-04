package com.example.appnewpersonagem

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appnewpersonagem.data.Mapper.toPersonagem
import com.example.appnewpersonagem.data.PersonagemEntity
import com.example.appnewpersonagem.viewmodel.PersonagemViewModel
import criarPersonagem
import listaDeRacas
import personagem.Personagem

class SecondActivity : ComponentActivity() {
    private val viewModel: PersonagemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CriarPersonagemScreen(
                viewModel = viewModel,
                onSave = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPersonagemScreen(
    viewModel: PersonagemViewModel,
    personagemExistente: PersonagemEntity? = null,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    // Estados para os atributos do personagem, utilizando valores do personagemExistente, se presente
    val nome = remember { mutableStateOf(personagemExistente?.nome ?: "") }
    var racaSelecionada by remember { mutableStateOf(personagemExistente?.raca ?: "") }
    val valoresAtributos = remember {
        mutableStateMapOf(
            "forca" to (personagemExistente?.forca?.toString() ?: "8"),
            "destreza" to (personagemExistente?.destreza?.toString() ?: "8"),
            "constituicao" to (personagemExistente?.constituicao?.toString() ?: "8"),
            "inteligencia" to (personagemExistente?.inteligencia?.toString() ?: "8"),
            "sabedoria" to (personagemExistente?.sabedoria?.toString() ?: "8"),
            "carisma" to (personagemExistente?.carisma?.toString() ?: "8")
        )
    }

    val racasDisponiveis = listaDeRacas
    var expandedRaca by remember { mutableStateOf(false) }

    val atributos = listOf(
        "forca", "destreza", "constituicao",
        "inteligencia", "sabedoria", "carisma"
    )

    val expandedAtributos = remember { mutableStateMapOf<String, Boolean>() }
    atributos.forEach { atributo ->
        expandedAtributos.getOrPut(atributo) { false }
    }

    var pontosRestantes by remember { mutableStateOf(27) }
    val custoPorNivel = mapOf(8 to 0, 9 to 1, 10 to 2, 11 to 3, 12 to 4, 13 to 5, 14 to 7, 15 to 9)
    val valoresPossiveisAtributos = (8..15).map { it.toString() }

    // Função para calcular os pontos restantes
    fun calcularPontosRestantes() {
        val totalGasto = atributos.sumOf { atributo ->
            val valor = valoresAtributos[atributo]?.toIntOrNull() ?: 8
            custoPorNivel[valor] ?: 0
        }
        pontosRestantes = 27 - totalGasto
    }

    var showDialog by remember { mutableStateOf(false) }
    var detalhesPersonagem by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {

            OutlinedTextField(
                value = nome.value,
                onValueChange = { nome.value = it },
                label = { Text("Nome do Personagem") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedRaca,
                onExpandedChange = { expandedRaca = !expandedRaca }
            ) {
                OutlinedTextField(
                    value = racaSelecionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Raça") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRaca)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRaca,
                    onDismissRequest = { expandedRaca = false }
                ) {
                    racasDisponiveis.forEach { raca ->
                        DropdownMenuItem(
                            text = { Text(text = raca) },
                            onClick = {
                                racaSelecionada = raca
                                expandedRaca = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pontos restantes: $pontosRestantes",
                style = MaterialTheme.typography.bodyLarge
            )

            atributos.forEach { atributo ->
                val expandedAtributo = expandedAtributos[atributo] ?: false

                ExposedDropdownMenuBox(
                    expanded = expandedAtributo,
                    onExpandedChange = {
                        expandedAtributos[atributo] = !expandedAtributo
                    }
                ) {
                    OutlinedTextField(
                        value = valoresAtributos[atributo] ?: "8",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(atributo.capitalize()) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAtributo)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAtributo,
                        onDismissRequest = {
                            expandedAtributos[atributo] = false
                        }
                    ) {
                        valoresPossiveisAtributos.forEach { valor ->
                            DropdownMenuItem(
                                text = { Text(text = valor) },
                                onClick = {
                                    val intValue = valor.toInt()
                                    val custoAtual = custoPorNivel[intValue] ?: 0
                                    val totalGastoSemEste = atributos.sumOf { attr ->
                                        if (attr != atributo) {
                                            val valorAttr = valoresAtributos[attr]?.toIntOrNull() ?: 8
                                            custoPorNivel[valorAttr] ?: 0
                                        } else 0
                                    }
                                    val pontosRestantesPossiveis = 27 - totalGastoSemEste - custoAtual
                                    if (pontosRestantesPossiveis >= 0) {
                                        valoresAtributos[atributo] = valor
                                        calcularPontosRestantes()
                                        expandedAtributos[atributo] = false
                                    } else {
                                        Toast.makeText(context, "Pontos insuficientes", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (nome.value.isNotBlank() && racaSelecionada.isNotBlank()) {
                    val personagemEntity = PersonagemEntity(
                        id = personagemExistente?.id ?: 0,
                        nome = nome.value,
                        raca = racaSelecionada,
                        forca = valoresAtributos["forca"]?.toInt() ?: 8,
                        destreza = valoresAtributos["destreza"]?.toInt() ?: 8,
                        constituicao = valoresAtributos["constituicao"]?.toInt() ?: 8,
                        inteligencia = valoresAtributos["inteligencia"]?.toInt() ?: 8,
                        sabedoria = valoresAtributos["sabedoria"]?.toInt() ?: 8,
                        carisma = valoresAtributos["carisma"]?.toInt() ?: 8
                    )

                    // Salvar ou atualizar
                    if (personagemExistente == null) {
                        viewModel.inserirPersonagem(personagemEntity.toPersonagem(racaSelecionada))
                    } else {
                        viewModel.editarPersonagem(personagemEntity)
                    }

                    onSave() // Retornar após salvar
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = pontosRestantes >= 0
        ) {
            Text(if (personagemExistente == null) "Concluir" else "Salvar")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Personagem Criado") },
                text = { Text(detalhesPersonagem) },
                confirmButton = {
                    TextButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
