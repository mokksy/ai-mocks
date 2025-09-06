package me.kpavlov.mokksy.utils

import io.ktor.http.ContentType
import io.ktor.http.defaultForFile
import io.ktor.http.defaultForFilePath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@JvmOverloads
public fun File.asBase64DataUrl(mimeType: MimeType = ContentType.defaultForFile(this).toString()): String =
    this.readBytes().asBase64DataUrl(mimeType)

@JvmOverloads
public fun Path.asBase64DataUrl(
    mimeType: MimeType =
        ContentType.Companion
            .defaultForFilePath(
                fileName.toString(),
            ).asMimeType(),
): String = Files.readAllBytes(this).asBase64DataUrl(mimeType)
