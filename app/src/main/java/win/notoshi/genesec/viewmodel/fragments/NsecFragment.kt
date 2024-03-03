//package win.notoshi.genesec.viewmodel.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import win.notoshi.genesec.R
//import win.notoshi.genesec.databinding.FragmentNsecBinding
//import win.notoshi.genesec.model.NostrnsecModel
//import win.notoshi.genesec.viewmodel.AppViewModelFactory
//
//class NsecFragment : Fragment(R.layout.fragment_nsec) {
//
//    private lateinit var binding: FragmentNsecBinding
//    private lateinit var viewModel: NostrnsecModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val factory = AppViewModelFactory(requireActivity())
//        viewModel = ViewModelProvider(this, factory)[NostrnsecModel::class.java]
//        binding = FragmentNsecBinding.inflate(layoutInflater)
//
//        return binding.root
//    }
//
//
//
//
//}