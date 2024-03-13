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
import win.notoshi.genesec.databinding.FragmentNostrKeyBinding
import win.notoshi.genesec.databinding.QrNostrDialogBinding
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.model.record.NostrKeyRecord
import win.notoshi.genesec.service.utils.ShiftTo.shortenString
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class NostrKeyFragment : Fragment(R.layout.fragment_nostr_key) {

    private lateinit var binding: FragmentNostrKeyBinding
    private lateinit var viewModel: NostrKeyModel
    private lateinit var qrDialogBinding: QrNostrDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[NostrKeyModel::class.java]
        binding = FragmentNostrKeyBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        toKeyTypePage()
        newKeyPair()
        observeNostrKeyPair()
    }

    private fun toKeyTypePage() {
        setupPushDownAnim(binding.goKeyTypePage)
        binding.goKeyTypePage.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun newKeyPair() {
        setupPushDownAnim(binding.newKeytBTN)
        binding.newKeytBTN.setOnClickListener {
            viewModel.nostrKeyPair()
        }
    }

    private fun copyNsec(nsec: String) {
        setupPushDownAnim(binding.copyPriv)
        binding.copyPriv.setOnClickListener {
            copyToClipboard(nsec, "nsec")
        }
    }

    private fun copyNpub(npub: String) {
        setupPushDownAnim(binding.copyPub)
        binding.copyPub.setOnClickListener {
            copyToClipboard(npub, "npub")
        }
    }

    private fun observeNostrKeyPair() {
        lifecycleScope.launch {
            viewModel.NOSTR_KEY.collect { keyData ->
                Log.d("NostrKeyFragment", "Nostr Key Pair Observed: $keyData")
                updateNostrKeyRecord(keyData)

                val nsec = keyData.nsec ?: ""
                val npub = keyData.npub ?: ""

                copyNsec(nsec)
                copyNpub(npub)

                qrcodeNsec(nsec)
                qrcodeNpub(npub)
            }
        }
    }

    private fun updateNostrKeyRecord(record: NostrKeyRecord) {
        binding.privateKeyView.text = record.nsec?.shortenString()
        binding.publicKeyView.text = record.npub?.shortenString()
    }

    private fun qrcodeNsec(nsec: String?) {
        setupPushDownAnim(binding.qrPrivateKey)
        binding.qrPrivateKey.setOnClickListener {
            when {
                !nsec.isNullOrEmpty() -> showQRDialog(nsec)
                else -> {
                    Toast.makeText(requireContext(), "nsec has not been created yet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun qrcodeNpub(npub: String) {
        setupPushDownAnim(binding.qrPublickey)
        binding.qrPublickey.setOnClickListener {
            when {
                npub.isNotEmpty() -> showQRDialog(npub)
                else -> {
                    Toast.makeText(requireContext(), "nsec has not been created yet", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun showQRDialog(data: String) {
        qrDialogBinding = QrNostrDialogBinding.inflate(layoutInflater)
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


    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }

}
