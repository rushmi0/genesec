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
            viewModel.nostrKeyPair()
        }

        binding.goHome.setOnClickListener {
            findNavController().navigateUp()
        }

        observeNostrKeyPair()
    }


    private fun copyNsec(nsec: String) {
        PushDownAnim.setPushDownAnimTo(binding.copyNsec)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                copyToClipboard(nsec, "nsec")
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
                copyNsec(keyData.nsec)
                copyNpub(keyData.npub)
            }
        }
    }

    private fun updateNostrKeyRecord(record: NostrKeyRecord) {
        binding.nsecView.text = record.nsec.shortenString()
        binding.npubView.text = record.npub.shortenString()
    }



    private fun String.shortenString(): String {
        val prefixLength = 13
        val suffixLength = 13

        // ตรวจสอบว่าข้อมูลไม่ว่างเปล่าหรือไม่
        if (this.isEmpty()) {
            return this
        }

        // ดึงข้อมูลที่ต้องการเก็บไว้ด้านหน้า
        val prefix = this.substring(0, minOf(prefixLength, this.length))

        // ดึงข้อมูลที่ต้องการเก็บไว้ด้านหลัง
        val suffix = this.substring(maxOf(0, this.length - suffixLength))

        return "$prefix....$suffix"
    }



}