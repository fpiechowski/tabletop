package tabletop.client

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.navigation.Navigation

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

@ExperimentalComposeUiApi
class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dependencies().run {
                Navigator(ConnectionScreen(this@run)) {
                    navigation.complete(Navigation(userInterface, it))

                    Scaffold {
                        Box(
                            Modifier.padding(
                                start = it.calculateStartPadding(LayoutDirection.Ltr),
                                end = it.calculateEndPadding(LayoutDirection.Ltr),
                            ).fillMaxSize()
                        ) {
                            CurrentScreen()
                        }
                    }
                }
            }
        }
    }
}
