package sun.trainingcourse.musicapp.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_music_control.*

import sun.trainingcourse.musicapp.R
import sun.trainingcourse.musicapp.data.model.Music
import java.text.SimpleDateFormat

private const val BUNDLE_MUSIC = "BUNDLE_MUSIC"

class MusicControlFragment : Fragment() {

    private var currentPosition = 0
    private var duration = 0
    private var isState: Boolean = false
    private var music: Music? = null
    private var callBack: MusicControlFragmentCallBack? = null

    private var seekBarChange = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

        override fun onStartTrackingTouch(p0: SeekBar?) {}

        override fun onStopTrackingTouch(p0: SeekBar?) {
            currentPosition = p0!!.progress
            callBack?.seekToPosition(currentPosition)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callBack = context as MusicControlFragmentCallBack
        } catch (e: ClassCastException) {
            throw ClassCastException(
                "$context must implement FragmentListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            music = it.getParcelable(BUNDLE_MUSIC)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initAction()
        updateSeekBar()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initData() {
        duration = callBack?.getDuration()!!
        currentPosition = callBack?.getCurrentPosition()!!
        seekBar.max = duration

        textAuthor.text = music?.author
        textSongName.text = music?.song
        textDuration.text = SimpleDateFormat("m:ss").format(duration)
    }

    private fun initAction() {
        imageButtonPlayAndPause.setOnClickListener { controlPlayAndPause() }
        imageButtonSkip.setOnClickListener { callBack?.skipMusic() }
        imageButtonPrevious.setOnClickListener { callBack?.previousMusic() }
        seekBar.setOnSeekBarChangeListener(seekBarChange)
    }

    private fun controlPlayAndPause() {
        isState = if (!isState) {
            imageButtonPlayAndPause.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp)
            callBack?.pauseMusic()
            true
        } else {
            imageButtonPlayAndPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp)
            callBack?.resumeMusic()
            false
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateSeekBar() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (!isState) {
                    if (currentPosition < duration) {
                        currentPosition += 1000
                        if (textCurrentPosition != null)
                            textCurrentPosition.text =
                                SimpleDateFormat("m:ss").format(currentPosition)
                        if (seekBar != null) seekBar.progress = currentPosition
                        handler.postDelayed(this, 1000)
                    } else {
                        callBack?.skipMusic()
                    }
                }
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    interface MusicControlFragmentCallBack {
        fun resumeMusic()
        fun pauseMusic()
        fun skipMusic()
        fun previousMusic()
        fun seekToPosition(position: Int)

        fun getDuration(): Int?
        fun getCurrentPosition(): Int?
    }

    companion object {
        fun newInstance(music: Music) =
            MusicControlFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_MUSIC, music)
                }
            }
    }
}
