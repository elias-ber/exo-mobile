package fr.cnam.campus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TopAppBar
import fr.cnam.campus.ui.theme.CampusTheme

private const val TITLE = "Campus"

data class Event(
    val id: Int,
    val title: String,
    val category: String,
    val location: String?,
    val capacity: Int
)

val staticEvents = listOf(
    Event(1, "Initiation à Kotlin", "Conférence", "31.1.71", 30),
    Event(2, "Apprendre SQL", "Cours du soir", "Grande salle", 60),
    Event(3, "Visite du CNAM", "Campus", null, 120),
    Event(4, "Week-end d'intégration", "Vie étudiante", "Cour St-Martin", 0),
    Event(5, "L'histoire des LLM", "Conférence", "Amphithéâtre principal", 150)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusTheme {
                EventListScreen(events = staticEvents)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    events: List<Event>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Campus") }) }
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        LazyColumn(
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { Log.d(TITLE, "Ouvrir ${event.id}") },
                    onBook = { Log.d(TITLE, "Réservation ${event.id}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
    val isFull = event.capacity == 0

    Card(onClick = onClick, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = event.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = event.location ?: "Lieu à confirmer",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFull) "Complet" else "${event.capacity} places",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFull) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onBook, enabled = !isFull) {
                    Text("Réserver")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Event complet")
@Composable
fun EventCardFullPreview() {
    CampusTheme {
        EventCard(
            event = Event(4, "Titre Test", "Test", "Test", 0),
            onClick = {},
            onBook = {}
        )
    }
}

@Preview(showBackground = true, name = "Event sans location")
@Composable
fun EventCardNoLocationPreview() {
    CampusTheme {
        EventCard(
            event = Event(3, "Titre Test", "Test", null, 50),
            onClick = {},
            onBook = {}
        )
    }
}

@Preview(showBackground = true, name = "Liste d'Event")
@Composable
fun EventListScreenPreview() {
    CampusTheme {
        EventListScreen(events = staticEvents)
    }
}