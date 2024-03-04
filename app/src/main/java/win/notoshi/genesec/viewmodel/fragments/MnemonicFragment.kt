package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentMnemonicBinding

class MnemonicFragment : Fragment(R.layout.fragment_mnemonic) {

    private lateinit var binding: FragmentMnemonicBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMnemonicBinding.inflate(layoutInflater)
        toHomePage()
        return binding.root
    }

    private fun toHomePage() {
        binding.goHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}