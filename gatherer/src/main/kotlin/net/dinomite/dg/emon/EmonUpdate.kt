package net.dinomite.dg.emon

data class EmonUpdate(val node: EmonNode, val updates: Map<String, String>)

