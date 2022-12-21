package org.nudt.player.ui.mine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.nudt.player.databinding.ActivityGbookBinding

class GBookActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGbookBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}