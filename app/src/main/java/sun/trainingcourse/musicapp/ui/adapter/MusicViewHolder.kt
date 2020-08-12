package sun.trainingcourse.musicapp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_music.view.*
import sun.trainingcourse.musicapp.data.model.Music

class MusicViewHolder(
    itemView: View,
    onItemClick: (Music, Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var itemData: Music? = null

    init {
        itemView.setOnClickListener {
            itemData?.let {
                onItemClick(it, adapterPosition)
            }
        }
    }

    fun bindData(music: Music) {
        itemData = music
        itemView.run {
            textAuthor.text = music.author
            textName.text = music.song
        }
    }
}
