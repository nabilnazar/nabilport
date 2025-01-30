package com.nabilnazar.deck74dragandrop

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nabilnazar.deck74dragandrop.ui.theme.Deck74dragandropTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deck74dragandropTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        Column(
                            verticalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.padding(48.dp)
                        ) {
                            DragImage(url = "https://images.performgroup.com/di/library/omnisport/25/3a/b78684dd345542db9770f6485f367559.jpg?t=-418815667&w=1200&h=630")
                            DropTargetImage(url = "https://e0.365dm.com/20/07/1600x900/skysports-lionel-messi-barcelona_5044145.jpg?20200719193014")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DragImage(url: String) {
    GlideImage(
        model = url,
        contentDescription = "Dragged Image",
        modifier = Modifier.dragAndDropSource {
            detectTapGestures(
                onLongPress = {
                    startTransfer(
                        DragAndDropTransferData(
                            ClipData.newPlainText("image uri", url)
                        )
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun DropTargetImage(url: String) {
    var urlState by remember { mutableStateOf(url) }
    var tintColor by remember { mutableStateOf(Color(0xffE5E4E2)) }

    val dndTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val draggedData = event.toAndroidDragEvent()
                    .clipData.getItemAt(0).text
                urlState = draggedData.toString()
                return true
            }

            override fun onEntered(event: DragAndDropEvent) {
                tintColor = Color(0xff00ff00)
            }

            override fun onEnded(event: DragAndDropEvent) {
                tintColor = Color(0xffE5E4E2)
            }

            override fun onExited(event: DragAndDropEvent) {
                tintColor = Color(0xffE5E4E2)
            }
        }
    }

    GlideImage(
        model = urlState,
        contentDescription = "Dropped Image",
        colorFilter = ColorFilter.tint(
            color = tintColor,
            blendMode = BlendMode.Modulate
        ),
        modifier = Modifier.dragAndDropTarget(
            shouldStartDragAndDrop = { event ->
                event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
            },
            target = dndTarget
        )
    )
}
