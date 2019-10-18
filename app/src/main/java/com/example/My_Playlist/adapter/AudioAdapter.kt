package com.example.My_Playlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.My_Playlist.R
import com.example.My_Playlist.model.Audiotracks

class AudioAdapter(private val audioadapterDelegate: AudioAdapterDelegate)
    : ListAdapter<Audiotracks, AudioAdapter.AudioViewHolder>(AudioDiffUtil()){
    interface  AudioAdapterDelegate{
        fun audioSelect(person: Audiotracks)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_item_view_layout, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.apply {
            audioNametextiew.text = getItem(position).name
            audioTypetextiew.text = getItem(position).type
            viewGroup?.context?.let {
                Glide.with(it) //1
                    .load(getItem(position).url)
                    .placeholder(R.drawable.ic_library_music_black_24dp)
                    .error(R.drawable.ic_library_music_black_24dp)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(audioItemimageview)
            }

            viewGroup.setOnClickListener{
                audioadapterDelegate.audioSelect(getItem(position))
            }
        }
    }
    class AudioViewHolder(view: View): RecyclerView.ViewHolder(view){
        val audioNametextiew: TextView = view.findViewById(R.id.audioName_textiew)
        val audioTypetextiew: TextView = view.findViewById(R.id.audioType_textiew)
        val audioItemimageview: ImageView = view.findViewById(R.id.audioItem_imageview)
        val viewGroup: ConstraintLayout = view.findViewById(R.id.audio_itemview)

    }
}

class AudioDiffUtil : DiffUtil.ItemCallback<Audiotracks>(){
    override fun areItemsTheSame(oldItem: Audiotracks, newItem: Audiotracks): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Audiotracks, newItem: Audiotracks): Boolean {
        return oldItem.name == newItem.name && oldItem.type == newItem.type
    }
}