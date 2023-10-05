package ${package_name}.helpers

import ${package_name}.enums.CharacterType
import ${package_name}.models.User
import ${package_name}.others.RedisKey
import org.apache.commons.codec.binary.Hex
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import jakarta.annotation.Resource

@Component
class PasswordHelper {
    @Resource
    private lateinit var redisHelper: RedisHelper

    val salt: String
        get() {
            val buffer = StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
            val sb = StringBuffer()
            val random = Random()
            val range = buffer.length
            for (i in 0 until 16) {
                sb.append(buffer[random.nextInt(range)])
            }
            return sb.toString()
        }


    /**
     * 生成含有随机盐的密码
     */
    fun generate(password: String, salt: String): String {
        val passMd5 = md5Hex(password + salt)
        val cs = CharArray(48)
        var i = 0
        while (i < 48) {
            cs[i] = passMd5[i / 3 * 2]
            val c = salt[i / 3]
            cs[i + 1] = c
            cs[i + 2] = passMd5[i / 3 * 2 + 1]
            i += 3
        }
        return String(cs)
    }

    /**
     * 校验密码是否正确
     */
    fun verify(password: String, md5: String): Boolean {
        val cs1 = CharArray(32)
        val cs2 = CharArray(16)
        var i = 0
        while (i < 48) {
            cs1[i / 3 * 2] = md5[i]
            cs1[i / 3 * 2 + 1] = md5[i + 2]
            cs2[i / 3] = md5[i + 1]
            i += 3
        }
        val salt = String(cs2)
        return md5Hex(password + salt) == String(cs1)
    }

    fun verify(plainPassword: String, cipherPassword: String, salt: String): Boolean {
        return cipherPassword == generate(plainPassword, salt)
    }

    fun verify(plainPassword: String, user: User): Boolean {
        val result = verify(plainPassword, user.password!!, user.salt!!)
        if (result) {
            return true
        }
        val passwordErrorTimes = redisHelper.get(RedisKey.passwordErrorTimes(user.mobile))?.toInt() ?: 0
        redisHelper.set(RedisKey.passwordErrorTimes(user.mobile), (passwordErrorTimes + 1).toString(), 60, TimeUnit.MINUTES)
        return false
    }

    fun tooWeak(password: String): Boolean {
        if (password.length < 8) {
            return true
        }
        if (countLetter(password, CharacterType.Number) < 1) {
            return true
        }
        if (countLetter(password, CharacterType.SmallLetter) < 1) {
            return true
        }
        if (countLetter(password, CharacterType.CapitalLetter) < 1) {
            return true
        }

        return false
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    fun md5Hex(src: String): String {
        val md5 = MessageDigest.getInstance("MD5")
        val bs = md5.digest(src.toByteArray())
        return String(Hex().encode(bs))
    }

    private fun countLetter(password: String, type: CharacterType): Int {
        var count = 0
        if (password.isNotEmpty()) {
            for (c in password.toCharArray()) {
                if (checkCharacterType(c) === type) {
                    count++
                }
            }
        }
        return count
    }

    private fun checkCharacterType(c: Char): CharacterType {
        if (c.code in 48..57) {
            return CharacterType.Number
        }
        if (c.code in 65..90) {
            return CharacterType.CapitalLetter
        }
        if (c.code in 97..122) {
            return CharacterType.SmallLetter
        }
        return CharacterType.Others
    }
}
