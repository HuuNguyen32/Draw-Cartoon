package nhn.ntech.ndraw.presentation.Intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nhn.ntech.ndraw.databinding.IntroItemBinding

class IntroAdapter(
    private var listIntros: List<Intro>
) : RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): IntroAdapter.IntroViewHolder {
        val binding = IntroItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntroAdapter.IntroViewHolder, position: Int) {
        val intro = listIntros[position]
        holder.bind(intro)
    }

    override fun getItemCount(): Int = listIntros.size

    fun setListIntros(intros: List<Intro>) {
        listIntros = intros
        notifyDataSetChanged()
    }

    class IntroViewHolder(private val binding: IntroItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(intro: Intro) {
            with(binding) {
                ivIntro.setImageResource(intro.imgRes)
                tvIntro.text = intro.title
            }
        }
    }
}