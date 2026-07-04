package com.example.game

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

// --- Sleek Interface Theme Colors ---
val WineRed = Color(0xFFB3261E)       // Material 3 Red for danger / hangover
val DeepWineRed = Color(0xFFCAC4D0)   // #CAC4D0 Light border color
val WarmAmber = Color(0xFFEADDFF)     // #EADDFF Accent / selected nav item pill
val SoftGold = Color(0xFF21005D)      // #21005D Deep elegant violet for headers and titles
val TavernDark = Color(0xFFFEF7FF)    // #FEF7FF Sleek light background
val CardDark = Color(0xFFF7F2FA)      // #F7F2FA Light gray-purple card surface
val Parchment = Color(0xFFFFFFFF)     // Clean white background for contrast panels
val SoftText = Color(0xFF1D1B20)      // #1D1B20 Deep charcoal text for perfect legibility
val FoodColor = Color(0xFF0061A4)     // Material 3 Blue for food/fullness progress

@Composable
fun SupraGameApp(viewModel: SupraGameViewModel) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TavernDark
        ) {
            AnimatedContent(
                targetState = state.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    GameScreen.HOME -> HomeScreen(
                        onStart = { viewModel.changeScreen(GameScreen.CHARACTER_SELECTION) }
                    )
                    GameScreen.CHARACTER_SELECTION -> CharacterSelectionScreen(
                        characters = viewModel.characters,
                        onSelect = { viewModel.selectCharacter(it) },
                        onBack = { viewModel.changeScreen(GameScreen.HOME) }
                    )
                    GameScreen.GAMEPLAY -> GameplayScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    GameScreen.GAME_OVER -> GameOverScreen(
                        state = state,
                        onRestart = { viewModel.restartGame() }
                    )
                }
            }
        }
    }
}

// --- 1. HOME SCREEN ---
@Composable
fun HomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Title and Logo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.radialGradient(listOf(WarmAmber, Color.Transparent)),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = "Logo",
                    tint = SoftGold,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "სუფრის სიმულატორი",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SoftGold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "გახდი ნამდვილი თამადა!",
                fontSize = 18.sp,
                color = SoftText,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, WarmAmber, RoundedCornerShape(16.dp))
                .shadow(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_supra_banner),
                contentDescription = "ქართული სუფრა",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gradient for atmosphere
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome text & instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "მოგესალმებით ქართულ სუფრაზე!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftGold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "ეს არის სახალისო თამაში, სადაც მოგიწევს ტრადიციული სუფრის გაძღოლა, სადღეგრძელოების თქმა (AI-ის დახმარებით!), ჭამა, სმა და უბნის ავტორიტეტებისგან პატივისცემის მოპოვება. ფრთხილად იყავი, არ გაითიშო და ხინკალი ჩანგლით არ ჭამო!",
                    fontSize = 14.sp,
                    color = SoftText,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start Button
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
                .testTag("start_game_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = WarmAmber,
                contentColor = SoftGold
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = "სუფრის გაშლა 🍷",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- 2. CHARACTER SELECTION SCREEN ---
@Composable
fun CharacterSelectionScreen(
    characters: List<Character>,
    onSelect: (Character) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "უკან",
                    tint = SoftGold
                )
            }
            Text(
                text = "აირჩიე პერსონაჟი",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SoftGold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(characters) { char ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(char) }
                        .testTag("char_${char.id}"),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = BorderStroke(1.dp, DeepWineRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = char.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftGold
                                )
                                Text(
                                    text = char.nickname,
                                    fontSize = 14.sp,
                                    color = Color(0xFF65558F),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Icon or Avatar visual helper
                            val icon = when (char.id) {
                                "guram" -> Icons.Default.Person
                                "nika" -> Icons.Default.Fastfood
                                else -> Icons.Default.EmojiPeople
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = char.description,
                            fontSize = 13.sp,
                            color = SoftText,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Divider(color = DeepWineRed)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Trait Info Box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WarmAmber, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "უნარი",
                                tint = SoftGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "უნარი: ${char.traitName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftGold
                                )
                                Text(
                                    text = char.traitDescription,
                                    fontSize = 11.sp,
                                    color = SoftText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatMiniBadge(label = "პატივისცემა", value = char.initialRespect, color = SoftGold)
                            StatMiniBadge(label = "სიმთვრალე", value = char.initialDrunkness, color = WineRed)
                            StatMiniBadge(label = "მაქს. საჭმელი", value = char.foodLimit, color = FoodColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMiniBadge(label: String, value: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Parchment, RoundedCornerShape(4.dp))
            .border(BorderStroke(0.5.dp, DeepWineRed), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: $value",
            fontSize = 11.sp,
            color = SoftText
        )
    }
}

// --- 3. GAMEPLAY SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameplayScreen(
    state: GameState,
    viewModel: SupraGameViewModel
) {
    val currentStage = viewModel.stages.getOrNull(state.currentStageIndex)
    var customTopicText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = TavernDark,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3EDF7),
                modifier = Modifier.border(BorderStroke(1.dp, DeepWineRed))
            ) {
                NavigationBarItem(
                    selected = state.activeActionCategory == "TOAST",
                    onClick = { viewModel.setActionCategory("TOAST") },
                    icon = { Icon(Icons.Default.Celebration, contentDescription = "Toast") },
                    label = { Text("სადღეგრძელო") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SoftGold,
                        selectedTextColor = SoftGold,
                        indicatorColor = WarmAmber,
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
                NavigationBarItem(
                    selected = state.activeActionCategory == "DRINK",
                    onClick = { viewModel.setActionCategory("DRINK") },
                    icon = { Icon(Icons.Default.LocalBar, contentDescription = "Drink") },
                    label = { Text("სმა") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SoftGold,
                        selectedTextColor = SoftGold,
                        indicatorColor = WarmAmber,
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
                NavigationBarItem(
                    selected = state.activeActionCategory == "EAT",
                    onClick = { viewModel.setActionCategory("EAT") },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Eat") },
                    label = { Text("ჭამა") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SoftGold,
                        selectedTextColor = SoftGold,
                        indicatorColor = WarmAmber,
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
                NavigationBarItem(
                    selected = state.activeActionCategory == "ACTIVITY",
                    onClick = { viewModel.setActionCategory("ACTIVITY") },
                    icon = { Icon(Icons.Default.LocalActivity, contentDescription = "Activity") },
                    label = { Text("გართობა") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SoftGold,
                        selectedTextColor = SoftGold,
                        indicatorColor = WarmAmber,
                        unselectedIconColor = Color(0xFF49454F),
                        unselectedTextColor = Color(0xFF49454F)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // --- TOP STATS ---
            StatsDashboard(state = state)

            Spacer(modifier = Modifier.height(12.dp))

            // --- MAIN GAME AREA (DIALOGUE / LOGS / RANDOM SCENARIO) ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (state.activeEvent != null) {
                    // Show active random event scenario popup
                    RandomEventCard(
                        event = state.activeEvent,
                        onSelectChoice = { viewModel.selectEventChoice(it) }
                    )
                } else {
                    // Show standard gameplay logs & history
                    GameplayLogPanel(state = state, currentStage = currentStage)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- BOTTOM ACTION PANEL (Based on category) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = BorderStroke(1.dp, DeepWineRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    when (state.activeActionCategory) {
                        "TOAST" -> ToastCategoryPanel(
                            viewModel = viewModel,
                            state = state,
                            customTopic = customTopicText,
                            onTopicChange = { customTopicText = it }
                        )
                        "DRINK" -> DrinkCategoryPanel(
                            viewModel = viewModel,
                            state = state
                        )
                        "EAT" -> EatCategoryPanel(
                            viewModel = viewModel,
                            state = state
                        )
                        "ACTIVITY" -> ActivityCategoryPanel(
                            viewModel = viewModel,
                            state = state
                        )
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENTS FOR GAMEPLAY ---

@Composable
fun StatsDashboard(state: GameState) {
    val char = state.selectedCharacter ?: return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = BorderStroke(1.dp, DeepWineRed)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${char.name} 🍷",
                    fontWeight = FontWeight.Bold,
                    color = SoftGold,
                    fontSize = 16.sp
                )
                Text(
                    text = "სადღეგრძელო ${state.currentStageIndex + 1}/11",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF65558F),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat 1: Respect
            StatProgressBar(
                label = "პატივისცემა (Respect)",
                value = state.respect,
                maxValue = 100,
                barColor = SoftGold,
                icon = Icons.Default.Star
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stat 2: Drunkness
            StatProgressBar(
                label = "სიმთვრალე (Drunkness)",
                value = state.drunkness,
                maxValue = 100,
                barColor = WineRed,
                icon = Icons.Default.LocalBar
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stat 3: Fullness
            StatProgressBar(
                label = "დანაყრება (Fullness)",
                value = state.fullness,
                maxValue = char.foodLimit,
                barColor = FoodColor,
                icon = Icons.Default.Restaurant
            )
        }
    }
}

@Composable
fun StatProgressBar(
    label: String,
    value: Int,
    maxValue: Int,
    barColor: Color,
    icon: ImageVector
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = barColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, fontSize = 11.sp, color = SoftText, fontWeight = FontWeight.Medium)
            }
            Text("$value / $maxValue%", fontSize = 11.sp, color = SoftText, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { value.toFloat() / maxValue.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun GameplayLogPanel(state: GameState, currentStage: SupraStage?) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(1.dp, DeepWineRed), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Parchment)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Stage Indicator Header
            if (currentStage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarmAmber, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "ეტაპი: ${currentStage.name} სადღეგრძელო",
                            fontWeight = FontWeight.Bold,
                            color = SoftGold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = currentStage.description,
                            color = SoftText,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Scrollable Log
            Text(
                text = "სუფრის ქრონიკა:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF65558F),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false
                ) {
                    items(state.historyLog) { log ->
                        Text(
                            text = log,
                            color = if (log.startsWith("---") || log.startsWith("ეტაპი:")) SoftGold else SoftText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (log.contains("შეცდომა") || log.contains("ვაი!")) WineRed.copy(
                                        alpha = 0.15f
                                    ) else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RandomEventCard(event: TableEvent, onSelectChoice: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(2.dp, Color(0xFF65558F)), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "შემთხვევა",
                        tint = WineRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "სუფრული შემთხვევა: ${event.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = SoftText,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .background(Parchment, RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, DeepWineRed), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { onSelectChoice(0) },
                    modifier = Modifier.fillMaxWidth().testTag("event_choice_0"),
                    colors = ButtonDefaults.buttonColors(containerColor = WineRed, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = event.optionA, fontSize = 12.sp, textAlign = TextAlign.Center)
                }

                Button(
                    onClick = { onSelectChoice(1) },
                    modifier = Modifier.fillMaxWidth().testTag("event_choice_1"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DeepWineRed)
                ) {
                    Text(text = event.optionB, fontSize = 12.sp, textAlign = TextAlign.Center)
                }

                Button(
                    onClick = { onSelectChoice(2) },
                    modifier = Modifier.fillMaxWidth().testTag("event_choice_2"),
                    colors = ButtonDefaults.buttonColors(containerColor = CardDark, contentColor = SoftGold),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF65558F))
                ) {
                    Text(text = event.optionC, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// --- 4. CATEGORY PANELS FOR CORE ACTIONS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToastCategoryPanel(
    viewModel: SupraGameViewModel,
    state: GameState,
    customTopic: String,
    onTopicChange: (String) -> Unit
) {
    val stage = viewModel.stages.getOrNull(state.currentStageIndex) ?: return

    Column {
        Text(
            text = "თქვი სადღეგრძელო (Stage: ${stage.name})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (state.isAILoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SoftGold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "თამადა ფიქრობს სადღეგრძელოზე (AI გენერაცია)...",
                        color = SoftText,
                        fontSize = 12.sp
                    )
                }
            }
        } else if (state.generatedAIToast != null) {
            // Toast generated, user reads it and must drink to proceed!
            Column(
                modifier = Modifier
                    .background(Parchment, RoundedCornerShape(8.dp))
                    .border(BorderStroke(1.dp, DeepWineRed), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "შენი სიტყვა:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.generatedAIToast,
                    fontSize = 13.sp,
                    color = SoftText,
                    lineHeight = 18.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "🍷 ახლა ჭიქა უნდა ასწიო და დალიო, რომ სადღეგრძელო ძალაში შევიდეს!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF65558F)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.setActionCategory("DRINK") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = SoftGold)
                ) {
                    Text("გადასვლა დალევაზე 🍷")
                }
            }
        } else {
            // Let user choose toast style or write custom topic
            Text(
                text = "აირჩიე სადღეგრძელოს სტილი:",
                fontSize = 12.sp,
                color = SoftText,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.selectToast(ToastType.SHORT) },
                    modifier = Modifier.weight(1f).testTag("toast_short"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment),
                    border = BorderStroke(1.dp, DeepWineRed),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("მოკლე", fontSize = 11.sp, color = SoftText)
                }
                Button(
                    onClick = { viewModel.selectToast(ToastType.PHILOSOPHICAL) },
                    modifier = Modifier.weight(1f).testTag("toast_philo"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment),
                    border = BorderStroke(1.dp, DeepWineRed),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("ღრმა", fontSize = 11.sp, color = SoftText)
                }
                Button(
                    onClick = { viewModel.selectToast(ToastType.POETIC) },
                    modifier = Modifier.weight(1f).testTag("toast_poetic"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment),
                    border = BorderStroke(1.dp, DeepWineRed),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("პოეტური", fontSize = 11.sp, color = SoftText)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI custom topic field
            Card(
                colors = CardDefaults.cardColors(containerColor = Parchment),
                border = BorderStroke(1.dp, DeepWineRed)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "ან ჩაწერე საკუთარი თემა AI-ისთვის:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customTopic,
                        onValueChange = onTopicChange,
                        placeholder = { Text("მაგ: პროგრამისტები, მეგობრობა, მწვადი...", fontSize = 11.sp, color = SoftText.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("ai_topic_input"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = SoftText),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftGold,
                            unfocusedBorderColor = DeepWineRed
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { viewModel.selectToast(ToastType.AI_GENERATED, customTopic) },
                        modifier = Modifier.fillMaxWidth().testTag("generate_ai_toast_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = WineRed, contentColor = Color.White)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gemini-სადღეგრძელო ✨", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrinkCategoryPanel(
    viewModel: SupraGameViewModel,
    state: GameState
) {
    Column {
        Text(
            text = "ღვინის დალევა 🍷",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "სიმთვრალე არის ${state.drunkness}%. აირჩიე სმის ტაქტიკა:",
            fontSize = 12.sp,
            color = SoftText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.drinkWine(DrinkStyle.BOTTOMS_UP) },
                modifier = Modifier.weight(1f).height(50.dp).testTag("drink_normal"),
                colors = ButtonDefaults.buttonColors(containerColor = WineRed, contentColor = Color.White)
            ) {
                Text("ბოლომდე", fontSize = 11.sp)
            }

            Button(
                onClick = { viewModel.drinkWine(DrinkStyle.HORN) },
                modifier = Modifier.weight(1.0f).height(50.dp).testTag("drink_horn"),
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = SoftGold)
            ) {
                Text("ყანწით", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.drinkWine(DrinkStyle.FAKE_DRINK) },
                modifier = Modifier.weight(1f).height(50.dp).testTag("drink_fake"),
                colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                border = BorderStroke(1.dp, DeepWineRed)
            ) {
                Text("მოტყუება", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun EatCategoryPanel(
    viewModel: SupraGameViewModel,
    state: GameState
) {
    val char = state.selectedCharacter ?: return

    Column {
        Text(
            text = "მიირთვი ქართული კერძები 🍢",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = "დანაყრება: ${state.fullness}/${char.foodLimit}%. ჭამა ამცირებს სიმთვრალეს და ანელებს მის მატებას.",
            fontSize = 11.sp,
            color = SoftText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Food buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.eatFood(FoodType.KHACHAPURI) },
                    modifier = Modifier.weight(1f).testTag("eat_khachapuri"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                    border = BorderStroke(1.dp, DeepWineRed)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ხაჭაპური", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("+25% საჭმელი", fontSize = 9.sp, color = FoodColor)
                    }
                }

                Button(
                    onClick = { viewModel.eatFood(FoodType.MTSVADI) },
                    modifier = Modifier.weight(1f).testTag("eat_mtsvadi"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                    border = BorderStroke(1.dp, DeepWineRed)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("მწვადი", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("+30% საჭმელი", fontSize = 9.sp, color = FoodColor)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.eatFood(FoodType.KHINKALI_HANDS) },
                    modifier = Modifier.weight(1f).testTag("eat_khinkali_hands"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftGold),
                    border = BorderStroke(1.dp, Color(0xFF65558F))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ხინკალი (ხელით)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("+15% საჭმელი", fontSize = 9.sp, color = FoodColor)
                    }
                }

                Button(
                    onClick = { viewModel.eatFood(FoodType.KHINKALI_FORK) },
                    modifier = Modifier.weight(1f).testTag("eat_khinkali_fork"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = WineRed),
                    border = BorderStroke(1.dp, DeepWineRed)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ხინკალი (ჩანგლით!)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("საშიშია!", fontSize = 9.sp, color = WineRed)
                    }
                }
            }

            Button(
                onClick = { viewModel.eatFood(FoodType.PICKLES) },
                modifier = Modifier.fillMaxWidth().testTag("eat_pickles"),
                colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                border = BorderStroke(1.dp, DeepWineRed)
            ) {
                Text("ჯონჯოლი / მწნილი (+5% საჭმელი, -5% სიმთვრალე)", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ActivityCategoryPanel(
    viewModel: SupraGameViewModel,
    state: GameState
) {
    Column {
        Text(
            text = "სუფრული აქტივობები 🎻",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "გართობა დაგეხმარება დანაყრებისა და სიმთვრალის შემცირებაში.",
            fontSize = 12.sp,
            color = SoftText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.performActivity(ActivityType.DANCE) },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("activity_dance"),
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = SoftGold)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Celebration, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("გადი საცეკვაოდ (აჭარული)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.performActivity(ActivityType.RESTROOM) },
                    modifier = Modifier.weight(1f).height(45.dp).testTag("activity_restroom"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                    border = BorderStroke(1.dp, DeepWineRed)
                ) {
                    Text("ფეხზე გასვლა", fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.performActivity(ActivityType.SNEAK_OUT) },
                    modifier = Modifier.weight(1f).height(45.dp).testTag("activity_sneak"),
                    colors = ButtonDefaults.buttonColors(containerColor = Parchment, contentColor = SoftText),
                    border = BorderStroke(1.dp, DeepWineRed)
                ) {
                    Text("ჩუმად გაპარვა 🚪", fontSize = 11.sp)
                }
            }
        }
    }
}

// --- 5. GAME OVER / WIN SCREEN ---
@Composable
fun GameOverScreen(
    state: GameState,
    onRestart: () -> Unit
) {
    val ending = state.ending ?: GameEnding.PASSED_OUT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(
                    if (ending.isWin) WarmAmber else WineRed.copy(alpha = 0.15f),
                    RoundedCornerShape(45.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            val endIcon = if (ending.isWin) Icons.Default.EmojiEvents else Icons.Default.SentimentVeryDissatisfied
            Icon(
                imageVector = endIcon,
                contentDescription = null,
                tint = if (ending.isWin) SoftGold else WineRed,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (ending.isWin) "სუფრა წარმატებით დასრულდა! 🎉" else "თამაში დასრულდა 💀",
            fontSize = 16.sp,
            color = if (ending.isWin) SoftGold else WineRed,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = ending.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SoftGold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = BorderStroke(1.dp, DeepWineRed)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = ending.description,
                    fontSize = 14.sp,
                    color = SoftText,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = DeepWineRed)

                Spacer(modifier = Modifier.height(12.dp))

                // Score stats
                Text(
                    text = "შენი საბოლოო შედეგები:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF65558F),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("პატივისცემა (Respect):", fontSize = 13.sp, color = SoftText)
                    Text("${state.respect}%", fontSize = 13.sp, color = SoftGold, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("სიმთვრალე (Drunkness):", fontSize = 13.sp, color = SoftText)
                    Text("${state.drunkness}%", fontSize = 13.sp, color = WineRed, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("დასრულებული რაუნდები:", fontSize = 13.sp, color = SoftText)
                    Text("${state.roundsCompleted} / 11", fontSize = 13.sp, color = SoftText, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
                .testTag("restart_button"),
            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = SoftGold),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("ახალი სუფრა 🍷", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
