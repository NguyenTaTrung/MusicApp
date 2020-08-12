package sun.trainingcourse.musicapp.service

import sun.trainingcourse.musicapp.data.model.Music

interface MusicServiceCallBack {
    fun replaceFragment(music: Music)
}