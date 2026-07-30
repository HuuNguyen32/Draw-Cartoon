package nhn.ntech.ndraw.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object MediaUtils {

    private var lastShareTime: Long = 0L
    private const val SHARE_DEBOUNCE_MS = 1000L

    /**
     * Save a file (image or video) to the device's public gallery.
     * - API 29+: Uses MediaStore + ContentResolver.
     * - API < 29: Copies file to public directory and scans with MediaScannerConnection.
     *
     * @return true if saved successfully, false otherwise.
     */
    fun saveToGallery(context: Context, file: File, isPhoto: Boolean): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveWithMediaStore(context, file, isPhoto)
            } else {
                saveWithFileCopy(context, file, isPhoto)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveWithMediaStore(context: Context, file: File, isPhoto: Boolean): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(
                MediaStore.MediaColumns.MIME_TYPE,
                if (isPhoto) getMimeTypeForImage(file) else getMimeTypeForVideo(file)
            )
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                if (isPhoto) Environment.DIRECTORY_PICTURES + "/NDraw"
                else Environment.DIRECTORY_MOVIES + "/NDraw"
            )
        }

        val collection = if (isPhoto) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(collection, contentValues) ?: return false

        resolver.openOutputStream(uri)?.use { outputStream ->
            FileInputStream(file).use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        } ?: return false

        return true
    }

    @Suppress("DEPRECATION")
    private fun saveWithFileCopy(context: Context, file: File, isPhoto: Boolean): Boolean {
        val publicDir = if (isPhoto) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        }

        val ndrawDir = File(publicDir, "NDraw")
        if (!ndrawDir.exists()) ndrawDir.mkdirs()

        val destFile = File(ndrawDir, file.name)

        FileInputStream(file).use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }

        // Scan file so it appears in gallery
        MediaScannerConnection.scanFile(
            context,
            arrayOf(destFile.absolutePath),
            arrayOf(if (isPhoto) getMimeTypeForImage(file) else getMimeTypeForVideo(file)),
            null
        )

        return true
    }

    /**
     * Share a single file via Intent.ACTION_SEND.
     */
    fun shareFile(context: Context, file: File, isPhoto: Boolean) {
        val now = System.currentTimeMillis()
        if (now - lastShareTime < SHARE_DEBOUNCE_MS) return
        lastShareTime = now

        val uri = getFileProviderUri(context, file) ?: return
        val mimeType = if (isPhoto) getMimeTypeForImage(file) else getMimeTypeForVideo(file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, null))
    }

    /**
     * Share multiple files via Intent.ACTION_SEND_MULTIPLE.
     */
    fun shareFiles(context: Context, files: List<File>, isPhoto: Boolean) {
        if (files.isEmpty()) return

        val now = System.currentTimeMillis()
        if (now - lastShareTime < SHARE_DEBOUNCE_MS) return
        lastShareTime = now

        if (files.size == 1) {
            shareFile(context, files.first(), isPhoto)
            return
        }

        val uris = ArrayList<Uri>()
        for (file in files) {
            val uri = getFileProviderUri(context, file) ?: continue
            uris.add(uri)
        }

        if (uris.isEmpty()) return

        val mimeType = if (isPhoto) "image/*" else "video/*"

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = mimeType
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, null))
    }

    private fun getFileProviderUri(context: Context, file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getMimeTypeForImage(file: File): String {
        return when (file.extension.lowercase()) {
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> "image/jpeg"
        }
    }

    private fun getMimeTypeForVideo(file: File): String {
        return when (file.extension.lowercase()) {
            "webm" -> "video/webm"
            "3gp" -> "video/3gpp"
            "mkv" -> "video/x-matroska"
            else -> "video/mp4"
        }
    }
}
