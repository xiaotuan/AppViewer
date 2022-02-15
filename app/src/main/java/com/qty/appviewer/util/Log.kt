package com.qty.appviewer.util

class Log {

    companion object {
        private const val TAG = "AppViewer"

        private const val VERBOSE = true
        private const val DEBUG = true
        private const val INFO = true
        private const val WARN = true
        private const val ERROR = true

        fun v(prefix: Any?, msg: String): Int {
            return if (VERBOSE) {
                android.util.Log.v(TAG, "[${getPrefix(prefix)}]${msg}")
            } else {
                0
            }
        }

        fun v(prefix: Any?, msg: String, tr: Throwable): Int {
            return if (VERBOSE) {
                android.util.Log.v(TAG, "[${getPrefix(prefix)}]${msg}", tr)
            } else {
                0
            }
        }

        fun d(prefix: Any?, msg: String): Int {
            return if (DEBUG) {
                android.util.Log.d(TAG, "[${getPrefix(prefix)}]${msg}")
            } else {
                0
            }
        }

        fun d(prefix: Any?, msg: String, tr: Throwable): Int {
            return if (DEBUG) {
                android.util.Log.d(TAG, "[${getPrefix(prefix)}]${msg}", tr)
            } else {
                0
            }
        }

        fun i(prefix: Any?, msg: String): Int {
            return if (INFO) {
                android.util.Log.i(TAG, "[${getPrefix(prefix)}]${msg}")
            } else {
                0
            }
        }

        fun i(prefix: Any?, msg: String, tr: Throwable): Int {
            return if (INFO) {
                android.util.Log.i(TAG, "[${getPrefix(prefix)}]${msg}", tr)
            } else {
                0
            }
        }

        fun w(prefix: Any?, msg: String): Int {
            return if (WARN) {
                android.util.Log.w(TAG, "[${getPrefix(prefix)}]${msg}")
            } else {
                0
            }
        }

        fun w(prefix: Any?, msg: String, tr: Throwable): Int {
            return if (WARN) {
                android.util.Log.w(TAG, "[${getPrefix(prefix)}]${msg}", tr)
            } else {
                0
            }
        }

        fun e(prefix: Any?, msg: String): Int {
            return if (ERROR) {
                android.util.Log.e(TAG, "[${getPrefix(prefix)}]${msg}")
            } else {
                0
            }
        }

        fun e(prefix: Any?, msg: String, tr: Throwable): Int {
            return if (ERROR) {
                android.util.Log.e(TAG, "[${getPrefix(prefix)}]${msg}", tr)
            } else {
                0
            }
        }

        private fun getPrefix(prefix: Any?): String {
            return if (prefix != null) {
                if (prefix is String) {
                    prefix as String
                } else {
                    prefix::class.java.simpleName.toString()
                }
            } else {
                ""
            }
        }
    }

}