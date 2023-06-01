package com.my.music.utils

import android.content.Context
import android.content.SharedPreferences
import com.my.music.MusicApplication

class SPUtils {
    private var sp: SharedPreferences

    private constructor(spName: String) {
        sp = MusicApplication.application.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

    private constructor(spName: String, mode: Int) {
        sp = MusicApplication.application.getSharedPreferences(spName, mode)
    }


    fun put(key: String, value: String, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putString(key, value).commit()
        } else {
            sp.edit().putString(key, value).apply()
        }
    }


    fun getString(key: String): String {
        return getString(key, "")
    }


    fun getString(key: String, defaultValue: String): String {
        return sp.getString(key, defaultValue) ?: ""
    }


    fun put(key: String, value: Int, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putInt(key, value).commit()
        } else {
            sp.edit().putInt(key, value).apply()
        }
    }


    fun getInt(key: String): Int {
        return getInt(key, -1)
    }


    fun getInt(key: String, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    fun put(key: String, value: Long, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putLong(key, value).commit()
        } else {
            sp.edit().putLong(key, value).apply()
        }
    }


    fun getLong(key: String): Long {
        return getLong(key, -1L)
    }


    fun getLong(key: String, defaultValue: Long): Long {
        return sp.getLong(key, defaultValue)
    }

    fun put(key: String, value: Float, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putFloat(key, value).commit()
        } else {
            sp.edit().putFloat(key, value).apply()
        }
    }


    fun getFloat(key: String): Float {
        return getFloat(key, -1f)
    }


    fun getFloat(key: String, defaultValue: Float): Float {
        return sp.getFloat(key, defaultValue)
    }

    fun put(key: String, value: Boolean, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putBoolean(key, value).commit()
        } else {
            sp.edit().putBoolean(key, value).apply()
        }
    }


    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }


    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    fun put(
        key: String,
        value: Set<String>,
        isCommit: Boolean = false
    ) {
        if (isCommit) {
            sp.edit().putStringSet(key, value).commit()
        } else {
            sp.edit().putStringSet(key, value).apply()
        }
    }


    fun getStringSet(key: String): Set<String> {
        return getStringSet(key, emptySet<String>())
    }


    fun getStringSet(
        key: String,
        defaultValue: Set<String>
    ): Set<String> {
        return sp.getStringSet(key, defaultValue) ?: setOf()
    }


    val all: Map<String, *>
        get() = sp.all


    operator fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    fun remove(key: String, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().remove(key).commit()
        } else {
            sp.edit().remove(key).apply()
        }
    }

    fun clear(isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().clear().commit()
        } else {
            sp.edit().clear().apply()
        }
    }

    companion object {
        private val SP_UTILS_MAP: MutableMap<String, SPUtils> = HashMap()


        val INSTANCE: SPUtils
            get() = getInstance("", Context.MODE_PRIVATE)


        fun getInstance(mode: Int): SPUtils {
            return getInstance("", mode)
        }


        fun getInstance(spName: String): SPUtils {
            return getInstance(spName, Context.MODE_PRIVATE)
        }


        fun getInstance(spName: String, mode: Int): SPUtils {
            var spName = spName
            if (isSpace(spName)) spName = "spUtils"
            var spUtils = SP_UTILS_MAP[spName]
            if (spUtils == null) {
                synchronized(SPUtils::class.java) {
                    spUtils = SP_UTILS_MAP[spName]
                    if (spUtils == null) {
                        spUtils = SPUtils(spName, mode)
                        SP_UTILS_MAP[spName] = spUtils!!
                    }
                }
            }
            return spUtils!!
        }

        private fun isSpace(s: String): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}