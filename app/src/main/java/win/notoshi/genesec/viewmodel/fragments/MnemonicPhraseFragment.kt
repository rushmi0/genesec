package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thekhaeng.pushdownanim.PushDownAnim
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

        to12WordPage()
        to15WordPage()
        to18WordPage()
        to24WordPage()
        return binding.root
    }

    private fun to24WordPage() {
        setupPushDownAnim(binding.Word24)
        binding.Word24.setOnClickListener {

        }
    }

    private fun to18WordPage() {
        setupPushDownAnim(binding.Word18)
        binding.Word24.setOnClickListener {

        }
    }

    private fun to15WordPage() {
        setupPushDownAnim(binding.Word15)
        binding.Word24.setOnClickListener {

        }
    }

    private fun to12WordPage() {
        setupPushDownAnim(binding.Word12)
        binding.Word24.setOnClickListener {

        }
    }


    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
    }

}