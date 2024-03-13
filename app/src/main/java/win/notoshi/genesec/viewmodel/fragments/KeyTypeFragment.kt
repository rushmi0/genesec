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
import win.notoshi.genesec.databinding.FragmentKeyTypeBinding
import win.notoshi.genesec.model.KeyTypeModel
import win.notoshi.genesec.viewmodel.AppViewModelFactory

class KeyTypeFragment : Fragment(R.layout.fragment_key_type) {


    private lateinit var binding: FragmentKeyTypeBinding
    private lateinit var viewModel: KeyTypeModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AppViewModelFactory(requireActivity())
        viewModel = ViewModelProvider(this, factory)[KeyTypeModel::class.java]
        binding = FragmentKeyTypeBinding.inflate(layoutInflater)

        toHomePage()
        toWIFKeyPage()
        toNostrKeyPage()
        return binding.root
    }

    private fun toHomePage() {
        setupPushDownAnim(binding.goHome)
        binding.goHome.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun toNostrKeyPage() {
        setupPushDownAnim(binding.nostrKeyBTN)
        binding.nostrKeyBTN.setOnClickListener {
            findNavController().navigate(KeyTypeFragmentDirections.actionKeyTypeFragmentToNostrKeyFragment())
        }
    }

    private fun toWIFKeyPage() {
        setupPushDownAnim(binding.wifKeyBTN)
        binding.wifKeyBTN.setOnClickListener {
            findNavController().navigate(KeyTypeFragmentDirections.actionKeyTypeFragmentToWIFKeyFragment())
        }
    }


    private fun setupPushDownAnim(view: View) {
        PushDownAnim.setPushDownAnimTo(view)
            .setScale(PushDownAnim.MODE_SCALE, 0.70f)
    }

}