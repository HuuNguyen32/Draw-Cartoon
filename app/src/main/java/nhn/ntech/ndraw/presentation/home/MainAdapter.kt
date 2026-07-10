package nhn.ntech.ndraw.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
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

    class MainViewHolder(val binding: TrendingItemBinding) : RecyclerView.ViewHolder(binding.root)
}