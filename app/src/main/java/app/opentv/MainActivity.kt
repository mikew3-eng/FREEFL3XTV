/*
 * FREEFL3X TV
 * Custom home screen redesign based on OpenTV.
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

/* -------------------------------------------------------------------------
   MAIN SCREEN
   ------------------------------------------------------------------------- */

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
            .background(FreeflexBackground)
    ) {

        /* LEFT NAVIGATION */

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

        /* MAIN CONTENT */

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 48.dp,
                    end = 48.dp,
                    top = 36.dp,
                    bottom = 36.dp
                )
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
                        onOpenProfiles = onOpenProfiles,
                        onAddSource = onAddSource
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

/* -------------------------------------------------------------------------
   COLORS
   ------------------------------------------------------------------------- */

private val FreeflexBackground = Color(0xFF0D0D0D)
private val FreeflexPanel = Color(0xFF151515)
private val FreeflexCard = Color(0xFF1C1C1C)
private val FreeflexCardLight = Color(0xFF242424)
private val FreeflexAccent = Color(0xFFE50914)
private val FreeflexAccentDark = Color(0xFFB20710)
private val FreeflexText = Color(0xFFFFFFFF)
private val FreeflexMuted = Color(0xFFB3B3B3)

/* -------------------------------------------------------------------------
   HOME SCREEN
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexHomeScreen(
    activeProfileName: String,
    hasSources: Boolean,
    onWatchLive: () -> Unit,
    onOpenMovies: () -> Unit,
    onOpenShows: () -> Unit,
    onOpenRecordings: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProfiles: () -> Unit,
    onAddSource: () -> Unit
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FreeflexBackground)
            .verticalScroll(scrollState)
    ) {

        /* -------------------------------------------------------------
           TOP HEADER
           ------------------------------------------------------------- */

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "FREE FLEX TV",
                    color = FreeflexText,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Your entertainment. Your way.",
                    color = FreeflexMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            FreeflexHeaderButton(
                icon = Icons.Default.Search,
                text = "Search",
                onClick = onOpenSearch
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            FreeflexHeaderButton(
                icon = Icons.Default.Person,
                text = activeProfileName,
                onClick = onOpenProfiles
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        /* -------------------------------------------------------------
           NO PLAYLIST STATUS
           ------------------------------------------------------------- */

        if (!hasSources) {

            FreeflexStatusBanner(
                onAddSource = onAddSource
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )
        }

        /* -------------------------------------------------------------
           HERO
           ------------------------------------------------------------- */

        FreeflexHero(
            onWatchLive = onWatchLive
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        /* -------------------------------------------------------------
           CONTINUE WATCHING
           ------------------------------------------------------------- */

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
                    "Resume Movie",
                    "Continue Episode",
                    "Watch Again",
                    "Your Library"
                )
            ) { title ->

                FreeflexMediaCard(
                    title = title,
                    subtitle = "Pick up where you left off",
                    icon = Icons.Default.PlayArrow,
                    onClick = onOpenMovies
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        /* -------------------------------------------------------------
           LIVE TV
           ------------------------------------------------------------- */

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
                    "All Channels",
                    "News",
                    "Sports",
                    "Entertainment",
                    "Kids",
                    "Music"
                )
            ) { category ->

                FreeflexCategoryCard(
                    title = category,
                    icon = Icons.Default.LiveTv,
                    onClick = onWatchLive
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        /* -------------------------------------------------------------
           MOVIES
           ------------------------------------------------------------- */

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
                    subtitle = "Movies & VOD",
                    icon = Icons.Default.Movie,
                    onClick = onOpenMovies
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        /* -------------------------------------------------------------
           SERIES
           ------------------------------------------------------------- */

        SectionTitle(
            title = "Series"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(
                listOf(
                    "Series",
                    "Recently Added",
                    "Popular Shows",
                    "Continue Watching"
                )
            ) { title ->

                FreeflexMediaCard(
                    title = title,
                    subtitle = "TV shows & episodes",
                    icon = Icons.Default.Tv,
                    onClick = onOpenShows
                )
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        /* -------------------------------------------------------------
           QUICK ACTIONS
           ------------------------------------------------------------- */

        SectionTitle(
            title = "Quick Actions"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            FreeflexQuickAction(
                title = "ADD PLAYLIST",
                subtitle = "Add your M3U or Xtream Codes",
                icon = Icons.Default.LiveTv,
                modifier = Modifier.weight(1f),
                onClick = onAddSource
            )

            FreeflexQuickAction(
                title = "PLAYLISTS",
                subtitle = "Manage your playlists",
                icon = Icons.Default.Tv,
                modifier = Modifier.weight(1f),
                onClick = onOpenSettings
            )

            FreeflexQuickAction(
                title = "EPG",
                subtitle = "TV Guide & Schedule",
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f),
                onClick = onOpenSettings
            )

            FreeflexQuickAction(
                title = "SETTINGS",
                subtitle = "App preferences",
                icon = Icons.Default.Settings,
                modifier = Modifier.weight(1f),
                onClick = onOpenSettings
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )
    }
}

/* -------------------------------------------------------------------------
   HERO
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexHero(
    onWatchLive: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = FreeflexPanel
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 34.dp,
                    vertical = 32.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "WELCOME TO FREE FLEX TV",
                    color = FreeflexText,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Your ultimate entertainment. Watch live TV, movies, and shows.",
                    color = FreeflexMuted,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = onWatchLive,
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FreeflexAccent,
                        contentColor = Color.White
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch Live"
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "WATCH LIVE TV",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------
   STATUS BANNER
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexStatusBanner(
    onAddSource: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1A1A1A)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "No playlist added",
                    color = FreeflexText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Add your playlist to get started.",
                    color = FreeflexMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onAddSource,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FreeflexAccent
                )
            ) {
                Text(
                    text = "ADD PLAYLIST",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------
   HEADER BUTTON
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexHeaderButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background =
        if (focused) FreeflexAccent
        else FreeflexCard

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .focusable()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* -------------------------------------------------------------------------
   SECTION TITLE
   ------------------------------------------------------------------------- */

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        color = FreeflexText,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

/* -------------------------------------------------------------------------
   MEDIA CARD
   ------------------------------------------------------------------------- */

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

    val background =
        if (focused) FreeflexAccent
        else FreeflexCard

    val contentColor = Color.White

    Column(
        modifier = Modifier
            .width(210.dp)
            .height(128.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .focusable()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(30.dp)
        )

        Column {

            Text(
                text = title,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.70f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* -------------------------------------------------------------------------
   CATEGORY CARD
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexCategoryCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background =
        if (focused) FreeflexAccent
        else FreeflexCard

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .focusable()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/* -------------------------------------------------------------------------
   QUICK ACTION
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexQuickAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background =
        if (focused) FreeflexAccent
        else FreeflexCard

    Column(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .focusable()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(25.dp)
        )

        Column {

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.70f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* -------------------------------------------------------------------------
   SIDEBAR
   ------------------------------------------------------------------------- */

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
            .width(150.dp)
            .fillMaxHeight()
            .background(FreeflexPanel)
            .padding(
                vertical = 18.dp,
                horizontal = 9.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /* LOGO */

        Text(
            text = "FREEFL3X",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = FreeflexAccent
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        /* HOME */

        FreeflexSideBarItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = currentTab == FreeflexTab.HOME,
            onClick = onHome
        )

        /* LIVE */

        if (liveEnabled) {

            FreeflexSideBarItem(
                icon = Icons.Default.LiveTv,
                label = "Live TV",
                selected = currentTab == FreeflexTab.LIVE,
                onClick = onLive
            )
        }

        /* MOVIES */

        if (moviesEnabled) {

            FreeflexSideBarItem(
                icon = Icons.Default.Movie,
                label = "Movies",
                selected = currentTab == FreeflexTab.MOVIES,
                onClick = onMovies
            )
        }

        /* SERIES */

        if (seriesEnabled) {

            FreeflexSideBarItem(
                icon = Icons.Default.Tv,
                label = "Series",
                selected = currentTab == FreeflexTab.SHOWS,
                onClick = onShows
            )
        }

        /* FAVORITES - Added to match your image */

        FreeflexSideBarItem(
            icon = Icons.Default.Favorite,
            label = "Favorites",
            selected = false,
            onClick = onOpenMovies
        )

        /* SEARCH */

        FreeflexSideBarItem(
            icon = Icons.Default.Search,
            label = "Search",
            selected = false,
            onClick = onSearch
        )

        /* PROFILE */

        FreeflexSideBarItem(
            icon = Icons.Default.Person,
            label = activeProfileName,
            selected = false,
            onClick = onProfiles
        )

        /* SETTINGS */

        FreeflexSideBarItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            selected = false,
            onClick = onSettings
        )
    }
}

/* -------------------------------------------------------------------------
   SIDEBAR ITEM
   ------------------------------------------------------------------------- */

@Composable
private fun FreeflexSideBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background = when {
        focused -> FreeflexAccent
        selected -> FreeflexCardLight
        else -> Color.Transparent
    }

    val contentColor = when {
        focused -> Color.White
        selected -> FreeflexAccent
        else -> FreeflexMuted
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .onFocusChanged {
                focused = it.isFocused
            }
            .focusable()
            .clickable {
                onClick()
            }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}