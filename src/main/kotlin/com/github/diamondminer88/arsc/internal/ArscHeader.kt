package com.github.diamondminer88.arsc.internal

import java.nio.ByteBuffer

/**
 * A header prefixed at the start of the file and before each package and chunk
 * @param type The body type
 * @param headerSize u16 header size
 * @param size u64 body size
 */
internal data class ArscHeader(
	val type: ArscHeaderType,
	val headerSize: UShort,
	val size: UInt,
) {
	companion object {
		const val BYTES_SIZE = ArscHeaderType.SIZE_BYTES + 6

		/**
		 * Parse a chunk header at the current position in the buffer
		 */
		@JvmStatic
		fun parse(bytes: ByteBuffer): ArscHeader {
			val type = ArscHeaderType.parse(bytes)
			val headerSize = bytes.short.toUShort()
			val bodySize = bytes.int.toUInt()

			return ArscHeader(
				type = type,
				headerSize = headerSize,
				size = bodySize,
			)
		}
	}
}
