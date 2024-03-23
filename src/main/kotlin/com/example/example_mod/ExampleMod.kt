package com.example.example_mod

import org.coolcosmos.cosmicquilt.api.entrypoint.ModInitializer
import org.quiltmc.loader.api.ModContainer

class ExampleMod : ModInitializer {
    companion object {
        const val MOD_ID = "example_mod"
    }

    override fun onInitialize(mod: ModContainer) {
        println("Hello from $MOD_ID!")
    }
}

