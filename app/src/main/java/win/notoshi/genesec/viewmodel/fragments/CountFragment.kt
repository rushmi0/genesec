package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentCountBinding
import win.notoshi.genesec.test.CountModelTest
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class CountFragment : Fragment(R.layout.fragment_count) {

    private lateinit var binding: FragmentCountBinding
    private lateinit var viewModel: CountModelTest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[CountModelTest::class.java]

        binding = FragmentCountBinding.inflate(layoutInflater)

        setUIEvent()
        return binding.root
    }


    private fun observe() {
        lifecycleScope.launch {
            viewModel.count.collect { count ->
                binding.countBTN.text = "Count $count"
            }
        }
    }


    private fun setUIEvent() {
        binding.countBTN.setOnClickListener {
            viewModel.add()
        }

        observe()
    }

}