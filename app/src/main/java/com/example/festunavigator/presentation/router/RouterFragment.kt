package com.example.festunavigator.presentation.router

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.festunavigator.R
import com.example.festunavigator.data.App
import com.example.festunavigator.databinding.FragmentRouterBinding
import com.example.festunavigator.databinding.FragmentSearchBinding
import com.example.festunavigator.domain.hit_test.HitTestResult
import com.example.festunavigator.presentation.LabelObject
import com.example.festunavigator.presentation.preview.MainEvent
import com.example.festunavigator.presentation.preview.MainShareModel
import com.example.festunavigator.presentation.scanner.ScannerFragment
import com.example.festunavigator.presentation.search.SearchFragment
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.node.Node
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RouterFragment : Fragment() {

    private val destinationDesc = App.instance!!.getDestinationDesc
    private val hitTest = App.instance!!.hitTest

    private val mainModel: MainShareModel by activityViewModels()

    private var _binding: FragmentRouterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (App.mode == App.ADMIN_MODE) {
            binding.adminPanel.isVisible = true
        }
        else {
            binding.adminPanel.isGone = true
        }

        binding.deleteButton.setOnClickListener {
            removeSelectedNode()
        }

        binding.linkButton.setOnClickListener {
            changeLinkPlacementMode()
        }

        binding.placeButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                createNode()
            }
        }

        binding.entryButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(
                ScannerFragment.SCAN_TYPE,
                ScannerFragment.TYPE_ENTRY
            )
            findNavController().navigate(R.id.action_global_scannerFragment, args = bundle)
        }

        binding.fromInput.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.fromInput.isActivated = false
                binding.fromInput.clearFocus()
                search(SearchFragment.TYPE_START)
            }
        }

        binding.toInput.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.toInput.isActivated = false
                binding.toInput.clearFocus()
                search(SearchFragment.TYPE_END)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.pathState.collect { pathState ->
                    binding.fromInput.setText(pathState.startLabel?.label ?: "")
                    binding.toInput.setText(pathState.endLabel?.label ?: "")
                    if (pathState.path != null){
                        binding.destinationText.isVisible = true
                        binding.destinationText.text =
                            resources.getString(
                                R.string.going,
                                destinationDesc(pathState.endLabel!!.label, requireContext())
                            )
                    }
                    else {
                        binding.destinationText.isGone = true
                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.selectedNode.collect { treeNode ->
                    if (treeNode == null) {

                    }
                    else {

                    }
                    binding.linkButton.isEnabled = treeNode != null
                    binding.deleteButton.isEnabled = treeNode != null
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.linkPlacementMode.collect { link ->
                    binding.linkButton.text = if (link) getString(R.string.cancel) else getString(R.string.link)

                }
            }
        }
    }

    private fun search(type: Int){
        val action = RouterFragmentDirections.actionRouterFragmentToSearchFragment(type)
        findNavController().navigate(action)
    }

    private fun changeLinkPlacementMode(){
        mainModel.onEvent(MainEvent.ChangeLinkMode)
    }

    private fun removeSelectedNode(){
        mainModel.selectedNode.value?.let {
            mainModel.onEvent(MainEvent.DeleteNode(it))
        }
    }

    private fun createNode(
        number: String? = null,
        position: Float3? = null,
        orientation: Quaternion? = null,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainModel.frame.value?.let { frame ->
                val result = useHitTest(frame).getOrNull()
                if (result != null) {
                    mainModel.onEvent(
                        MainEvent.CreateNode(
                        number,
                        position,
                        orientation,
                        result
                    ))
                }
            }
        }
    }

    private fun useHitTest(
        frame: ArFrame,
        x: Float = frame.session.displayWidth / 2f,
        y: Float = frame.session.displayHeight / 2f,
    ): Result<HitTestResult> {

        return hitTest(frame, Float2(x, y))
    }

}