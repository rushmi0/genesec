package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentMnemonicPhraseBinding
import win.notoshi.genesec.model.MnemonicPhraseModel
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class MnemonicPhraseFragment : Fragment(R.layout.fragment_mnemonic_phrase) {


    private lateinit var binding: FragmentMnemonicPhraseBinding
    private lateinit var viewModel: MnemonicPhraseModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MnemonicPhraseModel::class.java]
        binding = FragmentMnemonicPhraseBinding.inflate(layoutInflater)
        return binding.root
    }



}