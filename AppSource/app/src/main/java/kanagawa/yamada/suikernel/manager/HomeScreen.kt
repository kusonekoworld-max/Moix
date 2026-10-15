package kanagawa.yamada.suikernel.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

private val MoixPrimary = Color(0xFFD0BCFF)
private val MoixSurface = Color(0xFF171821)
private val MoixCard = Color(0xFF222431)
private val MoixMuted = Color(0xFFBFC2D3)
private val MoixGreen = Color(0xFFB7F0C0)

@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToRecomendedModules: () -> Unit = {}
) {
    val context = LocalContext.current
    val appVersion = remember {
        try { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0" }
        catch (_: Exception) { "1.0" }
    }
    var rootState by remember { mutableStateOf<Boolean?>(null) }
    var kernelText by remember { mutableStateOf("Checking kernel…") }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            rootState = checkRootAccess()
            kernelText = try {
                val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat /proc/version"))
                val value = p.inputStream.bufferedReader().use { it.readLine() ?: "Kernel information unavailable" }
                p.waitFor()
                value.substringAfter("Linux version ", value).take(100)
            } catch (_: Exception) { "Unavailable without working su access" }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MoixSurface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("MOIX", color = MoixPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Text("Kernel Manager", color = MaterialTheme.colorScheme.onBackground, fontSize = 29.sp, fontWeight = FontWeight.SemiBold)
                Text("Native kernel control center", color = MoixMuted, fontSize = 14.sp)
            }
            Surface(shape = RoundedCornerShape(18.dp), color = MoixCard) {
                Icon(Icons.Outlined.Memory, contentDescription = "Kernel", tint = MoixPrimary,
                    modifier = Modifier.padding(15.dp).size(30.dp))
            }
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier.background(Brush.linearGradient(listOf(Color(0xFF403568), Color(0xFF252B49), Color(0xFF20232F))))
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = MoixPrimary)
                        Text("PRIVILEGE STATUS", color = MoixPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                    }
                    Text(
                        when (rootState) { true -> "Root access available"; false -> "Root access unavailable"; null -> "Checking access…" },
                        color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        when (rootState) {
                            true -> "The su service answered the app's root check."
                            false -> "This app has not obtained UID 0. Check your kernel root solution or su manager."
                            null -> "Checking whether a privileged su service is present."
                        },
                        color = Color(0xFFE2DDF5), fontSize = 14.sp, lineHeight = 20.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusChip(if (rootState == true) "ROOT READY" else if (rootState == false) "NOT ROOTED" else "CHECKING",
                            if (rootState == true) MoixGreen else Color(0xFFFFD8A8))
                        StatusChip("APP v$appVersion", MoixPrimary)
                    }
                }
            }
        }

        Text("OVERVIEW", color = MoixMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoTile("Kernel", "Native driver", Icons.Outlined.Memory, Modifier.weight(1f))
            InfoTile("Control", "Performance", Icons.Outlined.Bolt, Modifier.weight(1f))
        }

        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MoixCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("RUNNING KERNEL", color = MoixMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text(kernelText, color = Color.White, fontSize = 14.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Text("Kernel details are informational. A displayed version does not prove that the driver's control interface works.",
                    color = MoixMuted, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }

        Text("TOOLS", color = MoixMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
        ActionTile("Kernel settings", "Configure built-in driver options", Icons.Outlined.Tune, onNavigateToSettings)
        ActionTile("Recommended modules", "Explore compatible extensions", Icons.Outlined.Bolt, onNavigateToRecomendedModules)

        Text("MOIX KERNEL MANAGER  •  Material 3", color = MoixMuted, fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp, bottom = 12.dp))
    }
}

@Composable
private fun StatusChip(label: String, tint: Color) {
    Surface(color = tint.copy(alpha = 0.14f), contentColor = tint, shape = RoundedCornerShape(50)) {
        Text(label, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
            fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun InfoTile(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MoixCard)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = MoixPrimary, modifier = Modifier.size(24.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(subtitle, color = MoixMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ActionTile(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MoixCard)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(16.dp), color = MoixPrimary.copy(alpha = 0.13f)) {
                Icon(icon, contentDescription = null, tint = MoixPrimary, modifier = Modifier.padding(12.dp).size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = MoixMuted, fontSize = 13.sp)
            }
            Text("›", color = MoixPrimary, fontSize = 28.sp)
        }
    }
}

fun checkRootAccess(): Boolean {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
        val output = process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor()
        process.exitValue() == 0 && output.contains("uid=0")
    } catch (_: Exception) { false }
}
