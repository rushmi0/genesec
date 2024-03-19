package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.launch
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentMnemonicBinding
import win.notoshi.genesec.model.MnemonicModel
import win.notoshi.genesec.model.record.BIP39Record
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class MnemonicFragment : Fragment(R.layout.fragment_mnemonic) {

    private lateinit var binding: FragmentMnemonicBinding
    private lateinit var viewModel: MnemonicModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MnemonicModel::class.java]
        binding = FragmentMnemonicBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        retrieveValue()
        toBackPage()
        toHomePage()
        newSeed()
        observeNewSeed()
    }

    private fun retrieveValue() {
        val strength = requireArguments().getInt("strength")
        val wordLength = requireArguments().getInt("wordLength")
        viewModel.getValue(strength, wordLength)

        binding.amountWord.text = wordLength.toString()

        // แสดงค่าใน Log
        Log.d("Strength Value", "$strength bits")
        Log.d("Word Length Value", "$wordLength")
    }

    private fun toHomePage() {
        setupPushDownAnim(binding.confirmSeedtBTN)
        binding.confirmSeedtBTN.setOnClickListener {
            findNavController().navigate(MnemonicFragmentDirections.actionMnemonicFragmentToHomeFragment())
        }
    }


    private fun toBackPage() {
        setupPushDownAnim(binding.backBTN)
        binding.backBTN.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun newSeed() {
        setupPushDownAnim(binding.newSeedtBTN)
        binding.newSeedtBTN.setOnClickListener {
            viewModel.mnemonicPhrase().toString()
        }
    }

    private fun observeNewSeed() {
        lifecycleScope.launch {
            viewModel.SEED.collect { seed ->
                updateBIP39Record(seed, viewModel.getWordLength())
            }
        }
    }

    private fun updateBIP39Record(record: BIP39Record, wordLength: Int) {
        binding.seedView.text = record.seed
        binding.amountWord.text = wordLength.toString()
        Log.d("Mnemonic $wordLength Word", "${binding.seedView.text}")
    }

    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }
}
