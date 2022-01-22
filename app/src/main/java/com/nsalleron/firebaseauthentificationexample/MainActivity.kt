package com.nsalleron.firebaseauthentificationexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nsalleron.firebaseauthentificationexample.ui.theme.FirebaseAuthentificationExampleTheme

class MainActivity : ComponentActivity() {

    private var user: FirebaseUser? = null

    // Choose authentication providers
    private val providers = arrayListOf(
        // AuthUI.IdpConfig.EmailBuilder().build(),
        // AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        // AuthUI.IdpConfig.FacebookBuilder().build(),
        // AuthUI.IdpConfig.TwitterBuilder().build(),
    )

    // Create and launch sign-in intent
    private val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        user = auth.currentUser
        setContent {
            Render()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // force SignOut
        AuthUI.getInstance().signOut(this)
        setContent {
            user?.let {
                Greeting(it)
            } ?: signInLauncher.launch(signInIntent)
        }
    }

    @Composable
    private fun Render() {
        FirebaseAuthentificationExampleTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colors.background) {
                user?.let {
                    Greeting(it)
                } ?: ShowTryAgain()
            }
        }
    }

    @Composable
    fun Greeting(firebaseUser: FirebaseUser) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = CenterHorizontally,) {

            Text(text = "Hello ${firebaseUser.displayName}!", fontWeight = FontWeight.Bold, fontSize = 30.sp, fontStyle = FontStyle.Italic,)

            Column {
                Title(title = "Common data")
                Spacer(modifier = Modifier.height(16.dp))
                textWithData(field = "Email", data = "${firebaseUser.email}")
                textWithData(
                    field = "isEmailVerified",
                    data = "${firebaseUser.isEmailVerified}",
                )
                textWithData(
                    field = "isAnonymous",
                    data = "${firebaseUser.isAnonymous}",
                )
                textWithData(field = "photoUrl", data = "${firebaseUser.photoUrl}")
                textWithData(
                    field = "phoneNumber",
                    data = "${firebaseUser.phoneNumber}",
                )
            }

            Column {
                Title(title = "Analytics data")
                Spacer(modifier = Modifier.height(16.dp))
                textWithData(
                    field = "metadata",
                    data = "${firebaseUser.metadata}",
                )
                textWithData(
                    field = "multiFactor",
                    data = "${firebaseUser.multiFactor}",
                )
                textWithData(
                    field = "providerId",
                    data = firebaseUser.providerId,
                )
                textWithData(
                    field = "tenantId",
                    data = "${firebaseUser.tenantId}",
                )
                textWithData(field = "uid", data = firebaseUser.uid,)
            }

            Button(onClick = {
                AuthUI.getInstance().signOut(this@MainActivity).addOnCompleteListener {
                    setContent {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = CenterHorizontally,
                        ) {

                            Button(onClick = {
                                signInLauncher.launch(signInIntent)
                            }) {
                                Text("SignIn")
                            }
                        }
                    }
                }
            }) {
                Text(text = "Logout")
            }
        }
    }

    @Composable
    private fun Title(title: String) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontStyle = FontStyle.Italic)
    }
    @Composable
    private fun textWithData(field: String, data: String) {
        Row {
            Text(text = "$field : ", fontWeight = FontWeight.Bold)
            Text(text = data)
        }
    }

    @Composable
    private fun ShowTryAgain() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally,
        ) {
            Text(text = "Oops ...!", fontSize = 30.sp)
            Text(text = "Something went wrong.", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                signInLauncher.launch(signInIntent)
            }) {
                Text("try again")
            }
        }
    }

    @VisibleForTesting
    fun inject(user: FirebaseUser): MainActivity {
        this.user = user
        return this
    }
}
