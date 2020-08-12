package sun.trainingcourse.musicapp.data.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import sun.trainingcourse.musicapp.data.model.Music

private const val TAG = "MusicData"

class MusicData(private val context: Context) {

    fun getMusicFromStorage(): List<Music> {
        val mListMusic: ArrayList<Music> = ArrayList()
        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)
        when {
            cursor == null -> Log.d(TAG, "Query fails")
            !cursor.moveToFirst() -> Log.d(TAG, "No media on device")
            else -> {
                val dataColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                do {
                    val data = cursor.getString(dataColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)

                    mListMusic.add(Music(data, title, artist))
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return mListMusic
    }
}
