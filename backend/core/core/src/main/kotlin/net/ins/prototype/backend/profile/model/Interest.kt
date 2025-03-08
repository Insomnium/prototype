package net.ins.prototype.backend.profile.model

enum class Interest(val code: Int) {
    DATING(1 shl 0),
    SEXTING(1 shl 2),
    RELATIONSHIPS(1 shl 3);

    companion object {

        fun unmask(code: Int): Set<Interest> {
            return entries.filter { code and it.code == it.code }.toSet()
        }
    }
}

fun Iterable<Interest>.calculateMask() = fold(0) { acc, interest -> acc or interest.code }
