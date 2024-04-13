# Cosmic Quilt Example Mod (Kotlin Edition!)
> The Kotlin example mod for the [Cosmic Quilt](https://codeberg.org/CRModders/cosmic-quilt) Loader

## How to test/build
For testing in the dev env, you can use the `gradle run` task

For building, the usual `gradle build` task can be used. The output will be in the `build/libs/` folder

## Notes
- Most project properties can be changed in the `gradle.properties`
- Author, description, and other mod properties can be changed in `src/main/resources/quilt.mod.json`
- The project name is defined in `settings.gradle`
- Mixins are not supported in Kotlin, so the example `src/main/java/com/example/example_mod/mixins/MainMenuMixin.java` unfortunately must exist in Java.
