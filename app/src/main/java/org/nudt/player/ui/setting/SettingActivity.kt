package org.nudt.player.ui.setting

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.nudt.player.R
import org.nudt.player.databinding.ActivitySettingBinding
import org.nudt.player.utils.SpUtils


class SettingActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    private val baseUrl = SpUtils.baseUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tbCommon.tvTitle.text = getString(R.string.main_nav_config)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        binding.tvUrl.text = baseUrl

        binding.clConfigUrl.setOnClickListener {
            val editText = EditText(this@SettingActivity)
            editText.text.append(baseUrl)
            val inputDialog = AlertDialog.Builder(this@SettingActivity, R.style.AlertDialog)
            inputDialog.setTitle("设置基本网址").setView(editText)
            inputDialog.setPositiveButton("确定") { dialog, which ->
                SpUtils.baseUrl = editText.text.toString()
                binding.tvUrl.text = SpUtils.baseUrl
            }.show()
        }


    }
}