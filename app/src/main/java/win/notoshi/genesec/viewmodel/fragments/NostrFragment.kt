package win.notoshi.genesec.viewmodel.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.launch
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentNostrBinding
import win.notoshi.genesec.databinding.QrDialogBinding
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.model.record.NostrKeyRecord
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class NostrFragment : Fragment(R.layout.fragment_nostr) {

    private lateinit var binding: FragmentNostrBinding
    private lateinit var viewModel: NostrKeyModel
    private lateinit var qrDialogBinding: QrDialogBinding

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
        newKeyPair()
        goHome()
        observeNostrKeyPair()
    }

    private fun goHome() {
        setupPushDownAnim(binding.goHome)
        binding.goHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun newKeyPair() {
        setupPushDownAnim(binding.nostrkeyBTN)
        binding.nostrkeyBTN.setOnClickListener {
            viewModel.nostrKeyPair()
        }
    }

    private fun copyNsec(nsec: String) {
        setupPushDownAnim(binding.copyNsec)
        binding.copyNsec.setOnClickListener {
            copyToClipboard(nsec, "nsec")
        }
    }

    private fun copyNpub(npub: String) {
        setupPushDownAnim(binding.copyNpub)
        binding.copyNpub.setOnClickListener {
            copyToClipboard(npub, "npub")
        }
    }

    private fun copyToClipboard(data: String?, label: String) {
        try {
            if (!data.isNullOrEmpty()) {
                val clipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(label, data)
                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No $label data available", Toast.LENGTH_SHORT).show()
            }
            Log.d("Clipboard [DEBUG] ", "Data: $data, Label: $label")
        } catch (e: Exception) {
            Log.e("Clipboard [ERROR] ", "Error copying to clipboard: ${e.message}")
        }
    }

    private fun observeNostrKeyPair() {
        lifecycleScope.launch {
            viewModel.NOSTR_KEY.collect { keyData ->
                Log.d("NostrFragment", "Nostr Key Pair Observed: $keyData")
                updateNostrKeyRecord(keyData)

                val nsec = keyData.nsec
                val npub = keyData.npub

                copyNsec(nsec)
                copyNpub(npub)

                qrcodeNsec(nsec)
                qrcodeNpub(npub)
            }
        }
    }

    private fun qrcodeNsec(nsec: String?) {
        setupPushDownAnim(binding.qrcodeNsec)
        binding.qrcodeNsec.setOnClickListener {
            when {
                !nsec.isNullOrEmpty() -> showQRDialog(nsec)
                else -> {
                    Toast.makeText(requireContext(), "nsec has not been created yet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun qrcodeNpub(npub: String) {
        setupPushDownAnim(binding.qrcodeNpub)
        binding.qrcodeNpub.setOnClickListener {
            when {
                npub.isNotEmpty() -> showQRDialog(npub)
                else -> {
                    Toast.makeText(requireContext(), "nsec has not been created yet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showQRDialog(data: String) {
        qrDialogBinding = QrDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).apply {
            setView(qrDialogBinding.root)
        }.create()

        qrDialogBinding.preViewKey.text = data
        qrDialogBinding.qrcodeImageView.setImageBitmap(viewModel.generateQRCode(data))

        setupPushDownAnim(qrDialogBinding.closeBTN)
        qrDialogBinding.closeBTN.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateNostrKeyRecord(record: NostrKeyRecord) {
        binding.nsecView.text = record.nsec.shortenString()
        binding.npubView.text = record.npub.shortenString()
    }

    private fun String.shortenString(): String {
        val prefixLength = 10
        val suffixLength = 10

        return when {
            this.isNotEmpty() -> {
                val prefix = this.substring(0, minOf(prefixLength, length))
                val suffix = this.substring(maxOf(0, length - suffixLength))
                "$prefix....$suffix"
            }
            else -> this
        }
    }

    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }

}
