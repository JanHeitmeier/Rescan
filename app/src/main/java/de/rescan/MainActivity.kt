package de.rescan

import ItemSelectScreen
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.rescan.scan.ScanScreen
import de.rescan.ui.theme.GreenDark
import de.rescan.ui.theme.GreenLight
import de.rescan.ui.theme.RescanTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RescanTheme {
                // Request the necessary permissions
                RequestPermissions()

                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "greeting",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("greeting") {
                            GreetingScreen(modifier = Modifier.padding(innerPadding))
                        }
                        composable("scan") {
                            ScanScreen(navController)
                        }
                        composable("itemSelect/{imageUri}") { backStackEntry ->
                            val encodedUri = backStackEntry.arguments?.getString("imageUri")
                            val imageUri = encodedUri?.let {
                                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                            }
                            imageUri?.let { ItemSelectScreen(it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestPermissions() {
    // Create a launcher to request multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // The result is a map of permissions to whether they were granted.
            // No further handling is done here per your instructions.
        }
    )

    // Launch the permission request when this composable is first composed.
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { navController.navigate("greeting") }) {
                    Icon(Icons.Default.Menu, contentDescription = "Home")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate("scan") }) {
                    Icon(Icons.Default.Add, contentDescription = "Scan")
                }
            }
        },
        modifier = Modifier.shadow(4.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GreenLight
        )
    )
}

@Composable
fun GreetingScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to \n  Rescan",
        color = GreenDark,
        fontSize = 40.sp,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Bold,
        lineHeight = 50.sp,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}
