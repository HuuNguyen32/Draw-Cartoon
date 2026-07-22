package nhn.ntech.ndraw.consts

import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.presentation.language.Language

object Const {
    const val LANGUAGE_TEST = true

    const val FLOW_TAG = "flow_code"
    const val FLOW_SPLASH_CODE = 0
    const val FLOW_SETTING_CODE = 1

    const val FILE_TAG = "file_path"
    const val IS_PHOTO_TAG = "is_photo"

    const val IMAGE_URI_TAG = "image_uri"
    const val URL_ASSETS = "file:///android_asset/"
    const val FOLDER_ASSETS = "ardrawing2"

    const val MY_WORKS_FOLDER = "my_works"

    fun getAssetsPath(subFolder: String): String = "$FOLDER_ASSETS/$subFolder"

    const val VIDEO_INSTRUCTION_URL =
        "https://lvtglobal.site/public/app/ardrawing2/intro/ar2/tutorial_trace.mp4"

    const val DEFAULT_TIME = "00:00"
}