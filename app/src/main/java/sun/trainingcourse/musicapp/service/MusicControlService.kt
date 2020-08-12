package sun.trainingcourse.musicapp.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import sun.trainingcourse.musicapp.data.local.MusicData
import sun.trainingcourse.musicapp.data.model.Music
import sun.trainingcourse.musicapp.ui.activity.MainActivity

class MusicControlService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var music: Music? = null
    private var position: Int = 0
    private var serviceCallBack: MusicServiceCallBack? = null
    private val listMusic = mutableListOf<Music>()

    override fun onCreate() {
        super.onCreate()
        val data = MusicData(this)
        listMusic.addAll(data.getMusicFromStorage())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initMusicData(intent)
        startMusic()
        return START_NOT_STICKY
    }

    private fun initMusicData(intent: Intent?) {
        intent?.run {
            music = getParcelableExtra(MainActivity.EXTRA_MUSIC)
            position = getIntExtra(MainActivity.EXTRA_MUSIC_POSITION, 0)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = MyBinder()

    private fun createMusic(path: String?) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    private fun startMusic() {
        if (mediaPlayer != null) {
            stopMusic()
        }
        createMusic(music?.path)
    }

    fun resumeMusic() {
        mediaPlayer?.let { if(!it.isPlaying) it.start() }
    }

    fun pauseMusic() {
        mediaPlayer?.let { if(it.isPlaying) it.pause() }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun skipMusic() {
        position++
        if (position > listMusic.size - 1) position = 0
        stopMusic()
        createMusic(listMusic[position].path)
        if (serviceCallBack != null) serviceCallBack?.replaceFragment(listMusic[position])
    }

    fun previousMusic() {
        position--
        if (position < 0) position = listMusic.size - 1
        stopMusic()
        createMusic(listMusic[position].path)
        if (serviceCallBack != null) serviceCallBack?.replaceFragment(listMusic[position])
    }

    fun getDuration() = mediaPlayer?.duration

    fun getCurrentPosition() = mediaPlayer?.currentPosition

    fun updateSeekBar(position: Int) = mediaPlayer?.seekTo(position)

    fun setServiceCallBack(musicServiceCallBack: MusicServiceCallBack?) {
        serviceCallBack = musicServiceCallBack
    }

    inner class MyBinder : Binder() {
        fun getMusicControl(): MusicControlService = this@MusicControlService
    }
}
