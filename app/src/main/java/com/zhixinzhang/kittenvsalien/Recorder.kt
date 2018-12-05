package com.zhixinzhang.kittenvsalien

import android.media.MediaRecorder

class Recorder(){
    var mRecorder: MediaRecorder? = null

    fun startRecorder() {
        if (mRecorder == null) {
            mRecorder = MediaRecorder()
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mRecorder!!.setOutputFile("/dev/null")
            mRecorder!!.prepare()
            mRecorder!!.start()
        }
    }

    fun stopRecorder() {
        if (mRecorder != null) {
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
        }
    }

    fun getAmplitude(): Double {
        return if (mRecorder != null)
            mRecorder!!.maxAmplitude.toDouble()
        else
            0.0

    }
}