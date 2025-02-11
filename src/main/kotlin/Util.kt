package dev.edwinchang

operator fun <T> List<T>.get(index: UInt) = get(index.toInt())

operator fun <T> MutableList<T>.set(index: UInt, element: T) = set(index.toInt(), element)
