package nhn.ntech.ndraw.presentation.Intro

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import nhn.ntech.ndraw.BaseActivity
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivityIntroBinding
import nhn.ntech.ndraw.domain.prefs.UserPreferences
import nhn.ntech.ndraw.presentation.home.MainActivity
import nhn.ntech.ndraw.presentation.permission.PermissionActivity
import nhn.ntech.ndraw.ext.setTextGradientColor

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewModel: IntroViewModel
    private lateinit var userPreferences: UserPreferences
    private lateinit var adapter: IntroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPaddingScreen()
        userPreferences = UserPreferences(this)
        val factory = IntroViewModelFactory(userPreferences)
        viewModel = ViewModelProvider(this, factory)[IntroViewModel::class.java]
        initView()
        setAdapter()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.introViewPager.currentItem
            if (currentItem < adapter.itemCount - 1) {
                binding.introViewPager.setCurrentItem(currentItem + 1, true)
                updateIndicator(currentItem + 1)
            } else {
                if (!viewModel.getPermissionVisited()) {
                    viewModel.setPermissionVisited(true)
                    startActivity(Intent(this, PermissionActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }

    private fun initView() {
        with(binding) {
            val colors = intArrayOf(
                "#B7ADF4".toColorInt(),
                "#DFA1F6".toColorInt()
            )

            val positions = floatArrayOf(
                0.0f,
                1f
            )
            btnNext.setTextGradientColor(colors = colors, positions = positions)
        }
    }

    private fun setAdapter() {
        val intros = listOf(
            Intro(R.drawable.intro_1, getString(R.string.intro_1_title)),
            Intro(R.drawable.intro_2, getString(R.string.intro_2_title)),
            Intro(R.drawable.intro_3, getString(R.string.intro_3_title))
        )

        adapter = IntroAdapter(intros)
        binding.introViewPager.adapter = adapter
        setIndicatorLayout(intros.size)
        updateIndicator(0)
        binding.introViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicator(position)
            }
        })

    }

    private fun setIndicatorLayout(amountIndicator: Int) {
        val indicators = arrayOfNulls<ImageView>(amountIndicator)
        binding.indicatorsLayout.removeAllViews()

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            val layoutParams = LinearLayout.LayoutParams(
                dpToPx(8),
                dpToPx(8)
            )
            layoutParams.setMargins(8, 0, 8, 0)
            indicators[i]?.apply {
                this.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.dot_unselected_bg
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.indicatorsLayout.addView(indicators[i])
        }
    }

    private fun updateIndicator(position: Int) {
        val indicators = binding.indicatorsLayout.childCount
        for (i in 0 until indicators) {
            val indicator = binding.indicatorsLayout.getChildAt(i) as ImageView
            val layoutParams = indicator.layoutParams as LinearLayout.LayoutParams
            if (i == position) {
                indicator.apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.dot_selected
                        )
                    )
                    layoutParams.width = dpToPx(20)
                    layoutParams.height = dpToPx(8)
                }
            } else {
                indicator.apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.dot_unselected_bg
                        )
                    )
                    layoutParams.width = dpToPx(8)
                    layoutParams.height = dpToPx(8)
                }
            }
            indicator.layoutParams = layoutParams
        }
        binding.indicatorsLayout.requestLayout()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }
}