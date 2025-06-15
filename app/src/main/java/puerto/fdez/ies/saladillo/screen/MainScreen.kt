package puerto.fdez.ies.saladillo.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import puerto.fdez.ies.saladillo.R


enum class ExpandableSection {
    TicTacToe
}

@Composable
fun MainScreen(
    expandedButton: ExpandableSection?,
    onExpandChange: (ExpandableSection?) -> Unit,
    onTicTacToePvp: () -> Unit,
    onTicTacToeAi: () -> Unit,
    onBattleship: () -> Unit,
    onHangman: () -> Unit,
    onWordSearch: () -> Unit
) {
    NotebookScaffold(
        title = "",
        onBack = {},
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Some\npen and paper\ngames",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                )

                ExpandableButton(
                    title = stringResource(R.string.tic_tac_toe),
                    expanded = expandedButton == ExpandableSection.TicTacToe,
                    onExpandRequest = {
                        onExpandChange(
                            if (expandedButton == ExpandableSection.TicTacToe) null else ExpandableSection.TicTacToe
                        )
                    },
                    onPvpClick = onTicTacToePvp,
                    onAiClick = onTicTacToeAi
                )

                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(title = stringResource(R.string.battleship), onClick = onBattleship)
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(title = stringResource(R.string.hangman), onClick = onHangman)
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(title = stringResource(R.string.wordsearch), onClick = onWordSearch)
            }
        }
    }
}

@Composable
fun ExpandableButton(
    title: String,
    expanded: Boolean,
    onExpandRequest: () -> Unit,
    onPvpClick: () -> Unit,
    onAiClick: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Button(
            onClick = onExpandRequest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded)
                        stringResource(R.string.collapse_section, title)
                    else
                        stringResource(R.string.expand_section, title),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .rotate(rotationAngle)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    title = stringResource(R.string.pvp),
                    onClick = onPvpClick,
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    title = stringResource(R.string.vs_ai),
                    onClick = onAiClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookTopAppBar(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun NotebookScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val backgroundPainter = painterResource(id = R.drawable.notebookbackground)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Scaffold(
            topBar = {
                NotebookTopAppBar(title = title, onBack = onBack, actions = actions)
            },
            containerColor = Color.Transparent,
            content = content
        )
    }
}

