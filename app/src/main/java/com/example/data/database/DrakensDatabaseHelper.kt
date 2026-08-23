package com.example.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLiteOpenHelper nativo para "Draken's Library".
 * Proporciona persistencia 100% offline sin servidores externos.
 */
class DrakensDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "drakens_native_library.db"
        const val DATABASE_VERSION = 1

        // Tabla: Libros
        const val TABLE_LIBROS = "libros"
        const val COL_LIBRO_ID = "id"
        const val COL_LIBRO_TITULO = "titulo"
        const val COL_LIBRO_AUTOR = "autor"
        const val COL_LIBRO_CATEGORIA = "categoria" // Filosofía, Ciencia, Novela, Poesía, Teatro
        const val COL_LIBRO_PORTADA = "ruta_portada"
        const val COL_LIBRO_CALIFICACION = "calificacion" // 1 a 5 estrellas
        const val COL_LIBRO_PDF = "ruta_pdf"
        const val COL_LIBRO_SINOPSIS = "sinopsis"
        const val COL_LIBRO_TOTAL_PAGINAS = "total_paginas"
        const val COL_LIBRO_PAGINA_ACTUAL = "pagina_actual"
        const val COL_LIBRO_TERMINADO = "terminado"
        const val COL_LIBRO_FECHA = "fecha_registro"

        // Tabla: Anotaciones
        const val TABLE_ANOTACIONES = "anotaciones"
        const val COL_NOTA_ID = "id"
        const val COL_NOTA_LIBRO_ID = "libro_id"
        const val COL_NOTA_PAGINA = "numero_pagina"
        const val COL_NOTA_CITA = "cita_texto"
        const val COL_NOTA_COMENTARIO = "comentario_personal"
        const val COL_NOTA_COLOR = "color_subrayado"
        const val COL_NOTA_FECHA = "fecha_registro"

        // Tabla: Salón de los Ídolos
        const val TABLE_IDOLOS = "salon_idolos"
        const val COL_IDOLO_ID = "id"
        const val COL_IDOLO_NOMBRE = "nombre"
        const val COL_IDOLO_FOTO = "ruta_foto"
        const val COL_IDOLO_BIOGRAFIA = "biografia"
        const val COL_IDOLO_PORQUE_ENCANTO = "porque_me_encanto"
        const val COL_IDOLO_EPOCA = "epoca"
        const val COL_IDOLO_CORRIENTE = "corriente"
        const val COL_IDOLO_FRASE = "frase_celebre"
        const val COL_IDOLO_OBRAS = "obras_principales"
        const val COL_IDOLO_FECHA = "fecha_registro"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Crear Tabla de Libros
        val createLibrosTable = """
            CREATE TABLE $TABLE_LIBROS (
                $COL_LIBRO_ID TEXT PRIMARY KEY,
                $COL_LIBRO_TITULO TEXT NOT NULL,
                $COL_LIBRO_AUTOR TEXT NOT NULL,
                $COL_LIBRO_CATEGORIA TEXT NOT NULL,
                $COL_LIBRO_PORTADA TEXT,
                $COL_LIBRO_CALIFICACION INTEGER DEFAULT 5,
                $COL_LIBRO_PDF TEXT,
                $COL_LIBRO_SINOPSIS TEXT,
                $COL_LIBRO_TOTAL_PAGINAS INTEGER DEFAULT 100,
                $COL_LIBRO_PAGINA_ACTUAL INTEGER DEFAULT 1,
                $COL_LIBRO_TERMINADO INTEGER DEFAULT 0,
                $COL_LIBRO_FECHA INTEGER NOT NULL
            );
        """.trimIndent()

        // 2. Crear Tabla de Anotaciones
        val createAnotacionesTable = """
            CREATE TABLE $TABLE_ANOTACIONES (
                $COL_NOTA_ID TEXT PRIMARY KEY,
                $COL_NOTA_LIBRO_ID TEXT NOT NULL,
                $COL_NOTA_PAGINA INTEGER NOT NULL,
                $COL_NOTA_CITA TEXT NOT NULL,
                $COL_NOTA_COMENTARIO TEXT,
                $COL_NOTA_COLOR TEXT DEFAULT '#BB86FC',
                $COL_NOTA_FECHA INTEGER NOT NULL,
                FOREIGN KEY($COL_NOTA_LIBRO_ID) REFERENCES $TABLE_LIBROS($COL_LIBRO_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        // 3. Crear Tabla del Salón de los Ídolos
        val createIdolosTable = """
            CREATE TABLE $TABLE_IDOLOS (
                $COL_IDOLO_ID TEXT PRIMARY KEY,
                $COL_IDOLO_NOMBRE TEXT NOT NULL,
                $COL_IDOLO_FOTO TEXT,
                $COL_IDOLO_BIOGRAFIA TEXT NOT NULL,
                $COL_IDOLO_PORQUE_ENCANTO TEXT NOT NULL,
                $COL_IDOLO_EPOCA TEXT,
                $COL_IDOLO_CORRIENTE TEXT,
                $COL_IDOLO_FRASE TEXT,
                $COL_IDOLO_OBRAS TEXT,
                $COL_IDOLO_FECHA INTEGER NOT NULL
            );
        """.trimIndent()

        db.execSQL(createLibrosTable)
        db.execSQL(createAnotacionesTable)
        db.execSQL(createIdolosTable)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANOTACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LIBROS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_IDOLOS")
        onCreate(db)
    }
}
