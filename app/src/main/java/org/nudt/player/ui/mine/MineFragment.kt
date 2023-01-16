package org.nudt.player.ui.mine

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.shortToast
import org.nudt.player.adapter.MineHistoryAdapter
import org.nudt.player.data.network.doSuccess
import org.nudt.player.databinding.FragmentMineBinding
import org.nudt.player.ui.MainActivity.Companion.CAMERA_REQ_CODE
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.download.VideoDownloadListActivity
import org.nudt.player.ui.history.PlayHistoryActivity
import org.nudt.player.ui.setting.SettingActivity
import org.nudt.player.utils.SpUtils

class MineFragment : Fragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    private val videoViewModel: VideoViewModel by viewModel()
    private lateinit var historyAdapter: MineHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        initHistoryView()

        initModulesView()

        initCheckUpdateView()

        initSettingView()
    }

    private fun initCheckUpdateView() {
        videoViewModel.checkUpdate()

        videoViewModel.checkUpdateResult.observe(viewLifecycleOwner) { resultState ->
            resultState.doSuccess {
                val packageManager = context!!.packageManager
                val packageInfo = packageManager.getPackageInfo(context!!.packageName, 0)
                val versionCode = packageInfo.longVersionCode
                if (it.version_code > versionCode) {
                    binding.clNewVersion.visibility = View.VISIBLE
                    binding.tvNewVersion.text = "版本更新(${it.version})"
                } else {
                    binding.clNewVersion.visibility = View.GONE
                }
            }
        }

        binding.clNewVersion.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            context?.startActivity(intent)
        }
    }

    private fun initSettingView() {

        binding.clConfig.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            context?.startActivity(intent)
        }
    }

    /**
     * 工具栏
     */
    private fun initToolbar() {
        binding.ivScan.setOnClickListener {
            activity?.let { ac ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        ac, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES),
                        CAMERA_REQ_CODE
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        ac, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                        CAMERA_REQ_CODE
                    )
                }
            }
        }

//        binding.ivConfig.setOnClickListener {
//            val intent = Intent(context, ConfigActivity::class.java)
//            context?.startActivity(intent)
//        }
    }

    /**
     * history 部分
     */
    private fun initHistoryView() {
        binding.clHistory.setOnClickListener {
            val intent = Intent(context, PlayHistoryActivity::class.java)
            context?.startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVideoHistory.layoutManager = linearLayoutManager

        context?.let {
            historyAdapter = MineHistoryAdapter(it)
            binding.rvVideoHistory.adapter = historyAdapter
        }

        videoViewModel.historyTop.observe(viewLifecycleOwner) {
            historyAdapter.updateFavoriteList(it)
        }
    }

    private fun initModulesView() {
        binding.tvNickName.text = SpUtils.userNickName
        binding.tvUserName.text = "ID：" + SpUtils.username


        binding.btnDownload.setOnClickListener {
            val intent = Intent(context, VideoDownloadListActivity::class.java)
            context?.startActivity(intent)
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(context, PlayHistoryActivity::class.java)
            context?.startActivity(intent)
        }

        binding.btnContribute.setOnClickListener {
            shortToast("敬请期待视频投稿功能")
        }
    }

}