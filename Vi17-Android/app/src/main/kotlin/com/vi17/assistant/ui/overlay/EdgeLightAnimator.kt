package com.vi17.assistant.ui.overlay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Handles all edge light animations using Compose Animated APIs.
 *
 * Provides smooth, fluid animations for different edge light states:
 * - LISTENING: Soft pulse with shimmer
 * - THINKING: Faster pulse
 * - SPEAKING: Breathing animation
 * - ERROR: Flash
 * - SOS: Rapid pulse
 */
class EdgeLightAnimator {

    /**
     * Animates to LISTENING state (soft pulse).
     */
    suspend fun animateListening(
        opacity: Animatable<Float, *>,
        scale: Animatable<Float, *>,
        shimmerPosition: Animatable<Float, *>
    ) {
        coroutineScope {
            launch {
                opacity.animateTo(
                    targetValue = 0.8f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.pulseAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1.1f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.pulseAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                shimmerPosition.animateTo(
                    targetValue = 1f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.pulseAnimationDuration,
                            easing = EaseInOutCubic
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }
    }

    /**
     * Animates to THINKING state (faster pulse).
     */
    suspend fun animateThinking(
        opacity: Animatable<Float, *>,
        scale: Animatable<Float, *>,
        shimmerPosition: Animatable<Float, *>
    ) {
        coroutineScope {
            launch {
                opacity.animateTo(
                    targetValue = 1f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.thinkingAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.thinkingAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                shimmerPosition.animateTo(
                    targetValue = 1f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.thinkingAnimationDuration / 2,
                            easing = EaseInOutCubic
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }
    }

    /**
     * Animates to SPEAKING state (breathing animation).
     */
    suspend fun animateSpeaking(
        opacity: Animatable<Float, *>,
        scale: Animatable<Float, *>,
        intensity: Float
    ) {
        coroutineScope {
            launch {
                opacity.animateTo(
                    targetValue = 0.6f + (intensity * 0.4f),
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.speakingAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1f + (intensity * 0.2f),
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.speakingAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    /**
     * Animates ERROR state (red flash).
     */
    suspend fun animateError(
        opacity: Animatable<Float, *>,
        scale: Animatable<Float, *>
    ) {
        coroutineScope {
            launch {
                opacity.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = EdgeLightTheme.errorFlashDuration,
                        easing = EaseInOutCubic
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = EdgeLightTheme.errorFlashDuration,
                        easing = EaseInOutCubic
                    )
                )
            }
        }
    }

    /**
     * Animates SOS state (rapid red pulse).
     */
    suspend fun animateSos(
        opacity: Animatable<Float, *>,
        scale: Animatable<Float, *>
    ) {
        coroutineScope {
            launch {
                opacity.animateTo(
                    targetValue = 1f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.sosAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1.2f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = EdgeLightTheme.sosAnimationDuration,
                            easing = EaseInOutSine
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    /**
     * Animates to IDLE state (fade out).
     */
    suspend fun animateIdle(
        opacity: Animatable<Float, *>,
        scale: Animatable<Float, *>
    ) {
        coroutineScope {
            launch {
                opacity.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseInOutCubic
                    )
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseInOutCubic
                    )
                )
            }
        }
    }

    /**
     * Gets the appropriate color based on state.
     */
    fun getColorForState(state: EdgeLightState): Pair<Color, Color> {
        return when (state) {
            EdgeLightState.Idle -> EdgeLightTheme.colorBackground to EdgeLightTheme.colorBackground
            EdgeLightState.Listening -> EdgeLightTheme.colorListeningBase to EdgeLightTheme.colorListeningShimmer
            EdgeLightState.Thinking -> EdgeLightTheme.colorThinking to EdgeLightTheme.colorThinking
            is EdgeLightState.Speaking -> EdgeLightTheme.colorSpeaking to EdgeLightTheme.colorAccent
            EdgeLightState.Error -> EdgeLightTheme.colorError to EdgeLightTheme.colorError
            EdgeLightState.SosActive -> EdgeLightTheme.colorSos to EdgeLightTheme.colorSos
        }
    }

    companion object {
        private const val TAG = "EdgeLightAnimator"
    }
}

/**
 * Composable hook for managing edge light animations.
 */
@Composable
fun rememberEdgeLightAnimator(): EdgeLightAnimator {
    return remember { EdgeLightAnimator() }
}

/**
 * Composable hook for managing animation values.
 */
@Composable
fun rememberEdgeLightAnimationValues(): EdgeLightAnimationValues {
    val opacity = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val shimmerPosition = remember { Animatable(0f) }

    return EdgeLightAnimationValues(
        opacity = opacity,
        scale = scale,
        shimmerPosition = shimmerPosition
    )
}

/**
 * Container for animation values.
 */
data class EdgeLightAnimationValues(
    val opacity: Animatable<Float, *>,
    val scale: Animatable<Float, *>,
    val shimmerPosition: Animatable<Float, *>
)
