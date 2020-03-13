package ${package}.others

import java.nio.charset.Charset
import java.util.Arrays

/**
 * 提供基于PKCS7算法的加解
 */
object PKCS7Encoder {

    private val CHARSET = Charset.forName("utf-8")
    private val BLOCK_SIZE = 32

    /**
     * 获得对明文进行补位填充的字节.
     *
     * @param count 需要进行填充补位操作的明文字节个数
     * @return 补齐用的字节数组
     */
    fun encode(count: Int): ByteArray {
        // 计算需要填充的位数
        var amountToPad = BLOCK_SIZE - count % BLOCK_SIZE
        if (amountToPad == 0) {
            amountToPad = BLOCK_SIZE
        }
        // 获得补位所用的字符
        val padChr = chr(amountToPad)
        var tmp = String()
        for (index in 0 until amountToPad) {
            tmp += padChr
        }
        return tmp.toByteArray(CHARSET)
    }

    /**
     * 删除解密后明文的补位字符
     *
     * @param decrypted 解密后的明文
     * @return 删除补位字符后的明文
     */
    fun decode(decrypted: ByteArray): ByteArray {
        var pad = decrypted[decrypted.size - 1].toInt()
        if (pad < 1 || pad > 32) {
            pad = 0
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.size - pad)
    }

    /**
     * 将数字转化成ASCII码对应的字符，用于对明文进行补码
     *
     * @param a 需要转化的数字
     * @return 转化得到的字符
     */
    fun chr(a: Int): Char {
        val target = (a and 0xFF).toByte()
        return target.toChar()
    }

}
