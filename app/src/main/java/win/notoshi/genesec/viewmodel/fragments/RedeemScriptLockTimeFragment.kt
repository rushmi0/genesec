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
import win.notoshi.genesec.databinding.FragmentRedeemScriptLocktimeBinding
import win.notoshi.genesec.databinding.QrDialogBinding
import win.notoshi.genesec.model.RedeemScriptLockTimeModel
import win.notoshi.genesec.model.record.LockTimeRecord
import win.notoshi.genesec.model.record.RedeemScriptRecord
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class RedeemScriptLockTimeFragment : Fragment(R.layout.fragment_redeem_script_locktime) {


    private lateinit var binding: FragmentRedeemScriptLocktimeBinding
    private lateinit var viewModel: RedeemScriptLockTimeModel
    private lateinit var qrDialogBinding: QrDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[RedeemScriptLockTimeModel::class.java]
        binding = FragmentRedeemScriptLocktimeBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        retrieveValue()
        buildLockTime()
        observeLockTime()
        goBackToLockTimeContractPage()
    }

    private fun observeLockTime() {
        lifecycleScope.launch {
            viewModel.CONTRACT.collect { data ->
                Log.d("Result Value", "Redeem Script: ${data.redeemScript}, Address: ${data.lockingScript}")
                updateLockTimeContract(data)

                val lockingScript = data.lockingScript
                val redeemScript = data.redeemScript

                copyLockingScript(lockingScript)
                qrcodeRedeemScript(redeemScript)
            }
        }
    }


    private fun qrcodeRedeemScript(redeemScript: String) {
        setupPushDownAnim(binding.qrcodeAddr)
        binding.qrcodeAddr.setOnClickListener {
            when {
                redeemScript.isNotEmpty() -> showQRDialog(redeemScript)
                else -> {
                Toast.makeText(requireContext(), "Redeem Script has not been created yet", Toast.LENGTH_SHORT).show()
            }
            }
        }
    }


    private fun copyLockingScript(lockingScript: String) {
        setupPushDownAnim(binding.copyAddr)
        binding.copyAddr.setOnClickListener {
            copyToClipboard(lockingScript, "Address")
        }
    }

    private fun updateLockTimeContract(record: RedeemScriptRecord) {
        binding.redeemView.text = record.redeemScript
        binding.addrView.text = record.lockingScript
    }

    private fun buildLockTime() {
        viewModel.processBuildContract()
    }

    private fun retrieveValue() {
        // ดึงค่า publicKey และ blockNumber จาก argument
        val publicKey = requireArguments().getString("publicKey", "")
        val blockNumber = requireArguments().getString("blockNumber", "")

        viewModel.getValue(publicKey, blockNumber)

        // แสดงค่าใน Log
        Log.d("Retrieve Value", "Public Key: $publicKey, Block Number: $blockNumber")
    }

    private fun goBackToLockTimeContractPage() {
        setupPushDownAnim(binding.goBackToLockTimeContractPage)
        binding.goBackToLockTimeContractPage.setOnClickListener {
            findNavController().navigateUp()
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