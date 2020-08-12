package sun.trainingcourse.musicapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sun.trainingcourse.musicapp.R
import sun.trainingcourse.musicapp.data.model.Music

class MusicAdapter : RecyclerView.Adapter<MusicViewHolder>() {

    var onItemClick: (Music, Int) -> Unit = { _, _ -> }
    private val listMusic = mutableListOf<Music>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_music, parent, false)
        return MusicViewHolder(itemView, onItemClick)
    }

    override fun getItemCount(): Int = listMusic.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bindData(listMusic[position])
    }
}
