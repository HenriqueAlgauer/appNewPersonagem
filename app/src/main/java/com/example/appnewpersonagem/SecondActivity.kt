package com.example.appnewpersonagem

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appnewpersonagem.data.Mapper.toPersonagem
import com.example.appnewpersonagem.data.PersonagemEntity
import com.example.appnewpersonagem.viewmodel.PersonagemViewModel
import listaDeRacas

class SecondActivity : ComponentActivity() {
    private val viewModel: PersonagemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if this is for editing or creating a new character
        val personagemId = intent.getIntExtra("personagemId", -1)
        if (personagemId != -1) {
            viewModel.obterPersonagemPorId(personagemId)
        }

        setContent {
            // Observe personagemSelecionado to load it when available for editing
            val personagemExistente by viewModel.personagemSelecionado.observeAsState()

            // If personagemId was provided but no personagem was found, show error
            if (personagemId != -1 && personagemExistente == null) {
                Toast.makeText(this, "Personagem não encontrado.", Toast.LENGTH_SHORT).show()
                finish() // Close activity if no character found
            } else {
                CriarPersonagemScreen(
                    viewModel = viewModel,
                    personagemExistente = personagemExistente,
                    onSave = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPersonagemScreen(
    viewModel: PersonagemViewModel,
    personagemExistente: PersonagemEntity?,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    // Check if we are editing or creating a character
    val isEditMode = personagemExistente != null
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
    var pontosRestantes by remember { mutableStateOf(27) }
    val custoPorNivel = mapOf(8 to 0, 9 to 1, 10 to 2, 11 to 3, 12 to 4, 13 to 5, 14 to 7, 15 to 9)
    val valoresPossiveisAtributos = (8..15).map { it.toString() }

    fun calcularPontosRestantes() {
        val totalGasto = valoresAtributos.values.sumOf { valor ->
            val valorInt = valor.toIntOrNull() ?: 8
            custoPorNivel[valorInt] ?: 0
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

            valoresAtributos.keys.forEach { atributo ->
                var expandedAtributo by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expandedAtributo,
                    onExpandedChange = {
                        expandedAtributo = !expandedAtributo
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
                            expandedAtributo = false
                        }
                    ) {
                        valoresPossiveisAtributos.forEach { valor ->
                            DropdownMenuItem(
                                text = { Text(text = valor) },
                                onClick = {
                                    val intValue = valor.toInt()
                                    val custoAtual = custoPorNivel[intValue] ?: 0
                                    val totalGastoSemEste = valoresAtributos.entries.sumOf { entry ->
                                        if (entry.key != atributo) {
                                            custoPorNivel[entry.value.toInt()] ?: 0
                                        } else {
                                            0
                                        }
                                    }
                                    val pontosRestantesPossiveis = 27 - totalGastoSemEste - custoAtual
                                    if (pontosRestantesPossiveis >= 0) {
                                        valoresAtributos[atributo] = valor
                                        calcularPontosRestantes()
                                        expandedAtributo = false
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

                    if (isEditMode) {
                        viewModel.editarPersonagem(personagemEntity)
                    } else {
                        viewModel.inserirPersonagem(personagemEntity.toPersonagem(racaSelecionada))
                    }

                    onSave()
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = pontosRestantes >= 0
        ) {
            Text(if (isEditMode) "Salvar" else "Concluir")
        }
    }
}
