package zlc.season.downloadx

import zlc.season.downloadx.database.*

open class State(val status: Int) {
    var progress: Progress = Progress()
        internal set

    class None : State(STATUS_NONE)
    class Waiting : State(STATUS_WAITING)
    class Downloading : State(STATUS_DOWNLOADING)
    class Paused : State(STATUS_PAUSED)
    class Succeed : State(STATUS_SUCCEED)
    class Failed : State(STATUS_FAILED)
}