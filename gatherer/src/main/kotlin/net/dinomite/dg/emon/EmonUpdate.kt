package net.dinomite.dg.emon

data class EmonUpdate(val node: String, val updates: Map<String, String>)

