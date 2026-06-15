package lk.nibm.kandy.hdse252ft.smartrestaurantpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.NavGraph
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SmartRestaurantPOSTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartRestaurantPOSTheme {
                NavGraph()
            }
        }
    }
}
