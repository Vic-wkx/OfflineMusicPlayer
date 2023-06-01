package com.my.music.repository

data class Music(
    var title: String = "UNKNOWN",
    var path: String = "UNKNOWN",
    var isSelected: Boolean = false
)