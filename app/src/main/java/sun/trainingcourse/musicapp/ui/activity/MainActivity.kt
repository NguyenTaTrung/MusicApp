package sun.trainingcourse.musicapp.ui.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import sun.trainingcourse.musicapp.R
import sun.trainingcourse.musicapp.data.local.MusicData
import sun.trainingcourse.musicapp.data.model.Music
import sun.trainingcourse.musicapp.service.MusicControlService
import sun.trainingcourse.musicapp.service.MusicServiceCallBack
import sun.trainingcourse.musicapp.ui.adapter.MusicAdapter
import sun.trainingcourse.musicapp.ui.fragment.MusicControlFragment

class MainActivity : AppCompatActivity(), MusicControlFragment.MusicControlFragmentCallBack,
    MusicServiceCallBack {

    private val REQUEST_CODE_PERMISSION = 1
    private var listMusic: ArrayList<Music> = ArrayList()
    private lateinit var musicControlService: MusicControlService
    private var isConnect = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnect = false
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder: MusicControlService.MyBinder = p1 as MusicControlService.MyBinder
            musicControlService = binder.getMusicControl()
            isConnect = true
            musicControlService.setServiceCallBack(this@MainActivity)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readRequest()
        Intent(this, MusicControlService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun readRequest() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION
            )
        } else {
            initView()
        }
    }

    private fun initView() {
        val data = MusicData(this)
        listMusic.addAll(data.getMusicFromStorage())

        recyclerViewMusic.layoutManager = LinearLayoutManager(this)
        recyclerViewMusic.adapter = MusicAdapter().apply {
            onItemClick = { item, position ->
                startService(getMusicIntent(this@MainActivity, item, position))

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentMusicControl, MusicControlFragment.newInstance(item))
                    .commit()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        if (isConnect) {
            musicControlService.setServiceCallBack(null)
            unbindService(serviceConnection)
            isConnect = false
        }
        super.onDestroy()
    }

    override fun resumeMusic() {
        musicControlService.resumeMusic()
    }

    override fun pauseMusic() {
        musicControlService.pauseMusic()
    }

    override fun skipMusic() {
        musicControlService.skipMusic()
    }

    override fun previousMusic() {
        musicControlService.previousMusic()
    }

    override fun seekToPosition(position: Int) {
        musicControlService.updateSeekBar(position)
    }

    override fun getDuration(): Int? = musicControlService.getDuration()

    override fun getCurrentPosition(): Int? = musicControlService.getCurrentPosition()

    override fun replaceFragment(music: Music) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentMusicControl, MusicControlFragment.newInstance(music))
            .commit()
    }

    companion object {
        const val EXTRA_MUSIC_POSITION = "EXTRA_MUSIC_POSITION"
        const val EXTRA_MUSIC = "EXTRA_MUSIC"

        fun getMusicIntent(context: Context, item: Music, position: Int): Intent {
            val intent = Intent(context, MusicControlService::class.java)
            intent.putExtra(EXTRA_MUSIC, item)
            intent.putExtra(EXTRA_MUSIC_POSITION, position)
            return intent
        }
    }
}
