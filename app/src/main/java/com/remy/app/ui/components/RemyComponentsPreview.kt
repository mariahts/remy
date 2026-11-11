package com.remy.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.TextoSecundario

@Preview(name = "RemyLogo", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRemyLogo() {
    REMYTheme {
        RemyLogo(size = 112.dp, conBorde = true)
    }
}

@Preview(name = "RemyTopBar", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRemyTopBar() {
    REMYTheme {
        RemyTopBar(titulo = "Mi despensa")
    }
}

@Preview(name = "RemyCard", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRemyCard() {
    REMYTheme {
        RemyCard(modifier = Modifier.padding(16.dp)) {
            Text("Contenido de la tarjeta")
        }
    }
}

@Preview(name = "Botones", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewBotones() {
    REMYTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(text = "Entrar a mi cocina", onClick = {})
            OutlineButton(text = "Continuar con Google", onClick = {})
        }
    }
}

@Preview(name = "RemyChip", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRemyChip() {
    REMYTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RemyChip(text = "Todas", selected = true, onClick = {})
            RemyChip(text = "Rápidas", selected = false, onClick = {})
        }
    }
}

@Preview(name = "RemySearchField", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRemySearchField() {
    REMYTheme {
        RemySearchField(
            value = "",
            onValueChange = {},
            placeholder = "Buscar en mi despensa",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "StatusBadge", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewStatusBadge() {
    REMYTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusBadge("Lista para cocinar", VarianteBadge.LISTA)
            StatusBadge("Faltan 2", VarianteBadge.FALTAN)
            StatusBadge("Caduca pronto", VarianteBadge.PRONTO)
            StatusBadge("Caducado", VarianteBadge.CADUCADO)
            StatusBadge("Sin caducidad", VarianteBadge.NEUTRO)
        }
    }
}

@Preview(name = "RecipePhoto", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRecipePhoto() {
    REMYTheme {
        RecipePhoto(
            model = null,
            nombre = "Arroz con pollo",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(180.dp)
        )
    }
}

@Preview(name = "IngredientIcon", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewIngredientIcon() {
    REMYTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IngredientIcon("Pollo")
            IngredientIcon("Tomate")
            IngredientIcon("Arroz")
            IngredientIcon("Leche")
            IngredientIcon("Huevo")
        }
    }
}

@Preview(name = "RemyBottomBar", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewRemyBottomBar() {
    REMYTheme {
        Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
            Text(
                "Barra inferior",
                modifier = Modifier.padding(16.dp),
                color = TextoSecundario
            )
            RemyBottomBar(
                destinos = listOf(
                    DestinoBarra("inicio", "Inicio", Icons.Outlined.CalendarMonth),
                    DestinoBarra("despensa", "Despensa", Icons.Outlined.Kitchen),
                    DestinoBarra("recetas", "Recetas", Icons.Outlined.MenuBook)
                ),
                rutaActual = "inicio",
                onNavegar = {}
            )
        }
    }
}
