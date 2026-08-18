package me.shedaniel.linkie.core.tests

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.shedaniel.linkie.*
import me.shedaniel.linkie.LinkieConfig
import me.shedaniel.linkie.Namespaces
import me.shedaniel.linkie.namespaces.MojangNamespace
import me.shedaniel.linkie.namespaces.MojangRawNamespace
import me.shedaniel.linkie.namespaces.YarnNamespace
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Linkie26Test {
    @Test
    fun mojangNamespaceExcludes26() {
        runBlocking {
            Namespaces.init(LinkieConfig.DEFAULT.copy(namespaces = listOf(MojangRawNamespace, YarnNamespace, MojangNamespace)))
            delay(2000)
            while (MojangNamespace.reloading) delay(100)
            val versions = MojangNamespace.getAllVersions().toList()
            assertFalse("26.2" in versions, "mojang namespace must not list 26.2, got: $versions")
            assertTrue(MojangNamespace.getProvider("26.2").isEmpty(), "mojang getProvider(26.2) must be empty")
            assertTrue(MojangNamespace.getProvider("1.21.11").get().allClasses.isNotEmpty(), "mojang 1.21.11 must still load")
        }
    }

    @Test
    fun mojmapRaw26Identity() {
        runBlocking {
            Namespaces.init(LinkieConfig.DEFAULT.copy(namespaces = listOf(MojangRawNamespace)))
            delay(2000)
            while (MojangRawNamespace.reloading) delay(100)
            val provider = MojangRawNamespace.getDefaultProvider()
            val container = provider.get()
            assertTrue(container.allClasses.isNotEmpty(), "26.2 identity mappings produced no classes")
            val source = provider.getSources("net/minecraft/client/Camera")
            assertTrue(source.exists(), "Decompiler did not produce a file: ${source.absolutePath}")
        }
    }

    @Test
    fun yarnSourceFixed() {
        runBlocking {
            Namespaces.init(LinkieConfig.DEFAULT.copy(namespaces = listOf(YarnNamespace)))
            delay(2000)
            while (YarnNamespace.reloading) delay(100)
            val provider = YarnNamespace.getDefaultProvider()
            val className = provider.get().allClasses.firstOrNull { it.optimumName == "net/minecraft/entity/data/TrackedData" }?.optimumName
                ?: provider.get().allClasses.random().optimumName
            val source = provider.getSources(className)
            assertTrue(source.exists(), "Decompiler did not produce a file: ${source.absolutePath}")
        }
    }

    @Test
    fun mojmapSourceFixed() {
        runBlocking {
            Namespaces.init(LinkieConfig.DEFAULT.copy(namespaces = listOf(MojangNamespace)))
            delay(2000)
            while (MojangNamespace.reloading) delay(100)
            val provider = MojangNamespace.getDefaultProvider()
            val className = provider.get().allClasses.firstOrNull { it.optimumName == "net/minecraft/world/level/levelgen/feature/foliageplacers/RandomSpreadFoliagePlacer" }?.optimumName
                ?: provider.get().allClasses.random().optimumName
            val source = provider.getSources(className)
            assertTrue(source.exists(), "Decompiler did not produce a file: ${source.absolutePath}")
        }
    }
}
