package fr.cnam.campus

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cnam.campus.ui.theme.CampusTheme

private const val TAG = "Campus"

data class Event(
    val id: Int,
    val title: String,
    val category: String,
    val location: String?,
    val capacity: Int,
    val description: String
)

val staticEvents = listOf(
    Event(1, "Initiation à Kotlin", "Conférence", "31.1.71", 30, "Lorem ipsum."),
    Event(2, "Apprendre SQL", "Cours du soir", "Grande salle", 60, "Lorem ipsum."),
    Event(3, "Visite du CNAM", "Campus", null, 120, "Lorem ipsum."),
    Event(4, "Week-end d'intégration", "Vie étudiante", "Cour St-Martin", 0, "Lorem ipsum."),
    Event(5, "L'histoire des LLM", "Conférence", "Amphithéâtre principal", 150, "Lorem ipsum.")
)

// MainActivity()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusTheme {
                EventListScreen(
                    events = staticEvents,
                    onEventClick = { Log.d(TAG, "Ouvrir ${it.id}") }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = events.map { it.category }.distinct()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryRow(
                    categories = categories,
                    onCategoryClick = { Log.d(TAG, "Catégorie $it") },
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            items(events, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    onBook = { Log.d(TAG, "Réservation ${event.id}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun CategoryRow(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it }) { category ->
            FilterChip(
                selected = false,
                onClick = { onCategoryClick(category) },
                label = { Text(category) }
            )
        }
    }
}


@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = event.location ?: stringResource(R.string.location_unknown),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeatsText(capacity = event.capacity, modifier = Modifier.weight(1f))
                Button(onClick = onBook, enabled = event.capacity > 0) {
                    Text(stringResource(R.string.book))
                }
            }
        }
    }
}

@Composable
fun SeatsText(capacity: Int, modifier: Modifier = Modifier) {
    val isFull = capacity == 0
    Text(
        text = if (isFull) stringResource(R.string.event_full)
        else pluralStringResource(R.plurals.available_seats, capacity, capacity),
        style = MaterialTheme.typography.bodySmall,
        color = if (isFull) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onBook: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Button(
                    onClick = onBook,
                    enabled = event.capacity > 0,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.book))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.event_banner),
                contentDescription = stringResource(R.string.event_banner_description, event.title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = event.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = event.location ?: stringResource(R.string.location_unknown),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SeatsText(capacity = event.capacity)
                Text(
                    text = stringResource(R.string.description_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// Previews

@Preview(showBackground = true, name = "Liste - clair")
@Preview(showBackground = true, name = "Liste - sombre", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EventListScreenPreview() {
    CampusTheme {
        EventListScreen(events = staticEvents, onEventClick = {})
    }
}

@Preview(showBackground = true, name = "Détail - clair")
@Preview(showBackground = true, name = "Détail - sombre", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EventDetailScreenPreview() {
    CampusTheme {
        EventDetailScreen(event = staticEvents[4], onBook = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "Détail complet - sombre", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EventDetailFullPreview() {
    CampusTheme {
        EventDetailScreen(event = staticEvents[3], onBook = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "Liste - police 200 %", fontScale = 2f, heightDp = 2000)
@Composable
fun EventListLargeFontPreview() {
    CampusTheme {
        EventListScreen(events = staticEvents, onEventClick = {})
    }
}

@Preview(showBackground = true, name = "Détail - police 200 %", fontScale = 2f, heightDp = 1200)
@Composable
fun EventDetailLargeFontPreview() {
    CampusTheme {
        EventDetailScreen(event = staticEvents[4], onBook = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "Event complet")
@Composable
fun EventCardFullPreview() {
    CampusTheme {
        EventCard(event = staticEvents[3], onClick = {}, onBook = {})
    }
}

@Preview(showBackground = true, name = "Event sans location")
@Composable
fun EventCardNoLocationPreview() {
    CampusTheme {
        EventCard(event = staticEvents[2], onClick = {}, onBook = {})
    }
}