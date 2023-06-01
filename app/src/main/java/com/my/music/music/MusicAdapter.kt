package com.my.music.music

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.my.music.R
import com.my.music.databinding.ItemMusicBinding
import com.my.music.player.PlayerActivity
import com.my.music.repository.MusicRepository
import com.my.music.utils.LogUtils

class MusicAdapter(private val musicViewModel: MusicViewModel) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemMusicBinding>(LayoutInflater.from(context), R.layout.item_music, parent, false)
        return MusicViewHolder(binding)
    }

    override fun getItemCount(): Int = MusicRepository.musicList.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        LogUtils.d("music: ${MusicRepository.musicList[holder.absoluteAdapterPosition]}, position = [${position}], bindingAdapterPosition = [${holder.bindingAdapterPosition}], absoluteAdapterPosition = [${holder.absoluteAdapterPosition}], layoutPosition = [${holder.layoutPosition}], oldPosition = [${holder.oldPosition}]")
        holder.binding.music = MusicRepository.musicList[holder.bindingAdapterPosition]
        holder.binding.root.setOnClickListener {
            musicViewModel.musicBinder?.startPlay(holder.bindingAdapterPosition)
            PlayerActivity.start(context)
        }
    }

    class MusicViewHolder(val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root)
}
