/*
 * FREEFL3X TV
 * Main interface redesign based on OpenTV.
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
import androidx.compose.material3.OutlinedButton
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

private val FreeflexBlack = Color(0xFF0D0D0D)
private val FreeflexCard = Color(0xFF1A1A1A)
private val FreeflexCardAlt = Color(0xFF222222)
private val FreeflexWhite = Color(0xFFFFFFFF)
private val FreeflexGray = Color(0xFFB3B3B3)
private val FreeflexRed = Color(0xFFE50914)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FreeflexBlack)
    ) {

        /*
         * TOP NAVIGATION
         */

        FreeflexTopBar(
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
            onFavorites = {
                onOpenSearch()
            },
            onSearch = onOpenSearch,
            onSettings = onOpenSettings,
            liveEnabled = liveEnabled,
            moviesEnabled = moviesEnabled,
            seriesEnabled = seriesEnabled
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            /*
             * LEFT ICON SIDEBAR
             */

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
                            onAddPlaylist = onAddSource,
                            onOpenMovies = {
                                currentTab = FreeflexTab.MOVIES
                            },
                            onOpenShows = {
                                currentTab = FreeflexTab.SHOWS
                            },
                            onOpenRecordings = {
                                currentTab = FreeflexTab.RECORDINGS
                            },
                            onOpenPlaylists = onOpenSettings,
                            onOpenEpg = onOpenSettings
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
}


/*
 * ---------------------------------------------------------
 * TOP BAR
 * ---------------------------------------------------------
 */

@Composable
private fun FreeflexTopBar(
    currentTab: FreeflexTab,
    onHome: () -> Unit,
    onLive: () -> Unit,
    onMovies: () -> Unit,
    onShows: () -> Unit,
    onFavorites: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    liveEnabled: Boolean,
    moviesEnabled: Boolean,
    seriesEnabled: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(FreeflexBlack)
            .padding(
                horizontal = 28.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "FREEFL3X TV",
            color = FreeflexWhite,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.width(38.dp)
        )

        FreeflexTopItem(
            title = "Home",
            selected = currentTab == FreeflexTab.HOME,
            onClick = onHome
        )

        if (liveEnabled) {
            FreeflexTopItem(
                title = "Live TV",
                selected = currentTab == FreeflexTab.LIVE,
                onClick = onLive
            )
        }

        if (moviesEnabled) {
            FreeflexTopItem(
                title = "Movies",
                selected = currentTab == FreeflexTab.MOVIES,
                onClick = onMovies
            )
        }

        if (seriesEnabled) {
            FreeflexTopItem(
                title = "Series",
                selected = currentTab == FreeflexTab.SHOWS,
                onClick = onShows
            )
        }

        FreeflexTopItem(
            title = "Favorites",
            selected = false,
            onClick = onFavorites
        )

        FreeflexTopItem(
            title = "Search",
            selected = false,
            onClick = onSearch
        )

        FreeflexTopItem(
            title = "Settings",
            selected = false,
            onClick = onSettings
        )
    }
}


/*
 * ---------------------------------------------------------
 * TOP NAV ITEM
 * ---------------------------------------------------------
 */

@Composable
private fun FreeflexTopItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val textColor = when {
        focused -> FreeflexWhite
        selected -> FreeflexRed
        else -> FreeflexGray
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 7.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
            .onFocusChanged {
                focused = it.isFocused
            }
            .clickable {
                onClick()
            }
            .focusable()
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color = textColor,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )

        if (selected) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .background(FreeflexRed)
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * FREEFL3X HOME
 * ---------------------------------------------------------
 */

@Composable
private fun FreeflexHomeScreen(
    activeProfileName: String,
    hasSources: Boolean,
    onWatchLive: () -> Unit,
    onAddPlaylist: () -> Unit,
    onOpenMovies: () -> Unit,
    onOpenShows: () -> Unit,
    onOpenRecordings: () -> Unit,
    onOpenPlaylists: () -> Unit,
    onOpenEpg: () -> Unit
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                start = 42.dp,
                end = 42.dp,
                top = 28.dp,
                bottom = 40.dp
            )
    ) {

        /*
         * STATUS BANNER
         */

        if (!hasSources) {

            PlaylistWarning(
                onAddPlaylist = onAddPlaylist
            )

            Spacer(
                modifier = Modifier.height(26.dp)
            )
        }


        /*
         * HERO
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(FreeflexCard)
                .padding(
                    horizontal = 34.dp,
                    vertical = 32.dp
                )
        ) {

            Text(
                text = "WELCOME TO FREEFL3X TV",
                color = FreeflexWhite,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Your ultimate entertainment. Watch live TV, movies, and shows.",
                color = FreeflexGray,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onWatchLive,
                modifier = Modifier
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FreeflexRed
                )
            ) {

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Watch Live TV"
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "WATCH LIVE TV",
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        /*
         * CONTINUE WATCHING
         */

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
                    "Recently Watched",
                    "Movies",
                    "Series",
                    "Live TV"
                )
            ) { title ->

                FreeflexMediaCard(
                    title = title,
                    subtitle = "Continue watching",
                    icon = Icons.Default.PlayArrow,
                    onClick = when (title) {
                        "Movies" -> onOpenMovies
                        "Series" -> onOpenShows
                        "Live TV" -> onWatchLive
                        else -> onOpenMovies
                    }
                )
            }
        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        /*
         * LIVE TV
         */

        SectionTitle(
            title = "Live TV"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                CategoryPill(
                    title = category,
                    onClick = onWatchLive
                )
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
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
                    onClick = when (title) {
                        "TV Guide" -> onOpenEpg
                        else -> onWatchLive
                    }
                )
            }
        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        /*
         * QUICK ACTIONS
         */

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

            QuickActionCard(
                title = "Add Playlist",
                description = "Add your M3U or Xtream Codes",
                icon = Icons.Default.LiveTv,
                onClick = onAddPlaylist,
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                title = "Playlists",
                description = "Manage your playlists",
                icon = Icons.Default.Tv,
                onClick = onOpenPlaylists,
                modifier = Modifier.weight(1f)
            )

            QuickActionCard(
                title = "EPG",
                description = "TV Guide & Schedule",
                icon = Icons.Default.LiveTv,
                onClick = onOpenEpg,
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        /*
         * PROFILE
         */

        Text(
            text = "Welcome back, $activeProfileName",
            color = FreeflexGray,
            style = MaterialTheme.typography.bodyMedium
        )

        if (!hasSources) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Add your playlist to start watching.",
                color = FreeflexGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * PLAYLIST WARNING
 * ---------------------------------------------------------
 */

@Composable
private fun PlaylistWarning(
    onAddPlaylist: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(FreeflexCard)
            .padding(
                horizontal = 22.dp,
                vertical = 18.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "No playlist added",
                color = FreeflexWhite,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Add your playlist to get started.",
                color = FreeflexGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onAddPlaylist,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FreeflexRed
            )
        ) {

            Text(
                text = "ADD PLAYLIST",
                fontWeight = FontWeight.Bold
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * CATEGORY PILL
 * ---------------------------------------------------------
 */

@Composable
private fun CategoryPill(
    title: String,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background = if (focused) {
        FreeflexRed
    } else {
        FreeflexCard
    }

    val textColor = if (focused) {
        FreeflexWhite
    } else {
        FreeflexGray
    }

    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(50.dp)
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
                vertical = 10.dp
            )
    ) {

        Text(
            text = title,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}


/*
 * ---------------------------------------------------------
 * SIDEBAR
 * ---------------------------------------------------------
 */

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
            .width(82.dp)
            .fillMaxHeight()
            .background(FreeflexBlack)
            .padding(
                vertical = 18.dp,
                horizontal = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "FX",
            color = FreeflexRed,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        SideBarItem(
            icon = Icons.Default.Home,
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
                label = "Series",
                selected = currentTab == FreeflexTab.SHOWS,
                onClick = onShows
            )
        }

        SideBarItem(
            icon = Icons.Default.Favorite,
            label = "Fav",
            selected = false,
            onClick = onSearch
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
            label = "Profile",
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


/*
 * ---------------------------------------------------------
 * SECTION TITLE
 * ---------------------------------------------------------
 */

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        color = FreeflexWhite,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}


/*
 * ---------------------------------------------------------
 * MEDIA CARD
 * ---------------------------------------------------------
 */

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
        FreeflexRed
    } else {
        FreeflexCardAlt
    }

    val textColor = FreeflexWhite

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
                color = FreeflexGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * QUICK ACTION CARD
 * ---------------------------------------------------------
 */

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var focused by remember {
        mutableStateOf(false)
    }

    val background = if (focused) {
        FreeflexRed
    } else {
        FreeflexCard
    }

    Column(
        modifier = modifier
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
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = FreeflexWhite,
            modifier = Modifier.size(26.dp)
        )

        Column {

            Text(
                text = title,
                color = FreeflexWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = description,
                color = if (focused) {
                    FreeflexWhite
                } else {
                    FreeflexGray
                },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/*
 * ---------------------------------------------------------
 * SIDEBAR ITEM
 * ---------------------------------------------------------
 */

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
        focused -> FreeflexRed
        selected -> FreeflexCardAlt
        else -> Color.Transparent
    }

    val contentColor = when {
        focused -> FreeflexWhite
        selected -> FreeflexRed
        else -> FreeflexGray
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