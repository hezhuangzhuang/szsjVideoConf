package com.hw.baselibrary.utils

import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import java.io.*
import java.util.*

/**
 *author：pc-20171125
 *data:2019/11/8 12:41
 */
object IOUtils {

    fun closeQuietly(closeable: Closeable?) {
        if (closeable == null) return
        try {
            closeable.close()
        } catch (e: Exception) {
            //TODO
//            CommonLogger.printStackTrace(e)
        }

    }

    fun flushQuietly(flushable: Flushable?) {
        if (flushable == null) return
        try {
            flushable.flush()
        } catch (e: Exception) {
//            CommonLogger.printStackTrace(e)
        }

    }

    fun toInputStream(input: CharSequence): InputStream {
        return ByteArrayInputStream(input.toString().toByteArray())
    }

    @Throws(UnsupportedEncodingException::class)
    fun toInputStream(input: CharSequence, encoding: String): InputStream {
        val bytes = input.toString().toByteArray(charset(encoding))
        return ByteArrayInputStream(bytes)
    }

    fun toBufferedInputStream(inputStream: InputStream): BufferedInputStream {
        return if (inputStream is BufferedInputStream) inputStream else BufferedInputStream(
            inputStream
        )
    }

    fun toBufferedOutputStream(outputStream: OutputStream): BufferedOutputStream {
        return if (outputStream is BufferedOutputStream) outputStream else BufferedOutputStream(
            outputStream
        )
    }

    fun toBufferedReader(reader: Reader): BufferedReader {
        return if (reader is BufferedReader) reader else BufferedReader(reader)
    }

    fun toBufferedWriter(writer: Writer): BufferedWriter {
        return if (writer is BufferedWriter) writer else BufferedWriter(writer)
    }

    @Throws(IOException::class)
    fun toString(input: InputStream): String {
        return String(toByteArray(input))
    }

    @Throws(IOException::class)
    fun toString(input: InputStream, encoding: String): String {
        return String(toByteArray(input))
//        return String(toByteArray(input), encoding)
    }

    @Throws(IOException::class)
    fun toString(input: Reader): String {
        return String(toByteArray(input))
    }

    @Throws(IOException::class)
    fun toString(input: Reader, encoding: String): String {
        return String(toByteArray(input))
//        return String(toByteArray(input), encoding)
    }

    fun toString(byteArray: ByteArray): String {
        return String(byteArray)
    }

    fun toString(byteArray: ByteArray, encoding: String): String {
        try {
//            return String(byteArray, encoding)
            return String(byteArray)
        } catch (e: UnsupportedEncodingException) {
            return String(byteArray)
        }

    }

    fun toByteArray(input: Any): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(input)
            oos.flush()
            return baos.toByteArray()
        } catch (e: IOException) {
//            CommonLogger.printStackTrace(e)
        } finally {
            IOUtils.closeQuietly(oos)
            IOUtils.closeQuietly(baos)
        }
        return null
    }

    fun toObject(input: ByteArray?): Any? {
        if (input == null) return null
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bais = ByteArrayInputStream(input)
            ois = ObjectInputStream(bais)
            return ois.readObject()
        } catch (e: Exception) {
//            CommonLogger.printStackTrace(e)
        } finally {
            IOUtils.closeQuietly(ois)
            IOUtils.closeQuietly(bais)
        }
        return null
    }

    fun toByteArray(input: CharSequence?): ByteArray {
        return input?.toString()?.toByteArray() ?: ByteArray(0)
    }

    @Throws(UnsupportedEncodingException::class)
    fun toByteArray(input: CharSequence?, encoding: String): ByteArray {
        return input?.toString()?.toByteArray(charset(encoding)) ?: ByteArray(0)
    }

    @Throws(IOException::class)
    fun toByteArray(input: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        write(input, output)
        output.close()
        return output.toByteArray()
    }

    @Throws(IOException::class)
    fun toByteArray(input: Reader): ByteArray {
        val output = ByteArrayOutputStream()
        write(input, output)
        output.close()
        return output.toByteArray()
    }

    @Throws(IOException::class)
    fun toByteArray(input: Reader, encoding: String): ByteArray {
        val output = ByteArrayOutputStream()
        write(input, output, encoding)
        output.close()
        return output.toByteArray()
    }

    @Throws(IOException::class)
    fun toCharArray(input: CharSequence): CharArray {
        val output = CharArrayWriter()
        write(input, output)
        return output.toCharArray()
    }

    @Throws(IOException::class)
    fun toCharArray(input: InputStream): CharArray {
        val output = CharArrayWriter()
        write(input, output)
        return output.toCharArray()
    }

    @Throws(IOException::class)
    fun toCharArray(input: InputStream, encoding: String): CharArray {
        val output = CharArrayWriter()
        write(input, output, encoding)
        return output.toCharArray()
    }

    @Throws(IOException::class)
    fun toCharArray(input: Reader): CharArray {
        val output = CharArrayWriter()
        write(input, output)
        return output.toCharArray()
    }

    @Throws(IOException::class)
    fun readLines(input: InputStream, encoding: String): List<String> {
        val reader = InputStreamReader(input, encoding)
        return readLines(reader)
    }

    @Throws(IOException::class)
    fun readLines(input: InputStream): List<String> {
        val reader = InputStreamReader(input)
        return readLines(reader)
    }

    @Throws(IOException::class)
    fun readLines(input: Reader): List<String> {
        val reader = toBufferedReader(input)
        val list = ArrayList<String>()
        var line: String? = reader.readLine()
        while (line != null) {
            list.add(line)
            line = reader.readLine()
        }
        return list
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?, output: OutputStream) {
        if (data != null) output.write(data)
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?, output: Writer) {
        if (data != null) output.write(String(data))
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?, output: Writer, encoding: String) {
//        if (data != null){
//            output.write(String(data))
//        }

        data?.let {
            output.write(String(data))
        }
    }

    @Throws(IOException::class)
    fun write(data: CharArray?, output: Writer) {
        if (data != null) output.write(data)
    }

    @Throws(IOException::class)
    fun write(data: CharArray?, output: OutputStream) {
        if (data != null) output.write(String(data).toByteArray())
    }

    @Throws(IOException::class)
    fun write(data: CharArray?, output: OutputStream, encoding: String) {
        if (data != null) output.write(String(data).toByteArray(charset(encoding)))
    }

    @Throws(IOException::class)
    fun write(data: CharSequence?, output: Writer) {
        if (data != null) output.write(data.toString())
    }

    @Throws(IOException::class)
    fun write(data: CharSequence?, output: OutputStream) {
        if (data != null) output.write(data.toString().toByteArray())
    }

    @Throws(IOException::class)
    fun write(data: CharSequence?, output: OutputStream, encoding: String) {
        if (data != null) output.write(data.toString().toByteArray(charset(encoding)))
    }

    @Throws(IOException::class)
    fun write(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(4096)
        var len: Int = inputStream.read(buffer)
        while (-1 != len) {
            outputStream.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }
//        while ((len = inputStream.read(buffer)) != -1){
//            outputStream.write(buffer, 0, len)
//        }
    }

    @Throws(IOException::class)
    fun write(input: Reader, output: OutputStream) {
        val out = OutputStreamWriter(output)
        write(input, out)
        out.flush()
    }

    @Throws(IOException::class)
    fun write(input: InputStream, output: Writer) {
        val `in` = InputStreamReader(input)
        write(`in`, output)
    }

    @Throws(IOException::class)
    fun write(input: Reader, output: OutputStream, encoding: String) {
        val out = OutputStreamWriter(output, encoding)
        write(input, out)
        out.flush()
    }

    @Throws(IOException::class)
    fun write(input: InputStream, output: OutputStream, encoding: String) {
        val `in` = InputStreamReader(input, encoding)
        write(`in`, output)
    }

    @Throws(IOException::class)
    fun write(input: InputStream, output: Writer, encoding: String) {
        val `in` = InputStreamReader(input, encoding)
        write(`in`, output)
    }

    @Throws(IOException::class)
    fun write(input: Reader, output: Writer) {
        var len: Int
        val buffer = CharArray(4096)
        do {
            len = input.read(buffer)
            output.write(buffer, 0, len)
        } while (-1 != len)
    }

    @Throws(IOException::class)
    fun contentEquals(input1: InputStream, input2: InputStream): Boolean {
        var input1 = input1
        var input2 = input2
        input1 = toBufferedInputStream(input1)
        input2 = toBufferedInputStream(input2)

        var ch = input1.read()
        while (-1 != ch) {
            val ch2 = input2.read()
            if (ch != ch2) {
                return false
            }
            ch = input1.read()
        }

        val ch2 = input2.read()
        return ch2 == -1
    }

    @Throws(IOException::class)
    fun contentEquals(input1: Reader, input2: Reader): Boolean {
        var input1 = input1
        var input2 = input2
        input1 = toBufferedReader(input1)
        input2 = toBufferedReader(input2)

        var ch = input1.read()
        while (-1 != ch) {
            val ch2 = input2.read()
            if (ch != ch2) {
                return false
            }
            ch = input1.read()
        }

        val ch2 = input2.read()
        return ch2 == -1
    }

    @Throws(IOException::class)
    fun contentEqualsIgnoreEOL(input1: Reader, input2: Reader): Boolean {
        val br1 = toBufferedReader(input1)
        val br2 = toBufferedReader(input2)

        var line1: String? = br1.readLine()
        var line2: String? = br2.readLine()
        while (line1 != null && line2 != null && line1 == line2) {
            line1 = br1.readLine()
            line2 = br2.readLine()
        }
        return line1 != null && (line2 == null || line1 == line2)
    }

    /**
     * Access to a directory available size.
     *
     * @param path path.
     * @return space size.
     */
    fun getDirSize(path: String): Long {
        val stat: StatFs
        try {
            stat = StatFs(path)
        } catch (e: Exception) {
//            CommonLogger.printStackTrace(e)
            return 0
        }

        return if (Build.VERSION.SDK_INT >= 18)
            getStatFsSize(stat, "getBlockSizeLong", "getAvailableBlocksLong")
        else
            getStatFsSize(stat, "getBlockSize", "getAvailableBlocks")
    }

    private fun getStatFsSize(
        statFs: StatFs,
        blockSizeMethod: String,
        availableBlocksMethod: String
    ): Long {
        try {
            val getBlockSizeMethod = statFs.javaClass.getMethod(blockSizeMethod)
            getBlockSizeMethod.isAccessible = true

            val getAvailableBlocksMethod = statFs.javaClass.getMethod(availableBlocksMethod)
            getAvailableBlocksMethod.isAccessible = true

            val blockSize = getBlockSizeMethod.invoke(statFs) as Long
            val availableBlocks = getAvailableBlocksMethod.invoke(statFs) as Long
            return blockSize * availableBlocks
        } catch (e: Throwable) {
//            CommonLogger.printStackTrace(e)
        }

        return 0
    }

    /**
     * If the folder can be written.
     *
     * @param path path.
     * @return True: success, or false: failure.
     */
    fun canWrite(path: String): Boolean {
        return File(path).canWrite()
    }

    /**
     * If the folder can be read.
     *
     * @param path path.
     * @return True: success, or false: failure.
     */
    fun canRead(path: String): Boolean {
        return File(path).canRead()
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param folderPath folder path.
     * @return True: success, or false: failure.
     */
    fun createFolder(folderPath: String): Boolean {
        if (!TextUtils.isEmpty(folderPath)) {
            val folder = File(folderPath)
            return createFolder(folder)
        }
        return false
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param targetFolder folder path.
     * @return True: success, or false: failure.
     */
    fun createFolder(targetFolder: File): Boolean {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory) return true

            targetFolder.delete()
        }
        return targetFolder.mkdirs()
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param folderPath folder path.
     * @return True: success, or false: failure.
     */
    fun createNewFolder(folderPath: String): Boolean {
        return delFileOrFolder(folderPath) && createFolder(folderPath)
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param targetFolder folder path.
     * @return True: success, or false: failure.
     */
    fun createNewFolder(targetFolder: File): Boolean {
        return delFileOrFolder(targetFolder) && createFolder(targetFolder)
    }

    /**
     * Create a file, If the file exists is not created.
     *
     * @param filePath file path.
     * @return True: success, or false: failure.
     */
    fun createFile(filePath: String): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            return createFile(file)
        }
        return false
    }

    /**
     * Create a file, If the file exists is not created.
     *
     * @param targetFile file.
     * @return True: success, or false: failure.
     */
    fun createFile(targetFile: File): Boolean {
        if (targetFile.exists()) {
            if (targetFile.isFile) return true
            delFileOrFolder(targetFile)
        }
        try {
            return targetFile.createNewFile()
        } catch (e: IOException) {
            return false
        }

    }

    /**
     * Create a new file, if the file exists, delete and create again.
     *
     * @param filePath file path.
     * @return True: success, or false: failure.
     */
    fun createNewFile(filePath: String): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            return createNewFile(file)
        }
        return false
    }

    /**
     * Create a new file, if the file exists, delete and create again.
     *
     * @param targetFile file.
     * @return True: success, or false: failure.
     */
    fun createNewFile(targetFile: File): Boolean {
        if (targetFile.exists()) delFileOrFolder(targetFile)
        try {
            return targetFile.createNewFile()
        } catch (e: IOException) {
            return false
        }

    }

    /**
     * Delete file or folder.
     *
     * @param path path.
     * @return is succeed.
     * @see .delFileOrFolder
     */
    fun delFileOrFolder(path: String): Boolean {
        return if (TextUtils.isEmpty(path)) false else delFileOrFolder(File(path))
    }


    /**
     * Delete file or folder.
     *
     * @param file file.
     * @return is succeed.
     * @see .delFileOrFolder
     */
    fun delFileOrFolder(file: File?): Boolean {
        if (file == null || !file.exists()) {
            // do nothing
        } else if (file.isFile) {
            file.delete()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (sonFile in files) {
                    delFileOrFolder(sonFile)
                }
            }
            file.delete()
        }
        return true
    }

    /**
     * 关闭流
     */
    fun close(io: Closeable?): Boolean {
        if (io != null) {
            try {
                io.close()
            } catch (e: IOException) {
                //                LogUtils.e(e);
            }
        }
        return true
    }
}