package nhn.ntech.ndraw.presentation.home

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.TrendingItemBinding
import nhn.ntech.ndraw.utils.TransferUtils

class MainAdapter(
    private var items: List<String>,
    private val onItemClick: (String, Boolean) -> Unit,
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    companion object {
        private const val HEIGHT_SMALL_DP = 164
        private const val HEIGHT_MEDIUM_DP = 258
        private const val HEIGHT_LARGE_DP = 302
    }

    private var isPhotoError = false

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
            .load(item)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    isPhotoError = false
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    isPhotoError = true
                    return true
                }
            })
            .error(R.drawable.ic_image_error)
            .into(holder.binding.ivTrendItem)


        holder.binding.root.setOnClickListener {
            onItemClick(item, isPhotoError)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    class MainViewHolder(val binding: TrendingItemBinding) : RecyclerView.ViewHolder(binding.root)
}
