package com.example.appnewpersonagem

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.appnewpersonagem.viewmodel.PersonagemViewModel
import criarPersonagem
import listaDeRacas

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CriarPersonagemScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPersonagemScreen() {
    val context = LocalContext.current

    
    var nome by remember { mutableStateOf("") }
    val racasDisponiveis = listaDeRacas 
    var racaSelecionada by remember { mutableStateOf("") }
    var expandedRaca by remember { mutableStateOf(false) }

    val atributos = listOf(
        "forca", "destreza", "constituicao",
        "inteligencia", "sabedoria", "carisma"
    )
    val valoresAtributos = remember { mutableStateMapOf<String, String>() }
    val expandedAtributos = remember { mutableStateMapOf<String, Boolean>() }
    atributos.forEach { atributo ->
        valoresAtributos.getOrPut(atributo) { "8" }
        expandedAtributos.getOrPut(atributo) { false }
    }

    
    var pontosRestantes by remember { mutableStateOf(27) }
    val custoPorNivel = mapOf(
        8 to 0,
        9 to 1,
        10 to 2,
        11 to 3,
        12 to 4,
        13 to 5,
        14 to 7,
        15 to 9
    )

    val valoresPossiveisAtributos = (8..15).map { it.toString() }

    fun calcularPontosRestantes() {
        val totalGasto = atributos.sumOf { atributo ->
            val valor = valoresAtributos[atributo]?.toIntOrNull() ?: 8
            val custo = custoPorNivel[valor] ?: 0
            custo
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
                value = nome,
                onValueChange = { nome = it },
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
                                            val custo = custoPorNivel[valorAttr] ?: 0
                                            custo
                                        } else {
                                            0
                                        }
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
                if (nome.isNotBlank() && racaSelecionada.isNotBlank() && valoresAtributos.values.all { it.isNotBlank() }) {
                    
                    val atributosInt = valoresAtributos.mapValues { it.value.toInt() }

                    
                    val personagem = criarPersonagem(nome, racaSelecionada, atributosInt)



                    if (personagem != null) {
                        val application = context.applicationContext as Application

                        val viewModel = PersonagemViewModel(application)

                        viewModel.inserirPersonagem(personagem)

                        detalhesPersonagem = personagem.getDetalhes()
                        
                        showDialog = true
                    } else {
                        
                        Toast.makeText(context, "Erro ao criar personagem.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    
                    Toast.makeText(context, "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = pontosRestantes >= 0 
        ) {
            Text(text = "Concluir")
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
