package nhn.ntech.ndraw.presentation.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.CategoryItemBinding

class CategoryAdapter(
    private var categories: List<String>,
    private val onItemClick: (String) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoryAdapter.CategoryViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryAdapter.CategoryViewHolder,
        position: Int,
    ) {
        val category = categories[position]
        holder.binding.tvCategory.text = category
        if (selectedPosition == position) {
            holder.binding.tvCategory.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.category_selected
                )
            )
            holder.binding.root.setBackgroundResource(R.drawable.category_selected_bg)
        } else {
            holder.binding.tvCategory.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.category_unselected
                )
            )
            holder.binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
        }
        holder.binding.root.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                val cate = categories[selectedPosition]
                onItemClick(cate)
                if (previousSelectedPosition != RecyclerView.NO_POSITION)
                    notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newListData: List<String>) {
        categories = newListData
        notifyItemRangeChanged(0, itemCount)
    }

    fun setSelectedPosition(position: Int) {
        val previous = selectedPosition
        selectedPosition = position
        if (selectedPosition != RecyclerView.NO_POSITION) {
            if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
        }
    }

    class CategoryViewHolder(val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}