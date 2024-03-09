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
import android.util.Log
import android.widget.Toast
import com.thekhaeng.pushdownanim.PushDownAnim
import win.notoshi.genesec.model.record.NostrKeyRecord


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

        observeNostrKeyPair()
        observeRawKeyPair()
    }


    private fun copyPriv(priv: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyPriv)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                copyToClipboard(priv, "priv")
            }
    }

    private fun copyNsec(nsec: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyNsec)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                copyToClipboard(nsec, "nsec")
            }
    }

    private fun copyPub(pub: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyPub)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                copyToClipboard(pub, "pub")
            }
    }

    private fun copyNpub(npub: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyNpub)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                copyToClipboard(npub, "npub")
            }
    }


    private fun copyToClipboard(data: String?, label: String) {
        try {
            if (!data.isNullOrEmpty()) {
                val clipboardManager =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clipData = ClipData.newPlainText(label, data)
                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No $label data available", Toast.LENGTH_SHORT).show()
            }
            Log.d("ClipboardDebug", "Data: $data, Label: $label")
        } catch (e: Exception) {
            Log.e("ClipboardError", "Error copying to clipboard: ${e.message}")
        }
    }


    private fun observeRawKeyPair() {
        lifecycleScope.launch {
            viewModel.SHOW_NOSTR_KEY.collect { keyData ->
                Log.d("NostrFragment", "Raw Key Pair Observed: $keyData")
                updateShortKeyRecord(keyData)
            }
        }
    }

    private fun observeNostrKeyPair() {
        viewModel.nostrKeyPair()
        lifecycleScope.launch {
            viewModel.NOSTR_KEY.collect { keyData ->
                Log.d("NostrFragment", "Nostr Key Pair Observed: $keyData")
                updateNostrKeyRecord(keyData)
                copyNsec(keyData.nsec)
                copyNpub(keyData.npub)
                copyPriv(keyData.priv)
                copyPub(keyData.pub)
            }
        }
    }

    private fun updateShortKeyRecord(record: ShortKeyRecord) {
        binding.nsecView.text = record.nsec
        binding.npubView.text = record.npub
        binding.privView.text = record.priv
        binding.pubView.text = record.pub
    }

    private fun updateNostrKeyRecord(record: NostrKeyRecord) {
        binding.nsecView.text = record.nsec
        binding.npubView.text = record.npub
        binding.privView.text = record.priv
        binding.pubView.text = record.pub
    }


}