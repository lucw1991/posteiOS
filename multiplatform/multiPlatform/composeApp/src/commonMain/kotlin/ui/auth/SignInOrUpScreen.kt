package ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.layout.ContentScale
import multiplatform.composeapp.generated.resources.Res
import multiplatform.composeapp.generated.resources.eposte
import org.jetbrains.compose.resources.painterResource



@Composable
fun SignInOrUpScreen(onAuthSuccess: () -> Unit,
                     onSkip: (() -> Unit)? = null) {

    var isSignIn by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(32.dp))

        // Logo at top of login.
        Image(
            painter = painterResource(Res.drawable.eposte),
            contentDescription = "ePoste",
            modifier = Modifier.fillMaxWidth().height(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(20.dp))

        if (isSignIn) {
            SignInForm(onSubmit = onAuthSuccess)
        } else {
            SignUpForm(onSubmit = onAuthSuccess)
        }

        Spacer(Modifier.height(18.dp))

        // Toggle row
        Text(
            text = if (isSignIn) "Don't have an account? " else "Already have an account? ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (isSignIn) "Sign Up" else "Log In",
            color = Color(0xFF357497),
            modifier = Modifier.clickable {
                isSignIn = !isSignIn
            }
        )

        // Skip button
        if (onSkip != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onSkip,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Skip login")
            }

        }
    }
}


@Composable
private fun SignInForm(onSubmit: () -> Unit) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email") })
        Spacer(Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(Modifier.height(16.dp))

        // Hook up actual auth implementation later.
        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF357497))
        ) {
            Text("Get Started")
        }

    }
}


@Composable
private fun SignUpForm(onSubmit: () -> Unit) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text("Email")
            })
        Spacer(Modifier.height(10.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF357497))
        ) {
            Text("Sign Up")
        }
    }

}