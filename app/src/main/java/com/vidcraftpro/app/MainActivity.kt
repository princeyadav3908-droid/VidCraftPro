package com.vidcraftpro.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView

private val Bg = Color(0xFF070B16)
private val Panel = Color(0xFF111A2B)
private val Purple = Color(0xFF7957FF)
private val Cyan = Color(0xFF27D9D0)
private val Muted = Color(0xFFAAB5CB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VidCraftApp() }
    }
}

private enum class Screen(val title: String, val subtitle: String) {
    HOME("VidCraft Pro", "Edit beyond limits"),
    EDITOR("Video Editor", "Timeline and preview"),
    BACKGROUND("Background Studio", "AI cutout and manual masks"),
    COLOR("Color Lab", "Replace, remove and glow"),
    BLUR("Selective Blur", "Control exactly where blur appears"),
    AUDIO("Audio Studio", "Mix, shape and sync audio"),
    GRADING("Color Grading", "Make every frame look right"),
    EXPORT("Export", "Choose output quality")
}

@Composable
private fun VidCraftApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) { selectedVideo = uri; screen = Screen.EDITOR } }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Purple,
            secondary = Cyan,
            background = Bg,
            surface = Panel
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onNewProject = { picker.launch("video/*") },
                    onOpen = { screen = it }
                )
                Screen.EDITOR -> EditorScreen(
                    videoUri = selectedVideo,
                    onPick = { picker.launch("video/*") },
                    onTool = { screen = it },
                    onBack = { screen = Screen.HOME }
                )
                else -> ToolScreen(screen = screen, onBack = { screen = Screen.HOME })
            }
        }
    }
}

@Composable
private fun HomeScreen(onNewProject: () -> Unit, onOpen: (Screen) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(46.dp).background(
                        Brush.linearGradient(listOf(Purple, Cyan)),
                        RoundedCornerShape(14.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp)) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("VidCraft Pro", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("EDIT BEYOND LIMITS", fontSize = 10.sp, color = Muted, letterSpacing = 2.sp)
                }
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }
        item {
            Box(
                Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Color(0xFF20245C), Color(0xFF102D42), Color(0xFF17102D))),
                    RoundedCornerShape(24.dp)
                ).padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Create. Edit. Share.", fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Your mobile video studio starts here.", color = Muted)
                    Button(onClick = onNewProject, colors = ButtonDefaults.buttonColors(containerColor = Purple)) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(6.dp))
                        Text("New Video Project")
                    }
                }
            }
        }
        item { Text("POWERFUL TOOLS", color = Muted, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolCard("Background Studio", "AI cutout • masks • tracking", Icons.Default.Person, Cyan) { onOpen(Screen.BACKGROUND) }
                ToolCard("Color Lab", "Color replace • remove • glow", Icons.Default.ColorLens, Purple) { onOpen(Screen.COLOR) }
                ToolCard("Selective Blur", "Brush • shape • tracking", Icons.Default.BlurOn, Color(0xFFFFA044)) { onOpen(Screen.BLUR) }
                ToolCard("Audio Studio", "Music • voice • mixing", Icons.Default.GraphicEq, Color(0xFF3898FF)) { onOpen(Screen.AUDIO) }
                ToolCard("Color Grading", "Curves • HSL • LUT workflow", Icons.Default.Tune, Color(0xFFFF5C9B)) { onOpen(Screen.GRADING) }
                ToolCard("Export Settings", "Resolution • frame rate • quality", Icons.Default.FileUpload, Cyan) { onOpen(Screen.EXPORT) }
            }
        }
        item {
            Text(
                "Starter build • Editing controls are being developed in stages",
                color = Muted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ToolCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(48.dp).background(accent.copy(alpha = .16f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(27.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Muted)
    }
}

@Composable
private fun EditorScreen(videoUri: Uri?, onPick: () -> Unit, onTool: (Screen) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val player = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
            videoUri?.let { setMediaItem(MediaItem.fromUri(it)); prepare() }
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }

    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
            Text("Video Editor", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onPick) { Text("Change video") }
        }
        Box(Modifier.fillMaxWidth().weight(1f).background(Color.Black, RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
            if (videoUri != null) {
                AndroidView(
                    factory = { ctx -> PlayerView(ctx).apply { this.player = player; useController = true } },
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                )
            } else {
                Text("Import a video to preview", color = Muted)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("EDITING TOOLS", color = Muted, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallTool("Background", Icons.Default.Person, Modifier.weight(1f)) { onTool(Screen.BACKGROUND) }
                SmallTool("Color Lab", Icons.Default.ColorLens, Modifier.weight(1f)) { onTool(Screen.COLOR) }
                SmallTool("Blur", Icons.Default.BlurOn, Modifier.weight(1f)) { onTool(Screen.BLUR) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallTool("Audio", Icons.Default.GraphicEq, Modifier.weight(1f)) { onTool(Screen.AUDIO) }
                SmallTool("Grading", Icons.Default.Tune, Modifier.weight(1f)) { onTool(Screen.GRADING) }
                SmallTool("Export", Icons.Default.FileUpload, Modifier.weight(1f)) { onTool(Screen.EXPORT) }
            }
        }
    }
}

@Composable
private fun SmallTool(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.background(Panel, RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Cyan)
        Spacer(Modifier.height(6.dp))
        Text(title, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun ToolScreen(screen: Screen, onBack: () -> Unit) {
    var amount by remember(screen) { mutableFloatStateOf(0.5f) }
    var mode by remember(screen) { mutableStateOf("Auto") }
    val descriptions = when (screen) {
        Screen.BACKGROUND -> listOf("AI Subject Cutout", "Brush / Eraser", "Motion Tracking", "Edge Refinement", "Background: Transparent / Color / Image / Video")
        Screen.COLOR -> listOf("Smart Color Cutout", "Replace Color", "Multi-color Selection", "Glow by Color", "Tolerance & Edge Feather")
        Screen.BLUR -> listOf("Brush / Eraser / Shape", "Track selected area", "Linear / Radial / Gaussian", "Multiple masks")
        Screen.AUDIO -> listOf("Music and voice tracks", "Equalizer / Compressor", "Noise reduction", "Fade and volume automation")
        Screen.GRADING -> listOf("Exposure / Contrast / Saturation", "Highlights / Shadows / Temperature", "Curves / HSL / LUT")
        Screen.EXPORT -> listOf("720p / 1080p / 4K options", "24 / 30 / 60 FPS options", "Quality and destination")
        else -> emptyList()
    }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
            Column {
                Text(screen.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(screen.subtitle, color = Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().height(210.dp).background(
            Brush.linearGradient(listOf(Color(0xFF20265C), Panel, Color(0xFF0B3137))),
            RoundedCornerShape(22.dp)
        ), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    when (screen) {
                        Screen.BACKGROUND -> Icons.Default.Person
                        Screen.COLOR -> Icons.Default.ColorLens
                        Screen.BLUR -> Icons.Default.BlurOn
                        Screen.AUDIO -> Icons.Default.GraphicEq
                        Screen.GRADING -> Icons.Default.Tune
                        else -> Icons.Default.FileUpload
                    }, null, tint = Cyan, modifier = Modifier.size(54.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("Import a video from the editor to work here", color = Color.White)
                Text("Tool interface foundation", color = Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        if (screen == Screen.COLOR) {
            Text("Selection mode", color = Color.White, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                listOf("Replace", "Remove", "Glow").forEach { option ->
                    FilterChip(selected = mode == option, onClick = { mode = option }, label = { Text(option) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Color tolerance", color = Color.White)
            Slider(value = amount, onValueChange = { amount = it })
            Text("Edge feather", color = Color.White)
            Slider(value = 1f - amount, onValueChange = { amount = 1f - it })
        } else if (screen == Screen.BACKGROUND) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Auto", "Select", "Track", "Refine").forEach { option ->
                    FilterChip(selected = mode == option, onClick = { mode = option }, label = { Text(option) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Selection refinement", color = Color.White)
            Slider(value = amount, onValueChange = { amount = it })
        } else {
            Text("Controls", color = Color.White, fontWeight = FontWeight.Bold)
            Slider(value = amount, onValueChange = { amount = it })
        }
        Spacer(Modifier.height(10.dp))
        descriptions.forEach { item ->
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircleOutline, null, tint = Cyan, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(10.dp))
                Text(item, color = Color.White)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(16.dp)
        ) { Text(if (screen == Screen.EXPORT) "Export pipeline — next stage" else "Apply — processing to be implemented") }
        Text(
            "Note: these controls are UI prototypes; the actual effect engine is the next development stage.",
            color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp)
        )
    }
}
