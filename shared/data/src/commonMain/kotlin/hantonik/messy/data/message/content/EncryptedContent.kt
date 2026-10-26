package hantonik.messy.data.message.content

import dev.whyoleg.cryptography.algorithms.AES
import hantonik.messy.encryption.Crypt
import hantonik.messy.encryption.exception.CryptException
import kotlin.jvm.JvmStatic

class EncryptedContent(val encryptedContent: ByteArray, val contentIV: ByteArray) {
    companion object {
        @JvmStatic
        @Throws(CryptException::class)
        fun encrypt(content: ByteArray, aesKey: AES.GCM.Key): EncryptedContent {
            val contentAndIV = Crypt.encryptUsingAESKeyGCM(content, aesKey)

            return EncryptedContent(
                contentAndIV[0],
                contentAndIV[1]
            )
        }

        @JvmStatic
        @Throws(CryptException::class)
        fun decrypt(encryptedContent: EncryptedContent, aesKey: AES.GCM.Key): ByteArray {
            return Crypt.decryptUsingAESKeyGCM(encryptedContent.encryptedContent, encryptedContent.contentIV, aesKey)
        }
    }
}