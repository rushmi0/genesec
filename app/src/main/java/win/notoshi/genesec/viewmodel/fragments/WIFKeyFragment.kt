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
import win.notoshi.genesec.databinding.FragmentWifKeyBinding
import win.notoshi.genesec.databinding.QrDialogBinding
import win.notoshi.genesec.model.WIFKeyModel
import win.notoshi.genesec.model.record.WIFKeyRecord
import win.notoshi.genesec.service.utils.ShiftTo.shortenString
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class WIFKeyFragment : Fragment(R.layout.fragment_wif_key) {

    private lateinit var binding: FragmentWifKeyBinding
    private lateinit var viewModel: WIFKeyModel
    private lateinit var qrDialogBinding: QrDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[WIFKeyModel::class.java]
        binding = FragmentWifKeyBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        toKeyTypePage()
        newKeyPair()
        observeWIFKeyPair()
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
            viewModel.WIFKeyPair()
        }
    }

    private fun copyPriv(priv: String) {
        setupPushDownAnim(binding.copyPriv)
        binding.copyPriv.setOnClickListener {
            copyToClipboard(priv, "private key")
        }
    }

    private fun copyPub(pub: String) {
        setupPushDownAnim(binding.copyPub)
        binding.copyPub.setOnClickListener {
            copyToClipboard(pub, "public key")
        }
    }

    private fun observeWIFKeyPair() {
        lifecycleScope.launch {
            viewModel.WIF_KEY.collect { keyData ->
                Log.d("WIFKeyFragment", "WIF Key Pair Observed: $keyData")
                updateNostrKeyRecord(keyData)

                val priv = keyData.priv ?: ""
                val pub = keyData.pub ?: ""

                copyPriv(priv)
                copyPub(pub)

                qrcodeNsec(priv)
                qrcodeNpub(pub)
            }
        }
    }

    private fun updateNostrKeyRecord(record: WIFKeyRecord) {
        binding.privateKeyView.text = record.priv?.shortenString()
        binding.publicKeyView.text = record.pub?.shortenString()
    }

    private fun qrcodeNsec(priv: String?) {
        setupPushDownAnim(binding.qrPrivateKey)
        binding.qrPrivateKey.setOnClickListener {
            when {
                !priv.isNullOrEmpty() -> showQRDialog(priv)
                else -> {
                    Toast.makeText(requireContext(), "private key has not been created yet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun qrcodeNpub(pub: String) {
        setupPushDownAnim(binding.qrPublickey)
        binding.qrPublickey.setOnClickListener {
            when {
                pub.isNotEmpty() -> showQRDialog(pub)
                else -> {
                    Toast.makeText(requireContext(), "public key has not been created yet", Toast.LENGTH_SHORT).show()
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


    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }

}