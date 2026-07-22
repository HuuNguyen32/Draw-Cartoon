package nhn.ntech.ndraw.presentation.work

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nhn.ntech.ndraw.databinding.MyWorkPhotoItemBinding
import java.io.File

class MyWorkAdapter(
    private var items: List<File>,
    private val onItemClick: (File) -> Unit,
    private val onMoreClick: (File, View) -> Unit,
    private val onSelectionModeChange: (Boolean) -> Unit,
    private val onSelectionChange: (Int) -> Unit
) : RecyclerView.Adapter<MyWorkAdapter.MyWorkViewHolder>() {

    var isSelectionMode = false
        private set
    private val selectedItems = mutableSetOf<File>()

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].absolutePath.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWorkViewHolder {
        val binding =
            MyWorkPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyWorkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyWorkViewHolder, position: Int) {
        val file = items[position]
        Glide.with(holder.itemView.context)
            .load(file)
            .into(holder.binding.ivMyWork)

        if (isSelectionMode) {
            holder.binding.ivMore.visibility = View.GONE
            holder.binding.cbCheck.visibility = View.VISIBLE
            holder.binding.cbCheck.setOnCheckedChangeListener(null)
            holder.binding.cbCheck.isChecked = selectedItems.contains(file)
            holder.binding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(file)
                } else {
                    selectedItems.remove(file)
                }
                onSelectionChange(selectedItems.size)
            }
        } else {
            holder.binding.ivMore.visibility = View.VISIBLE
            holder.binding.cbCheck.visibility = View.GONE
        }

        holder.binding.ivMore.setOnClickListener {
            onMoreClick(file, holder.binding.ivMore)
        }

        holder.binding.root.setOnClickListener {
            if (isSelectionMode) {
                holder.binding.cbCheck.isChecked = !holder.binding.cbCheck.isChecked
            } else {
                onItemClick(file)
            }
        }

        holder.binding.root.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                selectedItems.add(file)
                onSelectionModeChange(true)
                onSelectionChange(selectedItems.size)
                notifyDataSetChanged()
                true
            } else {
                false
            }
        }
    }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedItems.clear()
        onSelectionModeChange(false)
        onSelectionChange(0)
        notifyDataSetChanged()
    }

    fun selectAll() {
        if (selectedItems.size == items.size) {
            selectedItems.clear()
        } else {
            selectedItems.addAll(items)
        }
        onSelectionChange(selectedItems.size)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<File> = selectedItems.toList()

    fun getItems(): List<File> = items

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<File>) {
        items = newItems
        selectedItems.retainAll(newItems.toSet())
        if (isSelectionMode) {
            onSelectionChange(selectedItems.size)
        }
        notifyDataSetChanged()
    }

    class MyWorkViewHolder(val binding: MyWorkPhotoItemBinding) : RecyclerView.ViewHolder(binding.root)
}
