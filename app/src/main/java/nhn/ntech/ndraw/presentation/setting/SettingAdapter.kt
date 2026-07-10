package nhn.ntech.ndraw.presentation.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nhn.ntech.ndraw.databinding.SettingItemBinding

class SettingAdapter(
    private val items: List<SettingItem>,
    private val onItemClick: (SettingItem) -> Unit
) : RecyclerView.Adapter<SettingAdapter.SettingViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingAdapter.SettingViewHolder {
        val binding = SettingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingAdapter.SettingViewHolder, position: Int) {
        val item = items[position]
        holder.binding.ivIconStart.setImageResource(item.iconStart)
        holder.binding.tvTitle.text = item.title
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class SettingViewHolder(val binding: SettingItemBinding) : RecyclerView.ViewHolder(binding.root)
}