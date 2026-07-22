package nhn.ntech.ndraw.helper

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class ExoPlayerHelper(private val context: Context) {
    private var player: ExoPlayer? = null

    fun initPlayer(playerView: PlayerView) {
        player = ExoPlayer.Builder(context).build()
        playerView.useController = false
        playerView.player = player
    }

    fun setMedia(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun release() {
        player?.release()
        player = null
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    fun getDuration(): Long {
        return player?.duration ?: 0
    }

    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }

    fun getPLayer(): ExoPlayer? = player
}