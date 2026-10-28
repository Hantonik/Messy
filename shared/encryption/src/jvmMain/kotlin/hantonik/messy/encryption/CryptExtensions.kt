package hantonik.messy.encryption

import dev.whyoleg.cryptography.algorithms.AES
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

fun Crypt.toCipher(opMode: Int, key: AES.GCM.Key): Cipher {
    return Cipher.getInstance("AES/GCM/NoPadding").init(opMode, SecretKeySpec(this.encodeAESKeyGCM(key), "AES"), GCMParameterSpec(128, ))
// TODO Losowe IV przy kazdym szyfrowaniu mi sie nie spodobalo, trzeba poprawic, mozliwe ze CipherBase.java nie bylo takie najgorsze
}
