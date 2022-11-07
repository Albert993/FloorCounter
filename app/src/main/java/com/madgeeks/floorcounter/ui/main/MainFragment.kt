package com.madgeeks.floorcounter.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.ActivityTransition
import com.madgeeks.floorcounter.databinding.FragmentMainBinding
import com.madgeeks.floorcounter.service.ActivityTransitionUtil
import com.madgeeks.floorcounter.service.SensorsState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.floors.observe(viewLifecycleOwner) {
            binding.floorsValue.text = it.toString()
        }

        SensorsState.barometerReading.observe(viewLifecycleOwner) {
            binding.atmPressureValue.text = "$it hPa"
        }

        SensorsState.currentAltitude.observe(viewLifecycleOwner) {
            binding.altitudeValue.text = "$it m"
        }

        SensorsState.maxAltitudeChange.observe(viewLifecycleOwner) {
            binding.maxAltValue.text = "$it m"
        }

        SensorsState.currentActivityType.observe(viewLifecycleOwner) {
            if (it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) return@observe

            binding.activityValue.text = "${ActivityTransitionUtil.getActivityString(it.type)} enter at: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
    }
}