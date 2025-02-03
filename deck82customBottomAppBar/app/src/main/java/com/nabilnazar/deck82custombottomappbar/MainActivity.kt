package com.nabilnazar.deck82custombottomappbar

import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.JoinFull
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(
        Icons.Filled.AccountCircle,
        Icons.Filled.Bookmarks,
        Icons.Filled.CalendarMonth,
        Icons.Filled.Dashboard,
        Icons.Filled.Email,
        Icons.Filled.Favorite,
        Icons.Filled.Group,
        Icons.Filled.Headphones,
        Icons.Filled.Image,
        Icons.Filled.JoinFull,
        Icons.Filled.Keyboard,
        Icons.Filled.Laptop,
        Icons.Filled.Map,
        Icons.Filled.Navigation,
        Icons.Filled.Outbox,
        Icons.Filled.PushPin,
        Icons.Filled.QrCode,
        Icons.Filled.Radio,
    )
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(item.name.substringAfterLast(".")) },
                            selected = item == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (drawerState.isClosed) ">>> Swipe >>>" else "<<< Swipe <<<")
                Spacer(Modifier.height(20.dp))
                Button(onClick = { scope.launch { drawerState.open() } }) {
                    Text("Click to open")
                }
            }
        }
    )
}
//@Composable
//fun AppContent() {
//    val screens = listOf("Profile", "Settings", "Home", "Notifications", "More")
//    val icons = listOf(
//        Icons.Filled.Person,
//        Icons.Filled.Settings,
//        Icons.Filled.Home,
//        Icons.Filled.Notifications,
//        Icons.Filled.Star
//    )
//    var selectedIndex by remember { mutableStateOf(2) }
//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//
//    Scaffold(
//        bottomBar = {
//            CustomScrollableNavBar(
//                selectedIndex = selectedIndex,
//                screens = screens,
//                icons = icons,
//                onScreenChange = { newIndex ->
//                    selectedIndex = newIndex
//                    coroutineScope.launch {
//                        listState.animateScrollToItem(newIndex)
//                    }
//                },
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            Modifier
//                .fillMaxSize()
//                .background(Color(0xFFEEEEEE))
//                .padding(paddingValues),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = screens[selectedIndex], style = MaterialTheme.typography.headlineMedium)
//        }
//    }
//}
//
//@Composable
//fun CustomScrollableNavBar(
//    selectedIndex: Int,
//    screens: List<String>,
//    icons: List<ImageVector>,
//    onScreenChange: (Int) -> Unit,
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White, RoundedCornerShape(16.dp))
//            .padding(8.dp)
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { change, dragAmount ->
//                    change.consume()
//                    val newIndex = (selectedIndex - (dragAmount / 200)).coerceIn(0f, (screens.size - 1).toFloat()).toInt()
//                    if (newIndex != selectedIndex) {
//                        onScreenChange(newIndex)
//                    }
//                }
//            },
//        contentAlignment = Alignment.Center
//    ) {
//        LazyRow(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceAround,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            items(screens.size) { index ->
//                val isSelected = selectedIndex == index
//                AnimatedIconButton(isSelected, icons[index], onClick = { onScreenChange(index) })
//            }
//        }
//    }
//}
//
//@Composable
//fun AnimatedIconButton(
//    isSelected: Boolean,
//    icon: ImageVector,
//    onClick: () -> Unit
//) {
//    val transition = updateTransition(targetState = isSelected, label = "IconTransition")
//
//    val rotation by transition.animateFloat(
//        label = "IconRotation",
//        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
//    ) { selected -> if (selected) 360f else 0f }
//
//    val iconColor by transition.animateColor(
//        label = "IconColor",
//        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
//    ) { selected -> if (selected) Color.Blue else Color.Gray }
//
//    val iconSize by transition.animateDp(
//        label = "IconSize",
//        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
//    ) { selected -> if (selected) 48.dp else 32.dp }
//
//    IconButton(onClick = onClick) {
//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            tint = iconColor,
//            modifier = Modifier
//                .size(iconSize)
//                .rotate(rotation)
//        )
//    }
//}
//
