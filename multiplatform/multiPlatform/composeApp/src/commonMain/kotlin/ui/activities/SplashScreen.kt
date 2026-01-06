package ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import multiplatform.composeapp.generated.resources.Res
import multiplatform.composeapp.generated.resources.eposte
import org.jetbrains.compose.resources.painterResource



@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.eposte),
            contentDescription = "ePoste",
            modifier = Modifier.size(220.dp)
        )
    }
}