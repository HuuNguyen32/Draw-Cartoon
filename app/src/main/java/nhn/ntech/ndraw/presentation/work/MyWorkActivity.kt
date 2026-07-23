package nhn.ntech.ndraw.presentation.work

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.databinding.ActivityMyWorkBinding
import nhn.ntech.ndraw.databinding.CustomPopupBinding
import nhn.ntech.ndraw.presentation.detail.DetailWorkActivity
import nhn.ntech.ndraw.utils.DialogUtils
import nhn.ntech.ndraw.utils.MediaUtils
import java.io.File

class MyWorkActivity : BaseActivity() {

    private lateinit var binding: ActivityMyWorkBinding
    private lateinit var myWorkAdapter: MyWorkAdapter
    private lateinit var viewModel: MyWorkViewModel
    private var isPhotoSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MyWorkViewModel::class.java]
        setPaddingScreen()
        initView()
        setOnListener()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData(filesDir)
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                render(state)
            }
        }
    }

    private fun render(state: MyWorkUIState) = with(binding) {
        if (!::myWorkAdapter.isInitialized) return@with

        val isPhoto = state.isCateMode == CateMode.PHOTO

        isPhotoSelected = isPhoto

        val currentList = if (isPhoto) state.listFile else state.listVideo

        myWorkAdapter.updateData(currentList)

        updateTabUI(isPhoto)

        val isEmpty = currentList.isEmpty()

        tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        rvMyWork.visibility = if (isEmpty) View.GONE else View.VISIBLE
        tvEmpty.text = getString(if (isPhoto) R.string.no_photos_yet else R.string.no_videos_yet)
    }

    private fun updateTabUI(isPhotoSelected: Boolean) = with(binding) {
        if (isPhotoSelected) {
            btnCateVideo.setBackgroundColor(
                ContextCompat.getColor(
                    this@MyWorkActivity,
                    android.R.color.transparent
                )
            )
            btnCateVideo.setTextColor(
                ContextCompat.getColor(
                    this@MyWorkActivity,
                    R.color.black
                )
            )
            btnCatePhoto.setBackgroundResource(R.drawable.category_selected_bg)
            btnCatePhoto.setTextColor(
                ContextCompat.getColor(
                    this@MyWorkActivity,
                    R.color.white
                )
            )
        } else {
            btnCatePhoto.setBackgroundColor(
                ContextCompat.getColor(
                    this@MyWorkActivity,
                    android.R.color.transparent
                )
            )
            btnCatePhoto.setTextColor(
                ContextCompat.getColor(
                    this@MyWorkActivity,
                    R.color.black
                )
            )
            btnCateVideo.setBackgroundResource(R.drawable.category_selected_bg)
            btnCateVideo.setTextColor(
                ContextCompat.getColor(
                    this@MyWorkActivity,
                    R.color.white
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnListener() = with(binding) {
        btnCatePhoto.setOnClickListener {
            exitFromSelectMode()
            viewModel.updateCateMode(CateMode.PHOTO, filesDir)
        }

        btnCateVideo.setOnClickListener {
            exitFromSelectMode()
            viewModel.updateCateMode(CateMode.VIDEO, filesDir)
        }

        btnBack.setOnClickListener {
            exitFromSelectMode()
            finish()
        }

        btnSelectMore.setOnClickListener {
            if (::myWorkAdapter.isInitialized) {
                myWorkAdapter.selectAll()
            }
        }

        btnDelete.setOnClickListener {
            if (::myWorkAdapter.isInitialized) {
                val selectedFiles = myWorkAdapter.getSelectedItems()
                if (selectedFiles.isEmpty()) return@setOnClickListener
                confirmAndDeleteFiles(selectedFiles)
            }
        }

        main.setOnClickListener {
            exitFromSelectMode()
        }

        rvMyWork.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (::myWorkAdapter.isInitialized && myWorkAdapter.isSelectionMode) {
                    val child = rvMyWork.findChildViewUnder(event.x, event.y)
                    if (child == null) {
                        myWorkAdapter.exitSelectionMode()
                    }
                }
            }
            false
        }

        btnDownload.setOnClickListener {
            if (::myWorkAdapter.isInitialized) {
                val selectedFiles = myWorkAdapter.getSelectedItems()
                if (selectedFiles.isEmpty()) return@setOnClickListener
                lifecycleScope.launch {
                    var successCount = 0
                    for (file in selectedFiles) {
                        val success = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            MediaUtils.saveToGallery(this@MyWorkActivity, file, isPhotoSelected)
                        }
                        if (success) successCount++
                    }
                    Toast.makeText(
                        this@MyWorkActivity,
                        if (successCount > 0) getString(R.string.download_success)
                        else getString(R.string.download_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnShare.setOnClickListener {
            if (::myWorkAdapter.isInitialized) {
                val selectedFiles = myWorkAdapter.getSelectedItems()
                if (selectedFiles.isEmpty()) return@setOnClickListener
                MediaUtils.shareFiles(this@MyWorkActivity, selectedFiles, isPhotoSelected)
            }
        }
    }

    private fun initView() {
        myWorkAdapter = MyWorkAdapter(
            emptyList(),
            onItemClick = { file ->
                val intent = Intent(this@MyWorkActivity, DetailWorkActivity::class.java)
                intent.putExtra(Const.FILE_TAG, file.absolutePath)
                intent.putExtra(Const.IS_PHOTO_TAG, isPhotoSelected)
                startActivity(intent)
            },
            onMoreClick = { file, itemView ->
                setPopUp(file, itemView)
            },
            onSelectionModeChange = { isSelectionMode ->
                updateSelectionUI(isSelectionMode)
            },
            onSelectionChange = { count ->

            }
        )
        binding.rvMyWork.apply {
            layoutManager = GridLayoutManager(this@MyWorkActivity, 2)
            adapter = myWorkAdapter
            setHasFixedSize(true)
            itemAnimator = null
        }
    }

    private fun updateSelectionUI(isSelectionMode: Boolean) {
        with(binding) {
            if (isSelectionMode) {
                btnSelectMore.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                bottomBarContainer.visibility = View.VISIBLE
            } else {
                btnSelectMore.visibility = View.GONE
                btnDelete.visibility = View.GONE
                bottomBarContainer.visibility = View.GONE
            }
        }
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun exitFromSelectMode() {
        if (::myWorkAdapter.isInitialized && myWorkAdapter.isSelectionMode) myWorkAdapter.exitSelectionMode()
    }

    private fun setPopUp(file: File, view: View) {
        val popupBinding = CustomPopupBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupBinding.root.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupWidth = popupBinding.root.measuredWidth
        val xOff = popupWidth - view.width

        popupWindow.showAsDropDown(view, -xOff, 10)
        popupWindow.isOutsideTouchable = true

        with(popupBinding) {
            tvDownload.setOnClickListener {
                lifecycleScope.launch {
                    val success = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        MediaUtils.saveToGallery(this@MyWorkActivity, file, isPhotoSelected)
                    }
                    Toast.makeText(
                        this@MyWorkActivity,
                        if (success) getString(R.string.download_success)
                        else getString(R.string.download_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                popupWindow.dismiss()
            }
            tvShare.setOnClickListener {
                MediaUtils.shareFile(this@MyWorkActivity, file, isPhotoSelected)
                popupWindow.dismiss()
            }
            tvDelete.setOnClickListener {
                confirmAndDeleteFiles(listOf(file))
                popupWindow.dismiss()
            }
        }
    }

    private fun confirmAndDeleteFiles(selectedFiles: List<File>) {
        DialogUtils.createConfirmDialog(
            this@MyWorkActivity,
            getString(R.string.delete_title),
            getString(R.string.delete_description),
            onConfirm = {
                viewModel.deleteFiles(selectedFiles, filesDir) {
                    exitFromSelectMode()
                    Toast.makeText(
                        this@MyWorkActivity,
                        getString(R.string.success_delete),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}