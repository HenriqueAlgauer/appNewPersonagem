package com.example.appnewpersonagem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.appnewpersonagem.viewmodel.PersonagemViewModel
import com.example.appnewpersonagem.ui.ConsultarPersonagemScreen

class ConsultarPersonagemActivity : ComponentActivity() {

    private val viewModel: PersonagemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConsultarPersonagemScreen(viewModel)
        }
    }
}
