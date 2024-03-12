package win.notoshi.genesec.viewmodel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.thekhaeng.pushdownanim.PushDownAnim
import win.notoshi.genesec.R
import win.notoshi.genesec.databinding.FragmentContractBinding
import win.notoshi.genesec.model.ContractModel
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class ContractFragment : Fragment(R.layout.fragment_contract) {

    private lateinit var binding: FragmentContractBinding
    private lateinit var viewModel: ContractModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[ContractModel::class.java]
        binding = FragmentContractBinding.inflate(layoutInflater)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        toHomePage()
    }

    private fun toHomePage() {
        setupPushDownAnim(binding.goHome)
        binding.goHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }

}