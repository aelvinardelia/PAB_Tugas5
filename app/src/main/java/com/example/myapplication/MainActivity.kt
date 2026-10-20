
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import com.example.myapplication.ui.theme.MyApplicationTheme

private val Purple = Color(0xFF7357E8)
private val LightPurple = Color(0xFFF0ECFF)
private val Background = Color(0xFFF8F7FC)
private val DarkText = Color(0xFF29243D)
private val MutedText = Color(0xFF817C91)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                DailyFlowApp()
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: String) {
    HOME("Home", "🏠"),
    TASKS("Tasks", "✅"),
    FAVORITES("Favorites", "💜"),
    PROFILE("Profile", "👤")
}

@PreviewScreenSizes
@Composable
fun DailyFlowApp() {
    var destination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME.name)
    }

    var tasks by rememberSaveable {
        mutableStateOf(
            listOf(
                "Menyelesaikan tugas kuliah",
                "Membaca materi hari ini",
                "Olahraga 15 menit",
                "Merapikan meja belajar"
            )
        )
    }

    var completed by rememberSaveable {
        mutableStateOf(setOf<Int>())
    }

    var favorites by rememberSaveable {
        mutableStateOf(setOf("Membaca buku", "Journaling"))
    }

    var newTask by rememberSaveable { mutableStateOf("") }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { item ->
                item(
                    icon = {
                        Text(item.icon, fontSize = 21.sp)
                    },
                    label = { Text(item.label) },
                    selected = destination == item.name,
                    onClick = { destination = item.name }
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            when (destination) {
                AppDestinations.HOME.name -> HomeScreen(
                    tasks = tasks,
                    completed = completed,
                    onNavigate = { destination = it.name }
                )

                AppDestinations.TASKS.name -> TasksScreen(
                    tasks = tasks,
                    completed = completed,
                    newTask = newTask,
                    onNewTaskChange = { newTask = it },
                    onAddTask = {
                        val task = newTask.trim()
                        if (task.isNotEmpty()) {
                            tasks = tasks + task
                            newTask = ""
                        }
                    },
                    onToggleTask = { index ->
                        completed = if (index in completed) {
                            completed - index
                        } else {
                            completed + index
                        }
                    }
                )

                AppDestinations.FAVORITES.name -> FavoritesScreen(
                    favorites = favorites,
                    onToggle = { name ->
                        favorites = if (name in favorites) {
                            favorites - name
                        } else {
                            favorites + name
                        }
                    }
                )

                else -> ProfileScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(
    tasks: List<String>,
    completed: Set<Int>,
    onNavigate: (AppDestinations) -> Unit
) {
    val progress = if (tasks.isEmpty()) 0f
    else completed.count { it in tasks.indices }.toFloat() / tasks.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        contentPadding = PaddingValues(top = 30.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                "DAILYFLOW ✨",
                color = Purple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Make today\ncount.",
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Pelan-pelan, yang penting tetap berjalan.",
                color = MutedText,
                fontSize = 14.sp
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Purple),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(22.dp)) {
                    Text("Today's progress ☀️", color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${(progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color.White,
                        trackColor = Color(0xFFAA99F5)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "${completed.count { it in tasks.indices }} dari ${tasks.size} tugas selesai",
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                "Quick overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Total tasks",
                    value = "${tasks.size}",
                    emoji = "📝",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = "${completed.count { it in tasks.indices }}",
                    emoji = "🎉",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Ready to be productive?",
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Yuk, cek tugas kamu hari ini.",
                            color = MutedText,
                            fontSize = 13.sp
                        )
                    }
                    Button(
                        onClick = { onNavigate(AppDestinations.TASKS) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple
                        )
                    ) {
                        Text("Lihat")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                value,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(title, fontSize = 12.sp, color = MutedText)
        }
    }
}

@Composable
fun TasksScreen(
    tasks: List<String>,
    completed: Set<Int>,
    newTask: String,
    onNewTaskChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onToggleTask: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Text(
            "My Tasks 📝",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
        )
        Spacer(Modifier.height(6.dp))
        Text("Satu per satu, kamu pasti bisa.", color = MutedText)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = newTask,
            onValueChange = onNewTaskChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Tulis tugas baru...") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onAddTask,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            )
        ) {
            Text("+ Tambah Tugas", modifier = Modifier.padding(4.dp))
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "${completed.count { it in tasks.indices }} / ${tasks.size} selesai",
            color = Purple,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(tasks) { index, task ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = index in completed,
                            onCheckedChange = { onToggleTask(index) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Purple
                            )
                        )
                        Text(
                            task,
                            modifier = Modifier.weight(1f),
                            color = if (index in completed) MutedText else DarkText,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(
                            onClick = {
                                onToggleTask(index)
                            }
                        ) {
                            Text(
                                if (index in completed) "Undo" else "Done",
                                color = Purple
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    favorites: Set<String>,
    onToggle: (String) -> Unit
) {
    val options = listOf(
        "Membaca buku" to "📚",
        "Journaling" to "📔",
        "Olahraga" to "🏃",
        "Mendengarkan musik" to "🎧",
        "Me time" to "☕",
        "Belajar hal baru" to "💡"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        contentPadding = PaddingValues(vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Little Joys 💜",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Pilih hal-hal yang bikin kamu bahagia.",
                color = MutedText
            )
            Spacer(Modifier.height(10.dp))
        }

        itemsIndexed(options) { _, option ->
            val (name, emoji) = option
            val selected = name in favorites

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) LightPurple else Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        name,
                        modifier = Modifier.weight(1f),
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = { onToggle(name) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Purple else Color(0xFFE9E5F4)
                        )
                    ) {
                        Text(
                            if (selected) "♥" else "♡",
                            color = if (selected) Color.White else Purple,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Favorit kamu: ${favorites.size} kegiatan",
                color = Purple,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    var name by rememberSaveable { mutableStateOf("Adel") }
    var bio by rememberSaveable {
        mutableStateOf("Trying to become my best self ✨")
    }
    var notifications by rememberSaveable {
        mutableStateOf(true)
    }
    var showMessage by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(104.dp)
                .background(LightPurple, RoundedCornerShape(36.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌷", fontSize = 48.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "My Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
        )
        Text("Small steps, big changes.", color = MutedText)

        Spacer(Modifier.height(26.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Nama kamu", color = MutedText, fontSize = 13.sp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(14.dp))

                Text("Bio", color = MutedText, fontSize = 13.sp)
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Daily reminders",
                            color = DarkText,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Pengaturan pengingat demo",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = notifications,
                        onCheckedChange = { notifications = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Purple
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { showMessage = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {
                    Text("Simpan Profil")
                }

                if (showMessage) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Profil berhasil diperbarui! ✨",
                        color = Purple,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
