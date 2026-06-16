package lk.nibm.kandy.hdse252ft.smartrestaurantpos

import android.content.Intent
import android.net.Uri
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
        val deepLinkTable = extractTableFromIntent(intent)
        setContent {
            SmartRestaurantPOSTheme {
                NavGraph(deepLinkTableNumber = deepLinkTable)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun extractTableFromIntent(intent: Intent?): Int? {
        val uri: Uri = intent?.data ?: return null
        if (uri.scheme != "thegoldenoak" || uri.host != "menu") return null
        return uri.getQueryParameter("table")?.toIntOrNull()
    }
}
