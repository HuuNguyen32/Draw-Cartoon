package nhn.ntech.ndraw.ext

fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
}