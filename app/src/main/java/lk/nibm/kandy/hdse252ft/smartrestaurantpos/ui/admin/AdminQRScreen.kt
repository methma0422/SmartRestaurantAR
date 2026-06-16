package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.admin

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.utils.QrCodeGenerator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamMuted
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.CreamWhite
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.SurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQRScreen(navController: NavController) {
    var tableNumber by remember { mutableIntStateOf(1) }
    var qrSize by remember { mutableFloatStateOf(512f) }
    val context = LocalContext.current

    val deepLink = QrCodeGenerator.getTableDeepLink(tableNumber)
    val qrBitmap = remember(tableNumber, qrSize) {
        QrCodeGenerator.generateQrBitmap(deepLink, qrSize.toInt())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Table QR Codes",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0C0B0A),
                            Color(0xFF171311),
                            SurfaceDark
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Select Table Number",
                    color = CreamWhite,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Generate and save QR codes for dining tables",
                    color = CreamMuted,
                    fontSize = 13.sp
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2E2722), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151210))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "TABLE $tableNumber",
                        style = MaterialTheme.typography.headlineLarge,
                        color = GoldPrimary,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Slider(
                        value = tableNumber.toFloat(),
                        onValueChange = { tableNumber = it.toInt().coerceIn(1, 20) },
                        valueRange = 1f..20f,
                        steps = 18,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = Color(0xFF2E2722),
                            thumbColor = GoldPrimary,
                            activeTickColor = Color(0xFF151210),
                            inactiveTickColor = GoldPrimary.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // QR Code display container
            Card(
                modifier = Modifier
                    .size(260.dp)
                    .border(1.dp, GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White) // keep white background for perfect QR scanning!
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code for Table $tableNumber",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Deep-link address card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1715)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = deepLink,
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }

            Button(
                onClick = {
                    saveQrToGallery(context, qrBitmap, tableNumber)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF1E1B18))
                Text(" DOWNLOAD QR CODE", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, color = Color(0xFF1E1B18), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Print this QR code and place it on Table $tableNumber.\nScanning will launch the client menu locked to this table.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = CreamMuted,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

private fun saveQrToGallery(context: Context, bitmap: Bitmap, tableNumber: Int) {
    val filename = "golden_oak_table_${tableNumber}_qr.png"
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                filename,
                "QR code for table $tableNumber"
            )
        }
        Toast.makeText(context, "QR saved to gallery successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save QR: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
