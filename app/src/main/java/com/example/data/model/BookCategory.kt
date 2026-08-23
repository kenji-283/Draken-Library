package com.example.data.model

enum class BookCategory(val displayName: String) {
    FILOSOFIA("Filosofía"),
    CIENCIA("Ciencia"),
    NOVELA("Novela"),
    POESIA("Poesía"),
    TEATRO("Teatro");

    companion object {
        val ALL_CATEGORIES = listOf(
            FILOSOFIA.displayName,
            CIENCIA.displayName,
            NOVELA.displayName,
            POESIA.displayName,
            TEATRO.displayName
        )

        fun fromString(value: String): BookCategory {
            return entries.firstOrNull {
                it.displayName.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true)
            } ?: FILOSOFIA
        }
    }
}
