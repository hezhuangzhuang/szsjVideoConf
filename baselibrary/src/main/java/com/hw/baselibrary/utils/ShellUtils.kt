package com.hw.baselibrary.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 *author：pc-20171125
 *data:2019/11/7 16:47
 */
object ShellUtils {
    private val LINE_SEP = System.getProperty("line.separator")

    private fun ShellUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * Execute the command.
     *
     * @param command  The command.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(command: String, isRooted: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRooted, true)
    }

    /**
     * Execute the command.
     *
     * @param commands The commands.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(commands: List<String>?, isRooted: Boolean): CommandResult {
        return execCmd(commands?.toTypedArray(), isRooted, true)
    }

    /**
     * Execute the command.
     *
     * @param commands The commands.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(commands: Array<String>, isRooted: Boolean): CommandResult {
        return execCmd(commands, isRooted, true)
    }

    /**
     * Execute the command.
     *
     * @param command         The command.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the msg of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        command: String,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(arrayOf(command), isRooted, isNeedResultMsg)
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the msg of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        commands: List<String>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(
            commands?.toTypedArray(),
            isRooted,
            isNeedResultMsg
        )
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the msg of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        commands: Array<String>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        var result = -1
        if (commands == null || commands.size == 0) {
            return CommandResult(result, "", "")
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh")
            os = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                if (command == null) continue
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
                os.flush()
            }
            os.writeBytes("exit$LINE_SEP")
            os.flush()
            result = process.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(
                    InputStreamReader(process.inputStream, "UTF-8")
                )
                errorResult = BufferedReader(
                    InputStreamReader(process.errorStream, "UTF-8")
                )
                var line: String? = successResult.readLine()
                if (null != line) {
                    successMsg.append(line)
                    do {
                        line = successResult.readLine()
                        if (null != line) {
                            successMsg.append(LINE_SEP).append(line)
                        }
                    } while (null != line)
                }

                line = errorResult.readLine()
                if (null != line) {
                    errorMsg.append(line)
                    do {
                        line = errorResult.readLine()
                        if (null != line) {
                            errorMsg.append(LINE_SEP).append(line)
                        }
                    } while (null != line)
                }
//                if ((line = successResult.readLine()) != null) {
//                    successMsg.append(line)
//                    while ((line = successResult.readLine()) != null) {
//                        successMsg.append(LINE_SEP).append(line)
//                    }
//                }
//                if ((line = errorResult.readLine()) != null) {
//                    errorMsg.append(line)
//                    while ((line = errorResult.readLine()) != null) {
//                        errorMsg.append(LINE_SEP).append(line)
//                    }
//                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                successResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            process?.destroy()
        }
        return CommandResult(
            result,
            successMsg?.toString() ?: "",
            errorMsg?.toString() ?: ""
        )
    }

    /**
     * The result of command.
     */
    class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {

        override fun toString(): String {
            return "result: " + result + "\n" +
                    "successMsg: " + successMsg + "\n" +
                    "errorMsg: " + errorMsg
        }
    }
}