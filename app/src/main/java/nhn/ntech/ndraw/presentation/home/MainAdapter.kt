package nhn.ntech.ndraw.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import nhn.ntech.ndraw.databinding.TrendingItemBinding

class MainAdapter(
    private var items: List<Int>,
    private val onItemClick: (Int) -> Unit,
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
        val index = position % 4
        when(index) {
            0 -> {
                setDimensionRatio(holder.binding.ivTrendItem, "164:174")
            }
            1 -> {
                setDimensionRatio(holder.binding.ivTrendItem, "164:258")
            }
            2 -> {
                setDimensionRatio(holder.binding.ivTrendItem, "164:302")
            }
            3 -> {
                setDimensionRatio(holder.binding.ivTrendItem, "164:302")
            }
        }
        holder.binding.ivTrendItem.setImageResource(item)
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Int>) {
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