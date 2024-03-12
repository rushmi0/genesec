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
import win.notoshi.genesec.databinding.FragmentContractLocktimeBinding
import win.notoshi.genesec.model.LockTimeContractModel
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class LockTimeContractFragment : Fragment(R.layout.fragment_contract_locktime) {

    private lateinit var binding: FragmentContractLocktimeBinding
    private lateinit var viewModel: LockTimeContractModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[LockTimeContractModel::class.java]
        binding = FragmentContractLocktimeBinding.inflate(layoutInflater)
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