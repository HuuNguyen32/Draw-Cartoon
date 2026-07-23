package nhn.ntech.ndraw.presentation.home

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nhn.ntech.ndraw.databinding.TrendingItemBinding
import androidx.core.net.toUri
import nhn.ntech.ndraw.consts.Const
import nhn.ntech.ndraw.utils.TransferUtils

class MainAdapter(
    private var items: List<String>,
    private val onItemClick: (Uri) -> Unit,
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    companion object {
        private const val HEIGHT_SMALL_DP = 164
        private const val HEIGHT_MEDIUM_DP = 258
        private const val HEIGHT_LARGE_DP = 302
    }

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

        val heightPx = when (position % 3) {
            0 -> {
                val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
                screenWidth / 2 - TransferUtils.dpToPx(holder.itemView.context, 16)
            }
            else -> TransferUtils.dpToPx(holder.itemView.context, HEIGHT_MEDIUM_DP)
        }

        if (holder.binding.ivTrendItem.layoutParams.height != heightPx) {
            holder.binding.ivTrendItem.layoutParams.height = heightPx
        }

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

    class MainViewHolder(val binding: TrendingItemBinding) : RecyclerView.ViewHolder(binding.root)
}
