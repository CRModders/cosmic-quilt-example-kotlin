package com.example.example_mod

import org.coolcosmos.cosmicquilt.api.entrypoint.ModInitializer
import org.quiltmc.loader.api.ModContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ExampleMod : ModInitializer {
    companion object {
        const val MOD_ID = "example_mod"

        @kotlin.jvm.JvmField
        val LOGGER: Logger = LoggerFactory.getLogger("Example Mod")
    }

    override fun onInitialize(mod: ModContainer) {
        LOGGER.info("Hello from $MOD_ID!")
    }
}

