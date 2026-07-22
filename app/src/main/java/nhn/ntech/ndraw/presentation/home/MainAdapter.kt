package nhn.ntech.ndraw.presentation.home

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nhn.ntech.ndraw.databinding.TrendingItemBinding
import androidx.core.net.toUri
import nhn.ntech.ndraw.consts.Const

class MainAdapter(
    private var items: List<String>,
    private val onItemClick: (Uri) -> Unit,
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MainAdapter.MainViewHolder {
        val binding =
            TrendingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainAdapter.MainViewHolder, position: Int) {
        val item = items[position]
        val path = "${Const.URL_ASSETS}${item}".toUri()
        Log.d("MainAdapter", "onBindViewHolder: $path")
        Glide.with(holder.itemView.context)
            .load(path)
            .into(holder.binding.ivTrendItem)
        holder.binding.root.setOnClickListener {
            onItemClick(path)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setDimensionRatio(view: ImageView, ratio: String) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = ratio
        view.layoutParams = layoutParams
    }

    class MainViewHolder(val binding: TrendingItemBinding) : RecyclerView.ViewHolder(binding.root)
}