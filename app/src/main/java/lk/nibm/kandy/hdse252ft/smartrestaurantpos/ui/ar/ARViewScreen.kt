package lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val menuItems by viewModel.menuItems.collectAsState()
    val item = menuItems.find { it.id == itemId }

    // ── Camera permission ─────────────────────────────────────────────────
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    // Show permission UI if not granted
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E1B16)
                    )
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
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1B16)
                    ),
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

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = { cameraPermission.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary
                            ),
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

    // ── SceneView 2.x remembered resources ───────────────────────────────
    val engine          = rememberEngine()
    val modelLoader     = rememberModelLoader(engine)
    val materialLoader  = rememberMaterialLoader(engine)
    val cameraNode      = rememberARCameraNode(engine)
    val childNodes      = rememberNodes()
    val view            = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)

    var planeDetected by remember { mutableStateOf(false) }
    var arFrame by remember { mutableStateOf<Frame?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "View in AR: ${item?.name ?: ""}",
                        color = GoldPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── ARScene ───────────────────────────────────────────────────
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.depthMode = when (
                        session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
                    ) {
                        true  -> Config.DepthMode.AUTOMATIC
                        false -> Config.DepthMode.DISABLED
                    }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode  = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = true,
                onSessionUpdated = { _, frame ->
                    arFrame = frame
                    if (!planeDetected &&
                        frame.getUpdatedTrackables(Plane::class.java).isNotEmpty()
                    ) {
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
                                    trackable is Plane &&
                                            trackable.isPoseInPolygon(hitResult.hitPose)
                                }
                                ?.let { hitResult ->
                                    val anchorNode = AnchorNode(
                                        engine = engine,
                                        anchor = hitResult.createAnchor()
                                    )
                                    childNodes += anchorNode
                                }
                        }
                    }
                )
            )

            // ── Ingredient info overlay cards ─────────────────────────────
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

            // ── Bottom instruction pill ───────────────────────────────────
            val instruction = when {
                !planeDetected -> "Point camera at a flat surface..."
                else           -> "Tap on the surface to place ${item?.name ?: "item"}"
            }

            Text(
                text = instruction,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(
                        Color.Black.copy(alpha = 0.55f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}