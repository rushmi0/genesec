package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thekhaeng.pushdownanim.PushDownAnim
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        toNostrPage()
        toMnemonicPage()
        toContractPage()
        //toTransactionPage()
        return binding.root
    }

    /*
    private fun toTransactionPage() {
        PushDownAnim.setPushDownAnimTo(binding.transactionBTN)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTransactionFragment())
            }
    }
     */

    private fun toContractPage() {
        PushDownAnim.setPushDownAnimTo(binding.contractBTN)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToContractFragment())
            }
    }


    private fun toMnemonicPage() {
        PushDownAnim.setPushDownAnimTo(binding.mnemonicBTN)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMnemonicFragment())
            }
    }

    private fun toNostrPage() {
        PushDownAnim.setPushDownAnimTo(binding.nostrBTN)
            .setScale(PushDownAnim.MODE_SCALE, 0.90f)
            .setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNostrFragment())
            }
    }



}