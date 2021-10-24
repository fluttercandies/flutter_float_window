//package com.example.flutter_float_window
//
//import android.content.Context
//import android.util.Log
//import android.view.Gravity
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.SeekBar
//import android.widget.TextView
//import androidx.core.content.ContextCompat.startActivity
//import com.lzf.easyfloat.EasyFloat
//import com.lzf.easyfloat.enums.ShowPattern
//import com.lzf.easyfloat.enums.SidePattern
//import com.lzf.easyfloat.interfaces.OnTouchRangeListener
//import com.lzf.easyfloat.permission.PermissionUtils
//import com.lzf.easyfloat.utils.DragUtils
//import com.lzf.easyfloat.widget.BaseSwitchView
//
//class FlutterFloatManage {
//
//    private val TAG = "FlutterFloatWindowPlugin"
//
//    /**
//     * 检测浮窗权限是否开启，若没有给与申请提示框（非必须，申请依旧是EasyFloat内部内保进行）
//     */
//    fun checkPermission(context: Context) {
//        if (PermissionUtils.checkPermission(context)) {
//            showAppFloat(context)
//            Log.e(TAG, "使用浮窗功能，已经有权限拉。")
//        } else {
//            Log.e(TAG, "使用浮窗功能，需要您授权悬浮窗权限。")
////            AlertDialog.Builder(this)
////                    .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
////                    .setPositiveButton("去开启") { _, _ ->
////                        if (tag == null) showAppFloat() else showAppFloat2(tag)
////                    }
////                    .setNegativeButton("取消") { _, _ -> }
////                    .show()
//        }
//    }
//
//    private fun showAppFloat(context: Context) {
//        Log.e("Manage","showAppFloat")
//        EasyFloat.with(context)
//                .setShowPattern(ShowPattern.ALL_TIME)
//                .setSidePattern(SidePattern.RESULT_SIDE)
//                .setImmersionStatusBar(true)
//                .setGravity(Gravity.END, -20, 10)
//                .setLayout(R.layout.float_app) {
//                    Log.e("Manage","进入到里面")
//                    it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
//                        EasyFloat.dismiss()
//                    }
//                    it.findViewById<TextView>(R.id.tvOpenMain).setOnClickListener {
////                        startActivity<MainActivity>(this)
//                    }
//                    it.findViewById<CheckBox>(R.id.checkbox)
//                            .setOnCheckedChangeListener { _, isChecked -> EasyFloat.dragEnable(isChecked) }
//
////                    val progressBar = it.findViewById<RoundProgressBar>(R.id.roundProgressBar).apply {
////                        setProgress(66, "66")
////                        setOnClickListener { toast(getProgressStr()) }
////                    }
////                    it.findViewById<SeekBar>(R.id.seekBar)
////                            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
////                                override fun onProgressChanged(
////                                        seekBar: SeekBar?, progress: Int, fromUser: Boolean
////                                ) = progressBar.setProgress(progress, progress.toString())
////
////                                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
////
////                                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
////                            })
//
////                // 解决 ListView 拖拽滑动冲突
////                it.findViewById<ListView>(R.id.lv_test).apply {
////                    adapter = MyAdapter(
////                        this@MainActivity,
////                        arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "...")
////                    )
////
////                    // 监听 ListView 的触摸事件，手指触摸时关闭拖拽，手指离开重新开启拖拽
////                    setOnTouchListener { _, event ->
////                        logger.e("listView: ${event.action}")
////                        EasyFloat.appFloatDragEnable(event?.action == MotionEvent.ACTION_UP)
////                        false
////                    }
////                }
//                }
//                .registerCallback {
//                    drag { _, motionEvent ->
//                        DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
//                            override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
////                                setVibrator(inRange)
//                                view.findViewById<TextView>(com.lzf.easyfloat.R.id.tv_delete).text =
//                                        if (inRange) "松手删除" else "删除浮窗"
//
//                                view.findViewById<ImageView>(com.lzf.easyfloat.R.id.iv_delete)
//                                        .setImageResource(
//                                                if (inRange) com.lzf.easyfloat.R.drawable.icon_delete_selected
//                                                else com.lzf.easyfloat.R.drawable.icon_delete_normal
//                                        )
//                            }
//
//                            override fun touchUpInRange() {
//                                EasyFloat.dismiss()
//                            }
//                        }, showPattern = ShowPattern.ALL_TIME)
//                    }
//                }
//                .show()
//    }
//
//
//
////    private fun showAppFloat2(tag: String) {
////        EasyFloat.with(this.applicationContext)
////                .setTag(tag)
////                .setShowPattern(ShowPattern.FOREGROUND)
////                .setLocation(100, 100)
////                .setAnimator(null)
////                .setFilter()//SecondActivity::class.java
////                .setLayout(R.layout.float_app_scale) {
////                    val content = it.findViewById<RelativeLayout>(R.id.rlContent)
////                    val params = content.layoutParams as FrameLayout.LayoutParams
////                    it.findViewById<ScaleImage>(R.id.ivScale).onScaledListener =
////                            object : ScaleImage.OnScaledListener {
////                                override fun onScaled(x: Float, y: Float, event: MotionEvent) {
////                                    params.width = max(params.width + x.toInt(), 200)
////                                    params.height = max(params.height + y.toInt(), 200)
////                                    content.layoutParams = params
////                                }
////                            }
////
////                    it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
////                        EasyFloat.dismiss(tag)
////                    }
////                }
////                .show()
////    }
//
//}