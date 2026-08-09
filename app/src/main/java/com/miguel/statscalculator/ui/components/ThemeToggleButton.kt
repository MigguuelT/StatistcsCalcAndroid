package com.miguel.statscalculator.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    IconButton(onClick = onToggleTheme) {
        Crossfade(targetState = isDarkMode, label = "ThemeSwitch") { dark ->
            if (dark) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Mudar para Tema Claro",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Mudar para Tema Escuro",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}