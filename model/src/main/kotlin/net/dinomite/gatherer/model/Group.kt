package net.dinomite.gatherer.model

enum class Group {
    BATTERY,
    DEVICE,
    ENERGY,
    ENVIRONMENT;

    fun reportingValue() = this.name.toLowerCase()

    companion object {
        fun fromString(string: String) = values().firstOrNull { it.name.equals(string, true) }
    }
}
