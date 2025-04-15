package me.yvesz.jadxServer.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import org.slf4j.LoggerFactory

/**
 * A utility class for file operations.
 * 
 * This class provides methods for calculating SHA256 hash of files,
 * writing content to files, and reading files into memory.
 */
class FileUtil {
    
    companion object {
        private val log = LoggerFactory.getLogger(FileUtil::class.java)
        
        /**
         * Calculates the SHA-256 hash of a file.
         *
         * @param file The file to calculate hash for
         * @return The SHA-256 hash as a hexadecimal string, or null if an error occurs
         */
        fun calculateSHA256(file: File): String? {
            if (!file.exists() || !file.isFile) {
                log.error("File does not exist or is not a regular file: {}", file.absolutePath)
                return null
            }
            
            return try {
                val digest = MessageDigest.getInstance("SHA-256")
                val buffer = ByteArray(8192)
                var bytesRead: Int
                
                FileInputStream(file).use { fis ->
                    bytesRead = fis.read(buffer)
                    while (bytesRead != -1) {
                        digest.update(buffer, 0, bytesRead)
                        bytesRead = fis.read(buffer)
                    }
                }
                
                // Convert the digest to a hexadecimal string
                val hashBytes = digest.digest()
                val hexString = StringBuilder()
                
                for (byte in hashBytes) {
                    val hex = Integer.toHexString(0xff and byte.toInt())
                    if (hex.length == 1) {
                        hexString.append('0')
                    }
                    hexString.append(hex)
                }
                
                hexString.toString()
            } catch (e: Exception) {
                log.error("Error calculating SHA-256 hash for file: {}", file.absolutePath, e)
                null
            }
        }
        
        /**
         * Writes content to a file at the specified path.
         *
         * @param content The content to write to the file
         * @param filePath The path where the file should be created or overwritten
         * @return True if the operation was successful, false otherwise
         */
        fun writeToFile(content: String, filePath: String): Boolean {
            val file = File(filePath)
            
            // Create parent directories if they don't exist
            file.parentFile?.mkdirs()
            
            return try {
                FileOutputStream(file).use { fos ->
                    fos.write(content.toByteArray())
                }
                log.info("Successfully wrote content to file: {}", filePath)
                true
            } catch (e: Exception) {
                log.error("Error writing content to file: {}", filePath, e)
                false
            }
        }
        
        /**
         * Writes binary content to a file at the specified path.
         *
         * @param content The binary content to write to the file
         * @param filePath The path where the file should be created or overwritten
         * @return True if the operation was successful, false otherwise
         */
        fun writeToFile(content: ByteArray, filePath: String): Boolean {
            val file = File(filePath)
            
            // Create parent directories if they don't exist
            file.parentFile?.mkdirs()
            
            return try {
                FileOutputStream(file).use { fos ->
                    fos.write(content)
                }
                log.info("Successfully wrote binary content to file: {}", filePath)
                true
            } catch (e: Exception) {
                log.error("Error writing binary content to file: {}", filePath, e)
                false
            }
        }
        
        /**
         * Reads a file from the specified path into memory.
         *
         * @param filePath The path of the file to read
         * @return The File object if the file exists, null otherwise
         */
        fun readFile(filePath: String): File? {
            val file = File(filePath)
            
            if (!file.exists() || !file.isFile) {
                log.error("File does not exist or is not a regular file: {}", filePath)
                return null
            }
            
            return file
        }
        
        /**
         * Reads the content of a file as a string.
         *
         * @param filePath The path of the file to read
         * @return The content of the file as a string, or null if an error occurs
         */
        fun readFileAsString(filePath: String): String? {
            val file = File(filePath)
            
            if (!file.exists() || !file.isFile) {
                log.error("File does not exist or is not a regular file: {}", filePath)
                return null
            }
            
            return try {
                file.readText()
            } catch (e: Exception) {
                log.error("Error reading file content: {}", filePath, e)
                null
            }
        }
        
        /**
         * Reads the content of a file as a byte array.
         *
         * @param filePath The path of the file to read
         * @return The content of the file as a byte array, or null if an error occurs
         */
        fun readFileAsByteArray(filePath: String): ByteArray? {
            val file = File(filePath)
            
            if (!file.exists() || !file.isFile) {
                log.error("File does not exist or is not a regular file: {}", filePath)
                return null
            }
            
            return try {
                file.readBytes()
            } catch (e: Exception) {
                log.error("Error reading file content: {}", filePath, e)
                null
            }
        }
    }
}