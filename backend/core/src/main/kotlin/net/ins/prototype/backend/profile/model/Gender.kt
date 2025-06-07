package net.ins.prototype.backend.profile.model

enum class Gender(val code: Char) {
    MALE('M'),
    FEMALE('F')
    ;

    companion object {
        private val vals: Array<Gender> = Gender.values()

        fun byCode(code: Char): Gender = vals.find { it.code == code } ?: throw IllegalArgumentException("Unknown Gender code: $code")
    }
}
