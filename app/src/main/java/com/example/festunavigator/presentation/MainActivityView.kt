package com.example.festunavigator.presentation

import android.opengl.GLSurfaceView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.festunavigator.R
import com.example.festunavigator.presentation.common.helpers.SnackbarHelper
import io.github.sceneview.ar.ArSceneView

/**
 * Wraps [R.layout.activity_main] and controls lifecycle operations for [GLSurfaceView].
 */
class MainActivityView(val activity: MainActivity, renderer: AppRenderer) : DefaultLifecycleObserver {
    val root = View.inflate(activity, R.layout.activity_main, null)
    val surfaceView = root.findViewById<ArSceneView>(R.id.sceneView).apply {
        planeRenderer.isVisible = true
        instructions.enabled = false

    }
    val place: Button = root.findViewById(R.id.place_button)
    val delete: Button = root.findViewById(R.id.delete_button)
    val link: Button = root.findViewById(R.id.link_button)
    val entry: Button = root.findViewById(R.id.entry_button)
    val init: Button = root.findViewById(R.id.init_button)
    val pathfind: Button = root.findViewById(R.id.pathfind_button)
    val rend: Button = root.findViewById(R.id.rend_button)
    val initText: TextView = root.findViewById(R.id.init_text)




    //val useCloudMlSwitch = root.findViewById<SwitchCompat>(R.id.useCloudMlSwitch)
    //val scanButton = root.findViewById<AppCompatButton>(R.id.scanButton)
    //val resetButton = root.findViewById<AppCompatButton>(R.id.clearButton)
    val snackbarHelper = SnackbarHelper().apply {
        setParentView(root.findViewById(R.id.con_layout))
        setMaxLines(6)
    }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause(owner)
    }

    fun post(action: Runnable) = root.post(action)

    /**
     * Toggles the scan button depending on if scanning is in progress.
     */
//    fun setScanningActive(active: Boolean) = when(active) {
//        true -> {
//            scanButton.isEnabled = false
//            scanButton.setText(activity.getString(R.string.scan_busy))
//        }
//        false -> {
//            scanButton.isEnabled = true
//            scanButton.setText(activity.getString(R.string.scan_available))
//        }
//    }
}