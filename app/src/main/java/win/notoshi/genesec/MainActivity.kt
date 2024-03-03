package win.notoshi.genesec

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import win.notoshi.genesec.databinding.ActivityMainBinding
import win.notoshi.genesec.test.LibraryFragment
import win.notoshi.genesec.test.ShortsFragment
import win.notoshi.genesec.test.SubscriptionFragment
import win.notoshi.genesec.viewmodel.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.shorts -> replaceFragment(ShortsFragment())
                R.id.subscriptions -> replaceFragment(SubscriptionFragment())
                R.id.library -> replaceFragment(LibraryFragment())
            }
            true
        }
        binding.fab.setOnClickListener {
            showBottomDialog()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val videoLayout: LinearLayout = dialog.findViewById(R.id.layoutVideo)
        val shortsLayout: LinearLayout = dialog.findViewById(R.id.layoutShorts)
        val liveLayout: LinearLayout = dialog.findViewById(R.id.layoutLive)
        val cancelButton: ImageView = dialog.findViewById(R.id.cancelButton)

        videoLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show()
        }

        shortsLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Create a short is Clicked", Toast.LENGTH_SHORT).show()
        }

        liveLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Go live is Clicked", Toast.LENGTH_SHORT).show()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

}