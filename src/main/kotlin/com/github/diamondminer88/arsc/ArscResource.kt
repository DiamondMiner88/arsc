package com.github.diamondminer88.arsc

import java.nio.ByteBuffer

public data class ArscResource(
	internal val specId: UInt,
	val flags: UShort,
	val name: String,
	val value: ArscValue,
) {
	internal companion object {
		private const val FLAG_COMPLEX = 0x0001u

		@JvmStatic
		fun parse(bytes: ByteBuffer, resourceCount: Int): List<ArscResource> {
			val entries = (0 until resourceCount)
				.map { bytes.int.toUInt() }

			val resources = mutableListOf<ArscResource>()

			entries.forEachIndexed { specIndex, entry ->
				if (entry == UInt.MAX_VALUE)
					return@forEachIndexed

				val size = bytes.short.toUShort()
				val flags = bytes.short.toUShort()
				val nameIndex = bytes.int.toUInt()
				val value = if (flags.toUInt() and FLAG_COMPLEX != 0U) {
					val parent = bytes.int.toUInt()
					val count = bytes.int.toUInt()
					val values = (0 until count.toInt()).associate {
						val index = bytes.int.toUInt()
						val value = ArscValue.Plain.parse(bytes)
						index to value
					}

					ArscValue.Bag(
						parent = parent,
						values = values,
					)
				} else {
					ArscValue.Plain.parse(bytes)
				}

				resources += ArscResource(
					specId = specIndex.toUInt(),
					flags = flags,
					name = keyStringPool.strings[nameIndex],
					value = value,
				)
			}

			return resources
		}
	}
}