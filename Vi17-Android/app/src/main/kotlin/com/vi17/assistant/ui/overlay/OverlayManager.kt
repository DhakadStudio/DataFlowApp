package com.vi17.assistant.ui.overlay

import android.content.Context
import android.view.WindowManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages all overlays: edge light and screen overlay.
 *\n * Coordinates showing/hiding overlays based on Vi state.
 */
@Singleton\nclass OverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val _edgeLightState = MutableStateFlow<EdgeLightState>(EdgeLightState.Idle)
    val edgeLightState: StateFlow<EdgeLightState> = _edgeLightState.asStateFlow()

    private val _screenOverlayVisible = MutableStateFlow(false)
    val screenOverlayVisible: StateFlow<Boolean> = _screenOverlayVisible.asStateFlow()

    private val _edgeLightConfig = mutableStateOf(EdgeLightConfig())
    val edgeLightConfig: State<EdgeLightConfig> = _edgeLightConfig

    private val _screenOverlayConfig = mutableStateOf(ScreenOverlayConfig())
    val screenOverlayConfig: State<ScreenOverlayConfig> = _screenOverlayConfig

    // Track overlay views
    private var edgeLightOverlay: EdgeLightOverlay? = null
    private var screenOverlay: ScreenOverlay? = null

    /**
     * Initialize overlays.
     */
    fun initialize() {
        try {
            Timber.d("Initializing OverlayManager")
            // Overlays will be created on demand when state changes
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize OverlayManager")
        }
    }

    /**
     * Update edge light state.
     */
    fun updateEdgeLightState(state: EdgeLightState) {
        Timber.d("Edge light state: $state")
        _edgeLightState.value = state

        when (state) {
            EdgeLightState.Idle -> hideEdgeLight()\n            EdgeLightState.Listening -> showEdgeLight()\n            EdgeLightState.Thinking -> showEdgeLight()\n            is EdgeLightState.Speaking -> showEdgeLight()\n            EdgeLightState.Error -> showEdgeLight()\n            EdgeLightState.SosActive -> showEdgeLight()\n        }
    }

    /**\n     * Show edge light overlay.\n     */\n    private fun showEdgeLight() {\n        try {\n            if (edgeLightOverlay == null) {\n                edgeLightOverlay = EdgeLightOverlay(\n                    context = context,\n                    windowManager = windowManager,\n                    stateFlow = _edgeLightState,\n                    config = _edgeLightConfig.value\n                )\n                Timber.d("Edge light overlay created")\n            }\n            edgeLightOverlay?.show()\n        } catch (e: Exception) {\n            Timber.e(e, "Failed to show edge light")\n        }\n    }\n\n    /**\n     * Hide edge light overlay.\n     */\n    private fun hideEdgeLight() {\n        try {\n            edgeLightOverlay?.hide()\n        } catch (e: Exception) {\n            Timber.e(e, "Failed to hide edge light")\n        }\n    }\n\n    /**\n     * Show screen overlay (when reading screen).\n     */\n    fun showScreenOverlay() {\n        try {\n            if (screenOverlay == null) {\n                screenOverlay = ScreenOverlay(\n                    context = context,\n                    windowManager = windowManager,\n                    config = _screenOverlayConfig.value\n                )\n                Timber.d("Screen overlay created")\n            }\n            screenOverlay?.show()\n            _screenOverlayVisible.value = true\n        } catch (e: Exception) {\n            Timber.e(e, "Failed to show screen overlay")\n        }\n    }\n\n    /**\n     * Hide screen overlay.\n     */\n    fun hideScreenOverlay() {\n        try {\n            screenOverlay?.hide()\n            _screenOverlayVisible.value = false\n        } catch (e: Exception) {\n            Timber.e(e, "Failed to hide screen overlay")\n        }\n    }\n\n    /**\n     * Update edge light configuration.\n     */\n    fun updateEdgeLightConfig(config: EdgeLightConfig) {\n        _edgeLightConfig.value = config\n        edgeLightOverlay?.updateConfig(config)\n    }\n\n    /**\n     * Update screen overlay configuration.\n     */\n    fun updateScreenOverlayConfig(config: ScreenOverlayConfig) {\n        _screenOverlayConfig.value = config\n        screenOverlay?.updateConfig(config)\n    }\n\n    /**\n     * Cleanup overlays.\n     */\n    fun cleanup() {\n        try {\n            edgeLightOverlay?.destroy()\n            screenOverlay?.destroy()\n            edgeLightOverlay = null\n            screenOverlay = null\n            Timber.d("OverlayManager cleaned up")\n        } catch (e: Exception) {\n            Timber.e(e, "Failed to cleanup OverlayManager")\n        }\n    }\n\n    /**\n     * Check if edge light is visible.\n     */\n    fun isEdgeLightVisible(): Boolean = edgeLightOverlay?.isVisible() ?: false\n\n    /**\n     * Check if screen overlay is visible.\n     */\n    fun isScreenOverlayVisible(): Boolean = screenOverlay?.isVisible() ?: false\n}\n\n/**\n * Placeholder for EdgeLightOverlay implementation.\n */\nclass EdgeLightOverlay(\n    private val context: Context,\n    private val windowManager: WindowManager,\n    private val stateFlow: StateFlow<EdgeLightState>,\n    private val config: EdgeLightConfig\n) {\n    private var isVisible = false\n\n    fun show() {\n        isVisible = true\n        Timber.d("EdgeLightOverlay shown")\n    }\n\n    fun hide() {\n        isVisible = false\n        Timber.d("EdgeLightOverlay hidden")\n    }\n\n    fun updateConfig(newConfig: EdgeLightConfig) {\n        Timber.d("EdgeLightOverlay config updated")\n    }\n\n    fun isVisible(): Boolean = isVisible\n\n    fun destroy() {\n        hide()\n        Timber.d("EdgeLightOverlay destroyed")\n    }\n}\n\n/**\n * Placeholder for ScreenOverlay implementation.\n */\nclass ScreenOverlay(\n    private val context: Context,\n    private val windowManager: WindowManager,\n    private val config: ScreenOverlayConfig\n) {\n    private var isVisible = false\n\n    fun show() {\n        isVisible = true\n        Timber.d("ScreenOverlay shown")\n    }\n\n    fun hide() {\n        isVisible = false\n        Timber.d("ScreenOverlay hidden")\n    }\n\n    fun updateConfig(newConfig: ScreenOverlayConfig) {\n        Timber.d("ScreenOverlay config updated")\n    }\n\n    fun isVisible(): Boolean = isVisible\n\n    fun destroy() {\n        hide()\n        Timber.d("ScreenOverlay destroyed")\n    }\n}\n
