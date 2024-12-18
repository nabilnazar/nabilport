package com.nabilnazar.videoplayerapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.nabilnazar.videoplayerapp.ui.theme.VideoPlayerAppTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class MainActivity : ComponentActivity() {
    @kotlin.OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycle.addObserver(YouTubePlayerView(this@MainActivity))
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VideoViewModel = viewModel()
            VideoPlayerAppTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text("Video App") }
                            )
                        }
                    ) { innerPadding ->
                        VideoApp(viewModel, innerPadding, this@MainActivity)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoApp(viewModel: VideoViewModel, innerPadding: PaddingValues, activity: MainActivity) {
    val videoList by viewModel.videoList.collectAsState()
    val selectedVideoUrl by viewModel.selectedVideoUrl.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        if (selectedVideoUrl == null) {
            VideoListScreen(videoList, activity) { url ->
                viewModel.selectVideo(url)
            }
        } else {
            VideoPlayerScreen(selectedVideoUrl!!) {
                viewModel.selectVideo(null)
            }
        }
    }
}

@Composable
fun VideoListScreen(
    videoList: List<String>,
    activity: MainActivity,
    onVideoSelected: (String) -> Unit
) {
    var youtubeUrl by remember { mutableStateOf("") }
    var selectedVideoId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        // LazyColumn for video list
        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(videoList.size) { index ->
                val videoUrl = videoList[index]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable { onVideoSelected(videoUrl) }
                        .padding(16.dp)
                ) {
                    Text(
                        text = videoUrl.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // YouTube Player view
        selectedVideoId?.let { videoId ->
            AndroidView(factory = { context ->
                YouTubePlayerView(context).apply {
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            super.onReady(youTubePlayer)
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    })
                }
            })
        }

        // Row for URL input and Play button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            TextField(
                value = youtubeUrl,
                onValueChange = { youtubeUrl = it },
                placeholder = { Text("Paste YouTube link here...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(onClick = {
                val videoId = extractVideoIdFromUrl(youtubeUrl)
                if (videoId != null) {
                    selectedVideoId = videoId
                } else {
                    Toast.makeText(activity, "Invalid YouTube URL", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Play")
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(videoUrl: String, onBack: () -> Unit) {
    val context = LocalContext.current

    // ExoPlayer setup
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                .createMediaSource(MediaItem.fromUri(videoUrl))
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ExoPlayer View
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Back button at the top
        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text("Back", color = Color.White)
        }
    }
}

// Helper function to extract video ID
fun extractVideoIdFromUrl(url: String): String? {
    val regex =
        "(?:v=|youtu\\.be/|embed/|v/|u/\\w+/|e/|shorts/|\\?vi?=)([^#&?]*)(?:[?&].*)?".toRegex()
    return regex.find(url)?.groupValues?.get(1)
}


