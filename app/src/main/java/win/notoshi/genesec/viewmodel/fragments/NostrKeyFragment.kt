package win.notoshi.genesec.viewmodel.fragments

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
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
import win.notoshi.genesec.databinding.QrDialogBinding
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.model.record.NostrKeyRecord
import win.notoshi.genesec.service.utils.ShiftTo.shortenString
import win.notoshi.genesec.viewmodel.AppViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NostrKeyFragment : Fragment(R.layout.fragment_nostr_key) {

    private lateinit var binding: FragmentNostrKeyBinding
    private lateinit var viewModel: NostrKeyModel
    private lateinit var dialogBinding: QrDialogBinding

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
                !nsec.isNullOrEmpty() -> showCustomDialogBox(nsec)
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "nsec has not been created yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun qrcodeNpub(npub: String) {
        setupPushDownAnim(binding.qrPublickey)
        binding.qrPublickey.setOnClickListener {
            when {
                npub.isNotEmpty() -> showCustomDialogBox(npub)
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "nsec has not been created yet",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun showCustomDialogBox(data: String?) {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialogBinding = QrDialogBinding.inflate(LayoutInflater.from(requireContext()))

        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.preViewKey.text = data
        dialogBinding.qrcodeImageView.setImageBitmap(data?.let { viewModel.generateQRCode(it) })

        setupPushDownAnim(dialogBinding.closeBTN)
        dialogBinding.closeBTN.setOnClickListener {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            dialog.dismiss()
        }

        setupPushDownAnim(dialogBinding.saveQRCode)
        dialogBinding.saveQRCode.setOnClickListener {
            saveImageToGallery(dialogBinding.qrcodeImageView, data)
        }
        dialog.show()
    }

    private fun saveImageToGallery(imageView: ImageView, data: String?) {
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap

        // ถ้า bitmap เป็น null หรือ data เป็น null จะไม่ทำการดำเนินการต่อ
        bitmap?.let { safeBitmap ->
            data?.let { safeData ->
                val name = safeData.take(8) + "..." + safeData.takeLast(5)
                val imageFileName = "${name}.png"

                val storageDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

                val imageFile = File(storageDirectory, imageFileName)
                try {
                    val outputStream = FileOutputStream(imageFile)
                    safeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    MediaScannerConnection.scanFile(
                        requireContext(),
                        arrayOf(imageFile.absolutePath),
                        arrayOf("image/png"),
                        null
                    )

                    Toast.makeText(
                        requireContext(),
                        "image saved to gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "Failed to save QR Code image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "Bitmap or data is null", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }

}
