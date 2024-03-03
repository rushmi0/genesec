package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentHomeBinding
import win.notoshi.genesec.test.CountModelTest
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: CountModelTest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[CountModelTest::class.java]

        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }


}