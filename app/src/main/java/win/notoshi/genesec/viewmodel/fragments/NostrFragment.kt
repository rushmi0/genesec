package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import win.notoshi.genesec.model.record.ShortKeyRecord
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentNostrBinding
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.viewmodel.AppViewModelFactory

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.thekhaeng.pushdownanim.PushDownAnim


class NostrFragment : Fragment(R.layout.fragment_nostr) {

    private lateinit var binding: FragmentNostrBinding
    private lateinit var viewModel: NostrKeyModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[NostrKeyModel::class.java]
        binding = FragmentNostrBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.nostrkeyBTN.setOnClickListener {
            viewModel.rawKeyPair()
        }
        binding.goHome.setOnClickListener {
            findNavController().navigateUp()
        }

        copyNsec()
//        copyNpub()
//        copyPriv()
//        copyPub()
        observeNostrKeyPair()
        observeRawKeyPair()
    }

    private fun copyPub() {
        TODO("Not yet implemented")
    }

    private fun copyPriv() {
        TODO("Not yet implemented")
    }

    private fun copyNpub() {
        TODO("Not yet implemented")
    }

    private fun copyNsec() {
        PushDownAnim.setPushDownAnimTo(binding.copyNsec)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                val data = viewModel.NOSTR_KEY.value.nsec

                val clipboardManager =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clipData = ClipData.newPlainText("nsec", data)
                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(context, "nsec copied to clipboard", Toast.LENGTH_SHORT).show()
            }
    }


    private fun observeRawKeyPair() {
        lifecycleScope.launch {
            viewModel.SHOW_NOSTR_KEY.collect { keyData ->
                updateShortKeyRecord(keyData)
            }
        }
    }

    private fun observeNostrKeyPair() {
        lifecycleScope.launch {
            viewModel.NOSTR_KEY.collect { keyData ->
                updateNostrKeyRecord(keyData)
            }
        }
    }

    private fun updateShortKeyRecord(record: ShortKeyRecord) {
        binding.nsecView.text = record.nsec
        binding.npubView.text = record.npub
        binding.privView.text = record.priv
        binding.pubView.text = record.pub
    }

    private fun updateNostrKeyRecord(record: ShortKeyRecord) {
        binding.nsecView.text = record.nsec
        binding.npubView.text = record.npub
        binding.privView.text = record.priv
        binding.pubView.text = record.pub
    }


}