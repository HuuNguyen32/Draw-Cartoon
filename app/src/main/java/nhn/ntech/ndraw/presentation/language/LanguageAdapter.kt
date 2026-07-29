package nhn.ntech.ndraw.presentation.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.CardLanguageBinding

class LanguageAdapter(
    private var languageList: List<Language>,
    private val onLanguageSelected: (Language, Boolean) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = CardLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val item = languageList[position]
        val code = item.code
        val language = item.name
        val flag = item.flagRes

        with(holder.binding) {
            ivFlag.setImageResource(flag)
            tvLanguage.text = language

            root.isSelected = (selectedPosition == position)

            // cập nhật trạng thái hiển thị
            if (position == selectedPosition) {
                tvLanguage.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                rbLanguage.setImageResource(R.drawable.ic_select)
            } else {
                tvLanguage.setTextColor(ContextCompat.getColor(root.context, R.color.gray))
                rbLanguage.setImageResource(R.drawable.ic_no_select)
            }
        }

        holder.binding.root.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = holder.adapterPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                val languageTrans = languageList[selectedPosition]
                onLanguageSelected(languageTrans, true)
                if (previous != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previous)
                }
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int = languageList.size

    fun updateData(newLanguageList: List<Language>) {
        languageList = newLanguageList
        notifyDataSetChanged()
    }

    fun setSelectedPosition(position: Int) {
        val previous = selectedPosition
        selectedPosition = position
        if (selectedPosition != RecyclerView.NO_POSITION) {
            if (previous != RecyclerView.NO_POSITION) notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
        }
    }

    class LanguageViewHolder(val binding: CardLanguageBinding) : RecyclerView.ViewHolder(binding.root)
}