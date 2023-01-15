package zlc.season.downloadx.core

import zlc.season.downloadx.utils.getMd5


open class DownloadParam(
    var url: String,
    var savePath: String,
    var addTime: Long,
    var saveName: String = "",
    var type: Int = 0,
    var videoName: String,
    var videoThumb: String,
    var subName: String,
    var subIndex: Int,
) {

    /**
     * Each task with unique tag.
     */
    open fun tag() = url.getMd5()


    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true

        return if (other is DownloadParam) {
            tag() == other.tag()
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return tag().hashCode()
    }
}