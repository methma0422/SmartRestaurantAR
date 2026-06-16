package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.theme.GoldPrimary
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ARViewScreen(
    navController: NavController,
    itemId: String?,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val menuItems by viewModel.menuItems.collectAsState()
    val item = menuItems.find { it.id == itemId }
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    val isArSupported = remember(context) {
        context.packageManager.hasSystemFeature("android.hardware.camera.ar")
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    if (!isArSupported) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("View in AR", color = GoldPrimary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GoldPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1B16))
                )
            },
            containerColor = Color(0xFF121212)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B16)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "AR not supported on this device",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "This phone does not expose the required AR hardware feature, so the preview cannot be launched.",
                            color = Color(0xFFB8A990),
                            fontSize = 14.sp
                        )
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Go Back", color = Color(0xFF1A1200), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    if (!cameraPermission.status.isGranted) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("View in AR", color = GoldPrimary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GoldPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1B16))
                )
            },
            containerColor = Color(0xFF121212)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B16)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "📷", fontSize = 48.sp)
                        Text(
                            text = "Camera Permission Required",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F0E8),
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (cameraPermission.status.shouldShowRationale) {
                                "Camera access was denied. Please grant camera permission to use the AR feature and view ingredients on your dish."
                            } else {
                                "This feature uses your camera to overlay ingredient information on your dish in augmented reality."
                            },
                            color = Color(0xFFB8A990),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { cameraPermission.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Grant Camera Access",
                                color = Color(0xFF1A1200),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFB8A990)
                            )
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
        return
    }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val cameraNode = rememberARCameraNode(engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)

    var planeDetected by remember { mutableStateOf(false) }
    var arFrame by remember { mutableStateOf<Frame?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "View in AR: ${item?.name ?: ""}", color = GoldPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        false -> Config.DepthMode.DISABLED
                    }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = true,
                onSessionUpdated = { _, frame ->
                    arFrame = frame
                    if (!planeDetected && frame.getUpdatedTrackables(Plane::class.java).isNotEmpty()) {
                        planeDetected = true
                    }
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            arFrame
                                ?.hitTest(motionEvent)
                                ?.firstOrNull { hitResult ->
                                    val trackable = hitResult.trackable
                                    trackable is Plane && trackable.isPoseInPolygon(hitResult.hitPose)
                                }
                                ?.let { hitResult ->
                                    val anchorNode = AnchorNode(
                                        engine = engine,
                                        anchor = hitResult.createAnchor()
                                    )

                                    val modelPath = "models/$itemId.glb"
                                    val fallbackPath = "models/food_placeholder.glb"
                                    var finalModelPath: String? = null

                                    try {
                                        context.assets.open(modelPath).close()
                                        finalModelPath = modelPath
                                    } catch (e: Exception) {
                                        try {
                                            context.assets.open(fallbackPath).close()
                                            finalModelPath = fallbackPath
                                        } catch (ex: Exception) {
                                            // Neither model file exists
                                        }
                                    }

                                    if (finalModelPath != null) {
                                        val modelInstance = modelLoader.createModelInstance(finalModelPath)
                                        val modelNode = ModelNode(
                                            modelInstance = modelInstance
                                        ).apply {
                                            scale = io.github.sceneview.math.Scale(0.3f, 0.3f, 0.3f)
                                            isEditable = true
                                        }
                                        anchorNode.addChildNode(modelNode)
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "No 3D asset model found in assets. Placed reference anchor.",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    childNodes += anchorNode
                                }
                        }
                    }
                )
            )

            item?.let { menuItem ->
                if (menuItem.ingredients.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .width(160.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        menuItem.ingredients.take(4).forEach { ingredient ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF2E5E2E).copy(alpha = 0.88f)
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    Text(
                                        text = ingredient.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                    ingredient.benefits.forEach { benefit ->
                                        Text(
                                            text = "• $benefit",
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val instruction = when {
                !planeDetected -> "Point camera at a flat surface..."
                else -> "Tap on the surface to place ${item?.name ?: "item"}"
            }

            Text(
                text = instruction,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
