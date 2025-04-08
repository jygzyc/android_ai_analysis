package me.yvesz.jadxmcp.config

import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import java.nio.file.Files
import java.nio.file.Path

object JadxDecompileManager {

    private var jadxDecompiler: JadxDecompiler? = null

    /**
     * Get the globally unique JadxDecompiler instance.
     * If the instance already exists, return it directly; if it doesn't exist, return null.
     */
    fun getInstance(): JadxDecompiler? {
        return jadxDecompiler
    }

    /**
     * Initialize and get the globally unique JadxDecompiler instance.
     * If the instance already exists, return it directly; otherwise initialize it according to parameters.
     */
    fun getInstance(filePath: Path, outPath: Path): JadxDecompiler? {
        if (jadxDecompiler == null) {
            synchronized(this) {
                if (jadxDecompiler == null) { // Double-checked locking
                    jadxDecompiler = initializeJadxDecompiler(filePath, outPath)
                }
            }
        }
        return jadxDecompiler
    }

    /**
     * Close and clean up the JadxDecompiler instance.
     */
    fun clearInstance() {
        synchronized(this) {
            jadxDecompiler?.close()
            jadxDecompiler = null
        }
    }

    /**
     * Initialize the JadxDecompiler instance.
     */
    private fun initializeJadxDecompiler(filePath: Path, outPath: Path): JadxDecompiler? {
        require(Files.isRegularFile(filePath)) {
            "Invalid input file path: $filePath"
        }
        return try {
            val jadxArgs = JadxArgs().apply {
                setInputFile(filePath.toFile())
                outDir = outPath.toFile().apply {
                    parentFile?.takeUnless { it.exists() }?.mkdirs()
                }
            }
            JadxDecompiler(jadxArgs).apply {
                load()
                save()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
