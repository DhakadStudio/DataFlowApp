package com.vi17.assistant.ui.overlay

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * Sealed class representing the state of the edge light overlay.
 *
 * States:
 * - IDLE: No UI shown
 * - LISTENING: Soft pulse (indigo with electric blue shimmer)
 * - THINKING: Faster pulse (violet)
 * - SPEAKING: Breathing animation (cyan-white)
 * - ERROR: Red flash
 * - SOS_ACTIVE: Rapid red pulse
 */
@Stable
sealed class EdgeLightState {
    object Idle : EdgeLightState()
    object Listening : EdgeLightState()
    object Thinking : EdgeLightState()
    data class Speaking(val intensity: Float = 1f) : EdgeLightState()
    object Error : EdgeLightState()
    object SosActive : EdgeLightState()
}

/**
 * Design tokens for edge light UI.
 */
object EdgeLightTheme {
    // Colors
    val colorBackground = Color(0xFF0A0A0F)
    val colorSurface = Color(0xFF111118)
    val colorPrimary = Color(0xFF4A90E2)      // Electric blue
    val colorSecondary = Color(0xFF7B2FBE)    // Violet
    val colorAccent = Color(0xFFA8EDEA)       // Cyan
    val colorTextPrimary = Color(0xFFF0F0FF)
    val colorTextSecondary = Color(0xFF8888AA)

    // Edge light specific colors
    val colorListeningBase = Color(0xFF1A0A3D)  // Deep indigo
    val colorListeningShimmer = Color(0xFF4A90E2)  // Electric blue
    val colorThinking = Color(0xFF7B2FBE)  // Violet
    val colorSpeaking = Color(0xFFA8EDEA)  // Cyan-white
    val colorError = Color(0xFFFF3B3B)  // Red
    val colorSos = Color(0xFFFF3B3B)  // Red

    // Dimensions
    const val edgeWidthDp = 4f
    const val cornerRadiusDp = 20f
    const val pillHeightDp = 36f

    // Animation timings (ms)
    const val pulseAnimationDuration = 1500
    const val thinkingAnimationDuration = 800
    const val speakingAnimationDuration = 2000
    const val errorFlashDuration = 300
    const val sosAnimationDuration = 500
}

/**
 * Configuration for edge light overlay.
 */
data class EdgeLightConfig(
    val isEnabled: Boolean = true,
    val edgeWidthDp: Float = EdgeLightTheme.edgeWidthDp,
    val animationDuration: Int = EdgeLightTheme.pulseAnimationDuration,
    val useGradient: Boolean = true,
    val showScreenOverlay: Boolean = true
)

/**
 * Screen overlay configuration (when reading screen).
 */
data class ScreenOverlayConfig(
    val isEnabled: Boolean = true,
    val backgroundColor: Color = Color.White.copy(alpha = 0.06f),
    val pillBackgroundColor: Color = Color.Black.copy(alpha = 0.7f),
    val pillTextColor: Color = Color.White,
    val isDraggable: Boolean = true
)

/**
 * Result of edge light animation frame.
 */
data class EdgeLightFrame(
    val state: EdgeLightState,
    val opacity: Float,
    val scale: Float,
    val colorBase: Color,
    val colorShimmer: Color,
    val shimmerPosition: Float
)
