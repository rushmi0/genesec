package win.notoshi.genesec.viewmodel.fragments

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.launch
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentNostrBinding
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.model.record.NostrKeyRecord
import win.notoshi.genesec.viewmodel.AppViewModelFactory


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
        newKeyPair()
        goHome()
        observeNostrKeyPair()
    }

    private fun goHome() {
        PushDownAnim.setPushDownAnimTo(binding.goHome)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
            .setOnClickListener {
                findNavController().navigateUp()
            }
    }


    private fun newKeyPair() {
        PushDownAnim.setPushDownAnimTo(binding.nostrkeyBTN)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
            .setOnClickListener {
                viewModel.nostrKeyPair()
            }
    }


    private fun copyNsec(nsec: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyNsec)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
            .setOnClickListener {
                copyToClipboard(nsec, "nsec")
            }
    }


    private fun copyNpub(npub: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyNpub)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
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

    private fun generateQRCode(content: String): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400, 400)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun showQRCodeDialog(content: String) {
        val bitmap = generateQRCode(content)
        bitmap?.let {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.qrcode_dialog)
            val qrCodeImageView = dialog.findViewById<ImageView>(R.id.qrCodeImageView)
            qrCodeImageView.setImageBitmap(bitmap)
            dialog.show()
        }
    }


    private fun qrcodeNsec(nsec: String) {
        PushDownAnim.setPushDownAnimTo(binding.qrcodeNsec)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
            .setOnClickListener {
                showQRCodeDialog(nsec)
            }
    }

    private fun qrcodeNpub(npub: String) {
        PushDownAnim.setPushDownAnimTo(binding.qrcodeNpub)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
            .setOnClickListener {
                showQRCodeDialog(npub)
            }
    }

    private fun updateNostrKeyRecord(record: NostrKeyRecord) {
        binding.nsecView.text = record.nsec.shortenString()
        binding.npubView.text = record.npub.shortenString()
    }


    private fun String.shortenString(): String {
        val prefixLength = 10
        val suffixLength = 10

        // ตรวจสอบว่าข้อมูลไม่ว่างเปล่าหรือไม่
        if (this.isEmpty()) {
            return this
        }

        val prefix = this.substring(0, minOf(prefixLength, this.length))
        val suffix = this.substring(maxOf(0, this.length - suffixLength))

        return "$prefix....$suffix"
    }


}