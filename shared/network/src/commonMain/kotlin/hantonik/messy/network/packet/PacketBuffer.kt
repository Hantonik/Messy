package hantonik.messy.network.packet

import dev.whyoleg.cryptography.algorithms.RSA
import hantonik.messy.data.client.ClientActivity
import hantonik.messy.data.client.ClientData
import hantonik.messy.data.client.ClientProfile
import hantonik.messy.data.client.RelatedClientData
import hantonik.messy.data.device.DeviceInfo
import hantonik.messy.data.device.DeviceType
import hantonik.messy.data.file.FileMetadata
import hantonik.messy.data.message.EncryptedMessage
import hantonik.messy.data.message.MessageType
import hantonik.messy.data.message.content.EncryptedContent
import hantonik.messy.encryption.Crypt
import hantonik.messy.encryption.exception.CryptException
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readTo
import kotlin.jvm.JvmOverloads
import kotlin.time.Instant
import kotlin.uuid.Uuid

class PacketBuffer(val internalSource: Buffer = Buffer()) {
    constructor(bytes: ByteArray) : this() {
        this.internalSource.write(bytes)
    }

    val size: Long
        get() = this.internalSource.size

    fun write(source: ByteArray, startIndex: Int = 0, endIndex: Int = source.size) = this.internalSource.write(source, startIndex, endIndex)

    fun read(destination: ByteArray, startIndex: Int = 0, endIndex: Int = destination.size) = this.internalSource.readTo(destination, startIndex, endIndex)

    fun writeByte(value: Byte) = this.internalSource.writeByte(value)

    fun readByte() = this.internalSource.readByte()

    fun writeInt(value: Int) = this.internalSource.writeInt(value)

    fun readInt() = this.internalSource.readInt()

    fun writeLong(value: Long) = this.internalSource.writeLong(value)

    fun readLong() = this.internalSource.readLong()

    fun writeBoolean(value: Boolean) = this.writeByte(if (value) 1 else 0)

    fun readBoolean() = this.readByte() == 1.toByte()

    fun writeByteArray(array: ByteArray) {
        this.writeInt(array.size)
        this.write(array)
    }

    @JvmOverloads
    fun readByteArray(maxLength: Long = this.internalSource.size): ByteArray {
        val i = this.readInt()

        if (i > maxLength)
            throw IllegalStateException("ByteArray with size $i is bigger than allowed $maxLength")
        else
            return this.internalSource.readByteArray(i)
    }

    fun writeIntArray(array: IntArray) {
        this.writeInt(array.size)

        for (i in array)
            this.writeInt(i)
    }

    @JvmOverloads
    fun readIntArray(maxLength: Long = this.internalSource.size): IntArray {
        val i = this.readInt()

        if (i > maxLength)
            throw IllegalStateException("IntArray with size $i is bigger than allowed $maxLength")
        else {
            val array = IntArray(i)

            repeat(array.size) {
                array[it] = this.readInt()
            }

            return array
        }
    }

    fun writeLongArray(array: LongArray) {
        this.writeInt(array.size)

        for (i in array)
            this.writeLong(i)
    }

    @JvmOverloads
    fun readLongArray(maxLength: Long = this.internalSource.size): LongArray {
        val i = this.readInt()

        if (i > maxLength)
            throw IllegalStateException("LongArray with size $i is bigger than allowed $maxLength")
        else {
            val array = LongArray(i)

            repeat(array.size) {
                array[it] = this.readLong()
            }

            return array
        }
    }

    @JvmOverloads
    fun writeUtf(string: String, maxLength: Int = 32767) {
        val array = string.encodeToByteArray()

        if (array.size > maxLength)
            throw IllegalStateException("String too big (was ${array.size} bytes encoded, max $maxLength)")
        else
            this.writeByteArray(array)
    }

    @JvmOverloads
    fun readUtf(maxLength: Int = 32767): String {
        val i = this.readInt()

        if (i > maxLength * 4)
            throw IllegalStateException("The received encoded string buffer length is longer than maximum allowed ($i > ${maxLength * 4})")
        else if (i < 0)
            throw IllegalStateException("The received encoded string buffer length is less than zero")
        else {
            val s = this.internalSource.readByteArray(i).contentToString()

            if (s.length > maxLength)
                throw IllegalStateException("The received string length is longer than maximum allowed ($i > $maxLength)")

            return s
        }
    }

    fun writeEnum(value: Enum<*>) = this.writeInt(value.ordinal)

    inline fun <reified T : Enum<T>> readEnum(): T = enumValues<T>()[this.readInt()]

    fun writeUuid(uuid: Uuid) = this.writeByteArray(uuid.toByteArray())

    fun readUuid() = Uuid.fromByteArray(this.readByteArray())

    fun writeInstant(instant: Instant) = this.writeLong(instant.toEpochMilliseconds())

    fun readInstant() = Instant.fromEpochMilliseconds(this.readLong())

    fun writeFileMetadata(fileMetadata: FileMetadata) {
        this.writeBoolean(fileMetadata.fileId != null)

        if (fileMetadata.fileId != null)
            this.writeUuid(fileMetadata.fileId!!)

        this.writeUuid(fileMetadata.ownerId)

        this.writeUtf(fileMetadata.originalName)
        this.writeUtf(fileMetadata.mimeType)

        this.writeLong(fileMetadata.sizeBytes)
        this.writeByteArray(fileMetadata.sha256)

        this.writeBoolean(fileMetadata.createdAt != null)

        if (fileMetadata.createdAt != null)
            this.writeInstant(fileMetadata.createdAt!!)
    }

    fun readFileMetadata(): FileMetadata {
        var fileId: Uuid? = null

        if (this.readBoolean())
            fileId = this.readUuid()

        val ownerId = this.readUuid()

        val originalName = this.readUtf(255)
        val mimeType = this.readUtf(255)

        val sizeBytes = this.readLong()
        val sha256 = this.readByteArray()

        var createdAt: Instant? = null

        if (this.readBoolean())
            createdAt = this.readInstant()

        return FileMetadata(fileId, ownerId, originalName, mimeType, sizeBytes, sha256, createdAt)
    }

    fun writeClientData(clientData: ClientData) {
        this.writeUuid(clientData.id)

        this.writeBoolean(clientData.icon != null)

        if (clientData.icon != null)
            this.writeFileMetadata(clientData.icon!!)

        this.writeUtf(clientData.firstName, 255)
        this.writeUtf(clientData.lastName, 255)

        this.writeByteArray(Crypt.encodeRSAPublicKeyOAEP(clientData.publicKey))

        this.writeInstant(clientData.createdAt)

        this.writeBoolean(clientData.deletedAt != null)

        if (clientData.deletedAt != null)
            this.writeInstant(clientData.deletedAt!!)
    }

    fun readClientData(): ClientData {
        val id = this.readUuid()

        var icon: FileMetadata? = null

        if (this.readBoolean())
            icon = this.readFileMetadata()

        val firstName = this.readUtf(255)
        val lastName = this.readUtf(255)

        val publicKey: RSA.OAEP.PublicKey

        try {
            publicKey = Crypt.decodeRSAPublicKeyOAEP(this.readByteArray())
        } catch (e: CryptException) {
            throw IllegalStateException("Malformed public key bytes", e)
        }

        val createdAt = this.readInstant()
        var deletedAt: Instant? = null

        if (this.readBoolean())
            deletedAt = this.readInstant()

        return ClientData(id, icon, firstName, lastName, publicKey, createdAt, deletedAt)
    }

    fun writeRelatedClientData(relatedClientData: RelatedClientData) {
        this.writeClientData(relatedClientData)

        this.writeBoolean(relatedClientData.mutedUntil != null)

        if (relatedClientData.mutedUntil != null)
            this.writeInstant(relatedClientData.mutedUntil!!)

        this.writeBoolean(relatedClientData.isBlocked)
    }

    fun readRelatedClientData(): RelatedClientData {
        val clientData = this.readClientData()

        var mutedUntil: Instant? = null

        if (this.readBoolean())
            mutedUntil = this.readInstant()

        val isBlocked = this.readBoolean()

        return RelatedClientData(clientData, mutedUntil, isBlocked)
    }

    fun writeClientProfile(clientProfile: ClientProfile) {
        this.writeClientData(clientProfile)

        this.writeUtf(clientProfile.email, 255)

        this.writeBoolean(clientProfile.phone != null)

        if (clientProfile.phone != null)
            this.writeUtf(clientProfile.phone!!, 20)

        this.writeByteArray(clientProfile.encryptedPrivateKey)
    }

    fun readClientProfile(): ClientProfile {
        val clientData = this.readClientData()

        val email = this.readUtf(255)
        var phone: String? = null

        if (this.readBoolean())
            phone = this.readUtf(20)

        val encryptedPrivateKey = this.readByteArray()

        return ClientProfile(clientData, email, phone, encryptedPrivateKey)
    }

    fun writeClientActivity(clientActivity: ClientActivity) {
        this.writeUuid(clientActivity.clientId)

        this.writeBoolean(clientActivity.lastSeenAt != null)

        if (clientActivity.lastSeenAt != null)
            this.writeInstant(clientActivity.lastSeenAt!!)

        this.writeBoolean(clientActivity.activeSince != null)

        if (clientActivity.activeSince != null)
            this.writeInstant(clientActivity.activeSince!!)
    }

    fun readClientActivity(): ClientActivity {
        val clientId = this.readUuid()

        var lastSeenAt: Instant? = null

        if (this.readBoolean())
            lastSeenAt = this.readInstant()

        var activeSince: Instant? = null

        if (this.readBoolean())
            activeSince = this.readInstant()

        return ClientActivity(clientId, lastSeenAt, activeSince)
    }

    fun writeDeviceInfo(deviceInfo: DeviceInfo) {
        this.writeByteArray(deviceInfo.macAddress)

        this.writeUtf(deviceInfo.name)
        this.writeUtf(deviceInfo.os)
        this.writeEnum(deviceInfo.deviceType)
    }

    fun readDeviceInfo(): DeviceInfo {
        val macAddress = this.readByteArray()

        val name = this.readUtf(255)
        val os = this.readUtf(255)
        val deviceType = this.readEnum<DeviceType>()

        return DeviceInfo(macAddress, name, os, deviceType)
    }

    fun writeEncryptedContent(encryptedContent: EncryptedContent) {
        this.writeByteArray(encryptedContent.encryptedContent)
        this.writeByteArray(encryptedContent.contentIV)
    }

    fun readEncryptedContent(): EncryptedContent {
        val encryptedContent = this.readByteArray()
        val contentNonce = this.readByteArray()

        return EncryptedContent(encryptedContent, contentNonce)
    }

    fun writeEncryptedMessage(encryptedMessage: EncryptedMessage) {
        this.writeBoolean(encryptedMessage.messageId != null)

        if (encryptedMessage.messageId != null)
            this.writeUuid(encryptedMessage.messageId!!)

        this.writeInt(encryptedMessage.index)

        this.writeUuid(encryptedMessage.conversationId)
        this.writeUuid(encryptedMessage.senderId)

        this.writeEnum(encryptedMessage.type)
        this.writeBoolean(encryptedMessage.isSilent)

        this.writeBoolean(encryptedMessage.encryptedContent != null)

        if (encryptedMessage.encryptedContent != null)
            this.writeEncryptedContent(encryptedMessage.encryptedContent!!)

        this.writeInt(encryptedMessage.namedEncryptedKeys.size)

        for ((id, encryptedKey) in encryptedMessage.namedEncryptedKeys) {
            this.writeUuid(id)
            this.writeByteArray(encryptedKey)
        }

        this.writeInt(encryptedMessage.attachments.size)

        for (attachment in encryptedMessage.attachments)
            this.writeFileMetadata(attachment)

        this.writeBoolean(encryptedMessage.sentAt != null)

        if (encryptedMessage.sentAt != null)
            this.writeInstant(encryptedMessage.sentAt!!)

        this.writeBoolean(encryptedMessage.deliveredAt != null)

        if (encryptedMessage.deliveredAt != null)
            this.writeInstant(encryptedMessage.deliveredAt!!)

        this.writeInt(encryptedMessage.namedReadAt.size)

        for ((id, readAt) in encryptedMessage.namedReadAt) {
            this.writeUuid(id)
            this.writeInstant(readAt)
        }

        this.writeBoolean(encryptedMessage.updatedAt != null)

        if (encryptedMessage.updatedAt != null)
            this.writeInstant(encryptedMessage.updatedAt!!)

        this.writeBoolean(encryptedMessage.deletedAt != null)

        if (encryptedMessage.deletedAt != null)
            this.writeInstant(encryptedMessage.deletedAt!!)
    }

    fun readEncryptedMessage(): EncryptedMessage {
        var messageId: Uuid? = null

        if (this.readBoolean())
            messageId = this.readUuid()

        val index = this.readInt()

        val conversationId = this.readUuid()
        val senderId = this.readUuid()

        val type = this.readEnum<MessageType>()
        val isSilent = this.readBoolean()

        var encryptedContent: EncryptedContent? = null

        if (this.readBoolean())
            encryptedContent = this.readEncryptedContent()

        val namedEncryptedKeys = mutableMapOf<Uuid, ByteArray>()

        repeat(this.readInt()) {
            namedEncryptedKeys[this.readUuid()] = this.readByteArray()
        }

        val attachments = mutableListOf<FileMetadata>()

        repeat(this.readInt()) {
            attachments.add(this.readFileMetadata())
        }

        var sentAt: Instant? = null

        if (this.readBoolean())
            sentAt = this.readInstant()

        var deliveredAt: Instant? = null

        if (this.readBoolean())
            deliveredAt = this.readInstant()

        val namedReadAt = mutableMapOf<Uuid, Instant>()

        repeat(this.readInt()) {
            namedReadAt[this.readUuid()] = this.readInstant()
        }

        var updatedAt: Instant? = null

        if (this.readBoolean())
            updatedAt = this.readInstant()

        var deletedAt: Instant? = null

        if (this.readBoolean())
            deletedAt = this.readInstant()

        return EncryptedMessage(messageId, index, conversationId, senderId, type, isSilent, encryptedContent, namedEncryptedKeys, attachments, sentAt, deliveredAt, namedReadAt, updatedAt, deletedAt)
    }
}