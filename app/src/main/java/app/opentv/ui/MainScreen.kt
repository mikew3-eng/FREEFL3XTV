/*
 * FREEFL3X TV
 * Home screen redesign based on OpenTV.
 */
package app.opentv.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.opentv.core.AppSettings
import app.opentv.core.findActivity
import app.opentv.data.model.Channel
import app.opentv.data.model.Movie
import app.opentv.data.model.Recording
import app.opentv.data.model.Series
import app.opentv.ui.channels.HomeScreen
import app.opentv.ui.recordings.RecordingsScreen
import app.opentv.ui.vod.MoviesScreen
import app.opentv.ui.vod.SeriesScreen

private enum class FreeflexTab {
    HOME,
    LIVE,
    MOVIES,
    SHOWS,
    RECORDINGS
}

@Composable
fun MainScreen(
    isTelevision: Boolean,
    hasSources: Boolean,
    isSyncing: Boolean,
    onPlayChannel: (Channel) -> Unit,
    onOpenMovie: (Movie) -> Unit,
    onOpenSeries: (Series) -> Unit,
    onResume: (mediaKey: String, url: String, title: String) -> Unit,
    onAddSource: () -> Unit,
    onRefresh: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProfiles: () -> Unit,
    onPlayRecording: (Recording) -> Unit,
    onPlayCatchup: (mediaKey: String, url: String, title: String, ua: String) -> Unit,
    activeProfileName: String,
) {
    val context = LocalContext.current

    val settings = remember {
        AppSettings.get(context)
    }

    val liveEnabled by settings.liveEnabled.collectAsState()
    val moviesEnabled by settings.moviesEnabled.collectAsState()
    val seriesEnabled by settings.seriesEnabled.collectAsState()

    var currentTab by remember {
        mutableStateOf(FreeflexTab.HOME)
    }

    var showExit by remember {
        mutableStateOf(false)
    }

    BackHandler {
        if (currentTab != FreeflexTab.HOME) {
            currentTab = FreeflexTab.HOME
        } else {
            showExit = true
        }
    }

    if (showExit) {
        AlertDialog(
            onDismissRequest = {
                showExit = false
            },
            title = {
                Text("Exit FREEFL3X TV?")
            },
            text = {
                Text("Are you sure you want to close FREEFL3X TV?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExit = false
                        context.findActivity()?.finish()
                    }
                ) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExit = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        FreeflexSideBar(
            currentTab = currentTab,
            onHome = {
                currentTab = FreeflexTab.HOME
            },
            onLive = {
                currentTab = FreeflexTab.LIVE
            },
            onMovies = {
                currentTab = FreeflexTab.MOVIES
            },
            onShows = {
                currentTab = FreeflexTab.SHOWS
            },
            onRecordings = {
                currentTab = FreeflexTab.RECORDINGS
            },
            onSearch = onOpenSearch,
            onSettings = onOpenSettings,
            onProfiles = onOpenProfiles,
            activeProfileName = activeProfileName,
            liveEnabled = liveEnabled,
            moviesEnabled = moviesEnabled,
            seriesEnabled = seriesEnabled
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {

            when (currentTab) {

                FreeflexTab.HOME -> {
                    FreeflexHomeScreen(
                        activeProfileName = activeProfileName,
                        hasSources = hasSources,
                        onWatchLive = {
                            currentTab = FreeflexTab.LIVE
                        },
                        onOpenMovies = {
                            currentTab = FreeflexTab.MOVIES
                        },
                        onOpenShows = {
                            currentTab = FreeflexTab.SHOWS
                        },
                        onOpenRecordings = {
                            currentTab = FreeflexTab.RECORDINGS
                        },
                        onOpenSettings = onOpenSettings,
                        onOpenProfiles = onOpenProfiles
                    )
                }

                FreeflexTab.LIVE -> {
                    HomeScreen(
                        isTelevision = isTelevision,
                        hasSources = hasSources,
                        isSyncing = isSyncing,
                        onPlayChannel = onPlayChannel,
                        onAddSource = onAddSource,
                        onRefresh = onRefresh,
                        onPlayCatchup = onPlayCatchup
                    )
                }

                FreeflexTab.MOVIES -> {
                    MoviesScreen(
                        onOpenMovie = onOpenMovie,
                        onResume = onResume,
                        onOpenSearch = onOpenSearch,
                        hasSources = hasSources,
                        isSyncing = isSyncing
                    )
                }

                FreeflexTab.SHOWS -> {
                    SeriesScreen(
                        onOpenSeries = onOpenSeries,
                        onResume = onResume,
                        onOpenSearch = onOpenSearch,
                        hasSources = hasSources,
                        isSyncing = isSyncing
                    )
                }

                FreeflexTab.RECORDINGS -> {
                    RecordingsScreen(
                        onPlay = onPlayRecording
                    )
                }
            }
        }
    }
}


/* ---------------------------------------------------------
   FREEFL3X TV HOME SCREEN
   --------------------------------------------------------- */

@Composable
private fun FreeflexHomeScreen(
    activeProfileName: String,
    hasSources: Boolean,
    onWatchLive: () -> Unit,
    onOpenMovies: () -> Unit,
    onOpenShows: () -> Unit,
    onOpenRecordings: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProfiles: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 42.dp,
                end = 42.dp,
                top = 32.dp,
                bottom = 28.dp
            )
    ) {

        /* HEADER */

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "FREEFL3X TV",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Welcome back, $activeProfileName",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Your entertainment, your way.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onOpenProfiles
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Profile")
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )


        /* WATCH LIVE */

        Button(
            onClick = onWatchLive,
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Watch Live",
                modifier = Modifier.size(30.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = "WATCH LIVE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(32.dp)
        )


        /* CONTINUE WATCHING */

        SectionTitle(
            title = "Continue Watching"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(
                listOf(
                    "Continue Watching",
                    "Recently Watched",
                    "Your Library",
                    "Watch Again"
                )
            ) { title ->

                FreeflexMediaCard(
                    title = title,
                    subtitle = "Continue watching",
                    icon = Icons.Default.PlayArrow,
                    onClick = onOpenMovies
                )
            }
        }


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        /* LIVE TV */

        SectionTitle(
            title = "Live TV"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(
                listOf(
                    "Live Channels",
                    "TV Guide",
                    "Favorites",
                    "Recently Watched"
                )
            ) { title ->

                FreeflexMediaCard(
                    title = title,
                    subtitle = "Live television",
                    icon = Icons.Default.LiveTv,
                    onClick = onWatchLive
                )
            }
        }


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        /* MOVIES */

        SectionTitle(
            title = "Movies"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(
                listOf(
                    "Movies",
                    "Recently Added",
                    "Popular Movies",
                    "Continue Watching"
                )
            ) { title ->

                FreeflexMediaCard(
                    title = title,
                    subtitle = "Movies",
                    icon = Icons.Default.Movie,
                    onClick = onOpenMovies
                )
            }
        }


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        /* QUICK ACTIONS */

        SectionTitle(
            title = "Quick Actions"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            QuickAction(
                title = "TV GUIDE",
                icon = Icons.Default.Tv,
                onClick = onWatchLive
            )

            QuickAction(
                title = "PLAYLISTS",
                icon = Icons.Default.LiveTv,
                onClick = onOpenSettings
            )

            QuickAction(
                title = "FAVORITES",
                icon = Icons.Default.Favorite,
                onClick = onWatchLive
            )

            QuickAction(
                title = "SETTINGS",
                icon = Icons.Default.Settings,
                onClick = onOpenSettings
            )
        }


        if (!hasSources) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Add a TV provider to start watching.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/* ---------------------------------------------------------
   SIDEBAR
   --------------------------------------------------------- */

@Composable
private fun FreeflexSideBar(
    currentTab: FreeflexTab,
    onHome: () -> Unit,
    onLive: () -> Unit,
    onMovies: () -> Unit,
    onShows: () -> Unit,
    onRecordings: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onProfiles: () -> Unit,
    activeProfileName: String,
    liveEnabled: Boolean,
    moviesEnabled: Boolean,
    seriesEnabled: Boolean
) {

    Column(
        modifier = Modifier
            .width(92.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                vertical = 20.dp,
                horizontal = 10.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "FX",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )


        SideBarItem(
            icon = Icons.Default.Tv,
            label = "Home",
            selected = currentTab == FreeflexTab.HOME,
            onClick = onHome
        )

        if (liveEnabled) {

            SideBarItem(
                icon = Icons.Default.LiveTv,
                label = "Live",
                selected = currentTab == FreeflexTab.LIVE,
                onClick = onLive
            )
        }

        if (moviesEnabled) {

            SideBarItem(
                icon = Icons.Default.Movie,
                label = "Movies",
                selected = currentTab == FreeflexTab.MOVIES,
                onClick = onMovies
            )
        }

        if (seriesEnabled) {

            SideBarItem(
                icon = Icons.Default.Tv,
                label = "Shows",
                selected = currentTab == FreeflexTab.SHOWS,
                onClick = onShows
            )
        }

        SideBarItem(
            icon = Icons.Default.PlayArrow,
            label = "Recordings",
            selected = currentTab == FreeflexTab.RECORDINGS,
            onClick = onRecordings
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        SideBarItem(
            icon = Icons.Default.Search,
            label = "Search",
            selected = false,
            onClick = onSearch
        )

        SideBarItem(
            icon = Icons.Default.Person,
            label = activeProfileName,
            selected = false,
            onClick = onProfiles
        )

        SideBarItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            selected = false,
            onClick = onSettings
        )
    }
}


/* ---------------------------------------------------------
   SECTION TITLE
   --------------------------------------------------------- */

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}


/* ---------------------------------------------------------
   MEDIA CARD
   --------------------------------------------------------- */

@Composable
private fun FreeflexMediaCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background = if (focused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (focused) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .width(190.dp)
            .height(120.dp)
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .clickable {
                onClick()
            }
            .focusable()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(30.dp)
        )

        Column {

            Text(
                text = title,
                color = textColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                color = textColor.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


/* ---------------------------------------------------------
   QUICK ACTION
   --------------------------------------------------------- */

@Composable
private fun QuickAction(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background = if (focused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (focused) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .clickable {
                onClick()
            }
            .focusable()
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = title,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}


/* ---------------------------------------------------------
   SIDEBAR ITEM
   --------------------------------------------------------- */

@Composable
private fun SideBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background = when {
        focused -> MaterialTheme.colorScheme.primary
        selected -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

    val contentColor = when {
        focused -> MaterialTheme.colorScheme.onPrimary
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .clickable {
                onClick()
            }
            .focusable()
            .padding(
                vertical = 10.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}