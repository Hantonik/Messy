package hantonik.messy.data

import kotlin.jvm.JvmStatic

private val codes: Map<Int, ErrorCode> = mutableMapOf()

enum class ErrorCode(val code: Int) {
    USER_ALREADY_EXISTS(0xA01),

    NO_SUCH_USER(0xB01),
    INVALID_KEY(0xB02),
    INVALID_SESSION(0xB03);

    init {
        if ((codes as MutableMap).put(this.code, this) != null)
            throw Error("Error with code 0x${this.code.toHexString(HexFormat.UpperCase)} already exists")
    }

    companion object {
        @JvmStatic
        fun getByCode(code: Int) = codes[code]
    }
}