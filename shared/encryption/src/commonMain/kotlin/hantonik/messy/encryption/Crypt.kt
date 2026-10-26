package hantonik.messy.encryption

import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.BinarySize.Companion.bytes
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.*
import dev.whyoleg.cryptography.random.CryptographyRandom
import hantonik.messy.encryption.exception.CryptException
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

object Crypt {
    @JvmStatic
    @Throws(CryptException::class)
    fun generateAES256KeyGCM(): AES.GCM.Key {
        try {
            return CryptographyProvider.Default.get(AES.GCM)
                .keyGenerator(256.bits)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateSharedAES256KeyGCM(privateKey: XDH.PrivateKey, publicKey: XDH.PublicKey, salt: ByteArray): AES.GCM.Key {
        try {
            return this.decodeAESKeyGCM(this.digestDataHKDFWithSHA256(privateKey.sharedSecretGenerator().generateSharedSecretToByteArrayBlocking(publicKey), salt))
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateSharedAES256KeyGCM(privateKey: ECDH.PrivateKey, publicKey: ECDH.PublicKey, salt: ByteArray): AES.GCM.Key {
        try {
            return this.decodeAESKeyGCM(this.digestDataHKDFWithSHA256(privateKey.sharedSecretGenerator().generateSharedSecretToByteArrayBlocking(publicKey), salt))
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateRSA3072KeyPairOAEP(): RSA.OAEP.KeyPair {
        try {
            return CryptographyProvider.Default.get(RSA.OAEP)
                .keyPairGenerator(3072.bits, SHA256)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateRSA4096KeyPairOAEP(): RSA.OAEP.KeyPair {
        try {
            return CryptographyProvider.Default.get(RSA.OAEP)
                .keyPairGenerator(4096.bits, SHA256)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateXDHKeyPairX25519(): XDH.KeyPair {
        try {
            return CryptographyProvider.Default.get(XDH)
                .keyPairGenerator(XDH.Curve.X25519)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateXDHKeyPairX448(): XDH.KeyPair {
        try {
            return CryptographyProvider.Default.get(XDH)
                .keyPairGenerator(XDH.Curve.X448)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateECDHKeyPairP384(): ECDH.KeyPair {
        try {
            return CryptographyProvider.Default.get(ECDH)
                .keyPairGenerator(EC.Curve.P384)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun generateEdDSAKeyPairEd25519(): EdDSA.KeyPair {
        try {
            return CryptographyProvider.Default.get(EdDSA)
                .keyPairGenerator(EdDSA.Curve.Ed25519)
                .generateKeyBlocking()
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun generateSalt(length: Int = 64) = CryptographyRandom.nextBytes(length)

    @JvmStatic
    @JvmOverloads
    fun generateIV(length: Int = 12) = CryptographyRandom.nextBytes(length)

    @JvmStatic
    @Throws(CryptException::class)
    fun digestDataPBKDF2WithSHA512(input: ByteArray, salt: ByteArray): ByteArray {
        try {
            return CryptographyProvider.Default.get(PBKDF2)
                .secretDerivation(SHA512, 210_000, 64.bytes, salt)
                .deriveSecretToByteArrayBlocking(input)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun digestDataHKDFWithSHA256(input: ByteArray, salt: ByteArray): ByteArray {
        try {
            return CryptographyProvider.Default.get(HKDF)
                .secretDerivation(SHA256, 32.bytes, salt)
                .deriveSecretToByteArrayBlocking(input)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @JvmOverloads
    @Throws(CryptException::class)
    fun digestDataSHA256(input: String, iterationCount: Int = 1) = this.digestDataSHA256(input.encodeToByteArray(), iterationCount = iterationCount)

    @JvmStatic
    @JvmOverloads
    @Throws(CryptException::class)
    fun digestDataSHA256(bytes: ByteArray, iterationCount: Int = 1): ByteArray {
        try {
            val hasher = CryptographyProvider.Default.get(SHA256).hasher()

            var result = hasher.hashBlocking(bytes)

            repeat(iterationCount - 1) {
                result = hasher.hashBlocking(result)
            }

            return result
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    @OptIn(DelicateCryptographyApi::class)
    fun encryptUsingAESKeyGCM(input: ByteArray, iv: ByteArray, key: AES.GCM.Key): ByteArray {
        try {
            return key.cipher().encryptWithIvBlocking(iv, input)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    @OptIn(DelicateCryptographyApi::class)
    fun decryptUsingAESKeyGCM(input: ByteArray, iv: ByteArray, key: AES.GCM.Key): ByteArray {
        try {
            return key.cipher().decryptWithIvBlocking(iv, input)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeAESKeyGCM(encodedKey: ByteArray): AES.GCM.Key {
        try {
            return CryptographyProvider.Default.get(AES.GCM)
                .keyDecoder()
                .decodeFromByteArrayBlocking(AES.Key.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeRSAPublicKeyOAEP(encodedKey: ByteArray): RSA.OAEP.PublicKey {
        try {
            return CryptographyProvider.Default.get(RSA.OAEP)
                .publicKeyDecoder(SHA256)
                .decodeFromByteArrayBlocking(RSA.PublicKey.Format.DER, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeRSAPrivateKeyOAEP(encodedKey: ByteArray): RSA.OAEP.PrivateKey {
        try {
            return CryptographyProvider.Default.get(RSA.OAEP)
                .privateKeyDecoder(SHA256)
                .decodeFromByteArrayBlocking(RSA.PrivateKey.Format.DER, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeXDHPublicKeyX25519(encodedKey: ByteArray): XDH.PublicKey {
        try {
            return CryptographyProvider.Default.get(XDH)
                .publicKeyDecoder(XDH.Curve.X25519)
                .decodeFromByteArrayBlocking(XDH.PublicKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeXDHPrivateKeyX25519(encodedKey: ByteArray): XDH.PrivateKey {
        try {
            return CryptographyProvider.Default.get(XDH)
                .privateKeyDecoder(XDH.Curve.X25519)
                .decodeFromByteArrayBlocking(XDH.PrivateKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeXDHPublicKeyX448(encodedKey: ByteArray): XDH.PublicKey {
        try {
            return CryptographyProvider.Default.get(XDH)
                .publicKeyDecoder(XDH.Curve.X448)
                .decodeFromByteArrayBlocking(XDH.PublicKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeXDHPrivateKeyX448(encodedKey: ByteArray): XDH.PrivateKey {
        try {
            return CryptographyProvider.Default.get(XDH)
                .privateKeyDecoder(XDH.Curve.X448)
                .decodeFromByteArrayBlocking(XDH.PrivateKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeECDHPublicKeyP384(encodedKey: ByteArray): ECDH.PublicKey {
        try {
            return CryptographyProvider.Default.get(ECDH)
                .publicKeyDecoder(EC.Curve.P384)
                .decodeFromByteArrayBlocking(EC.PublicKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeECDHPrivateKeyP384(encodedKey: ByteArray): ECDH.PrivateKey {
        try {
            return CryptographyProvider.Default.get(ECDH)
                .privateKeyDecoder(EC.Curve.P384)
                .decodeFromByteArrayBlocking(EC.PrivateKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeEdDSAPublicKeyEd25519(encodedKey: ByteArray): EdDSA.PublicKey {
        try {
            return CryptographyProvider.Default.get(EdDSA)
                .publicKeyDecoder(EdDSA.Curve.Ed25519)
                .decodeFromByteArrayBlocking(EdDSA.PublicKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    @Throws(CryptException::class)
    fun decodeEdDSAPrivateKeyEd25519(encodedKey: ByteArray): EdDSA.PrivateKey {
        try {
            return CryptographyProvider.Default.get(EdDSA)
                .privateKeyDecoder(EdDSA.Curve.Ed25519)
                .decodeFromByteArrayBlocking(EdDSA.PrivateKey.Format.RAW, encodedKey)
        } catch (e: Exception) {
            throw CryptException(e)
        }
    }

    @JvmStatic
    fun encodeAESKeyGCM(key: AES.GCM.Key): ByteArray {
        return key.encodeToByteArrayBlocking(AES.Key.Format.RAW)
    }

    @JvmStatic
    fun encodeRSAPublicKeyOAEP(key: RSA.OAEP.PublicKey): ByteArray {
        return key.encodeToByteArrayBlocking(RSA.PublicKey.Format.DER)
    }

    @JvmStatic
    fun encodeRSAPrivateKeyOAEP(key: RSA.OAEP.PrivateKey): ByteArray {
        return key.encodeToByteArrayBlocking(RSA.PrivateKey.Format.DER)
    }

    @JvmStatic
    fun encodeXDHPublicKey(key: XDH.PublicKey): ByteArray {
        return key.encodeToByteArrayBlocking(XDH.PublicKey.Format.RAW)
    }

    @JvmStatic
    fun encodeXDHPrivateKey(key: XDH.PrivateKey): ByteArray {
        return key.encodeToByteArrayBlocking(XDH.PrivateKey.Format.RAW)
    }

    @JvmStatic
    fun encodeECDHPublicKey(key: ECDH.PublicKey): ByteArray {
        return key.encodeToByteArrayBlocking(EC.PublicKey.Format.RAW)
    }

    @JvmStatic
    fun encodeECDHPrivateKey(key: ECDH.PrivateKey): ByteArray {
        return key.encodeToByteArrayBlocking(EC.PrivateKey.Format.RAW)
    }

    @JvmStatic
    fun encodeEdDSAPublicKey(key: EdDSA.PublicKey): ByteArray {
        return key.encodeToByteArrayBlocking(EdDSA.PublicKey.Format.RAW)
    }

    @JvmStatic
    fun encodeEdDSAPrivateKey(key: EdDSA.PrivateKey): ByteArray {
        return key.encodeToByteArrayBlocking(EdDSA.PrivateKey.Format.RAW)
    }
}