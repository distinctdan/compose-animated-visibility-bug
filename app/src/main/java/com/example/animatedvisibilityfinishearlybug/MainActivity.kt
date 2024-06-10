package com.example.animatedvisibilityfinishearlybug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

private val _globalTimerFlow = MutableStateFlow(Instant.now())

/**
 * A global state flow that emits the current time once a second. This ensures that we use the
 * same timer everywhere in order to batch UI recompositions for better performance.
 */
val globalTimerFlow = _globalTimerFlow.asStateFlow()

@OptIn(DelicateCoroutinesApi::class)
private val globalTimer = GlobalScope.launch {
    while (true) {
        delay(1000)
        _globalTimerFlow.emit(Instant.now())
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var visible by remember { mutableStateOf(true) }
            val duration = 3000

            Column {
                Button(
                    onClick = {
                        visible = !visible
                    }
                ) {
                    Text(
                        text = if (visible) "Hide" else "Show",
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = expandVertically(tween(duration, 0, FastOutSlowInEasing)),
                        exit = shrinkVertically(tween(duration, 0, FastOutSlowInEasing))
                    ) {

                        Column(
                            modifier = Modifier
                                .background(Color.Blue)
                                .requiredHeight(300.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Basic text",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = expandVertically(tween(duration, 0, FastOutSlowInEasing)),
                        exit = shrinkVertically(tween(duration, 0, FastOutSlowInEasing))
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color.Blue)
                                .requiredHeight(300.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            FixedHeightTimerText()
                        }
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = expandVertically(tween(duration, 0, FastOutSlowInEasing)),
                        exit = shrinkVertically(tween(duration, 0, FastOutSlowInEasing))
                    ) {
                        VariableHeightTimerText(
                            modifier = Modifier
                                .background(Color.Red)
                                .padding(top = 300.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VariableHeightTimerText(
    modifier: Modifier = Modifier,
) {
    val now = globalTimerFlow.collectAsState()

    Text(
        text = "Variable Height: ${now.value}",
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            // Add padding based on the seconds so that our height changes.
            .padding(top = (now.value.epochSecond % 60).toInt().dp)
    )
}

@Composable
fun FixedHeightTimerText(
    modifier: Modifier = Modifier,
) {
    val now = globalTimerFlow.collectAsState()

    Text(
        text = "Fixed Height: ${now.value}",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}