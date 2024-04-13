package com.example.example_mod

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer
import org.quiltmc.loader.api.ModContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MOD_ID = "example_mod"
val logger: Logger = LoggerFactory.getLogger("Example Mod")

class ExampleMod : ModInitializer {
    override fun onInitialize(mod: ModContainer) {
        logger.info("Hello from $MOD_ID!")
    }
}

