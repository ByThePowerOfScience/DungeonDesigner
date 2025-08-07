package btpos.unittest.fabric

import com.google.common.base.Stopwatch
import com.mojang.authlib.GameProfile
import com.mojang.authlib.yggdrasil.ServicesKeySet
import com.mojang.logging.LogUtils
import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.SharedConstants
import net.minecraft.SystemReport
import net.minecraft.Util
import net.minecraft.commands.Commands
import net.minecraft.core.LayeredRegistryAccess
import net.minecraft.core.MappedRegistry
import net.minecraft.core.registries.Registries
import net.minecraft.server.Bootstrap
import net.minecraft.server.MinecraftServer
import net.minecraft.server.RegistryLayer
import net.minecraft.server.ReloadableServerResources
import net.minecraft.server.Services
import net.minecraft.server.WorldLoader
import net.minecraft.server.WorldStem
import net.minecraft.server.level.progress.ChunkProgressListenerFactory
import net.minecraft.server.level.progress.LoggerChunkProgressListener
import net.minecraft.server.packs.repository.PackRepository
import net.minecraft.server.packs.repository.ServerPacksSource
import net.minecraft.server.packs.resources.CloseableResourceManager
import net.minecraft.server.players.PlayerList
import net.minecraft.util.datafix.DataFixers
import net.minecraft.util.debugchart.LocalSampleLogger
import net.minecraft.util.debugchart.SampleLogger
import net.minecraft.world.Difficulty
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.DataPackConfig
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.LevelSettings
import net.minecraft.world.level.WorldDataConfiguration
import net.minecraft.world.level.levelgen.WorldOptions
import net.minecraft.world.level.levelgen.presets.WorldPresets
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.PrimaryLevelData
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver
import org.slf4j.Logger
import java.io.IOException
import java.net.Proxy
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import kotlin.system.exitProcess


/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
@Suppress("ALL")
class EphemeralTestServerProvider : ParameterResolver, Extension {
	companion object {
		val SERVER = AtomicReference<MinecraftServer?>()
		val IN_CONSTRUCTION = AtomicBoolean()
	}
	
	@Throws(ParameterResolutionException::class)
	override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
		return parameterContext.getParameter().getType() == MinecraftServer::class.java
	}
	
	@Throws(ParameterResolutionException::class)
	override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
		return grabServer()
	}
	
	fun grabServer(): MinecraftServer {
		SERVER.get().let {
			if (it != null)
				return it
		}
		
		if (IN_CONSTRUCTION.compareAndSet(false, true)) {
			try {
				val tempDir = Files.createTempDirectory("test-mc-server-")
				val storage = LevelStorageSource.createDefault(tempDir.resolve("world"))
				val storageAccess = storage.validateAndCreateAccess("main")
				val packrepository = ServerPacksSource.createPackRepository(storageAccess)
				val server: MinecraftServer = MinecraftServer.spin { thread: Thread -> JUnitServer.Companion.create(thread, tempDir, storageAccess, packrepository) }
				
				Runtime.getRuntime().addShutdownHook(Thread {
					server.stopServer()
					LogManager.shutdown()
				})
			} catch (ex: Exception) {
				LogUtils.getLogger().error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", ex)
				throw RuntimeException(ex)
			}
		}
		
		while (SERVER.get() == null) {
			try {
				Thread.sleep(1000)
				Thread.onSpinWait()
			} catch (e: InterruptedException) {
				throw RuntimeException(e)
			}
		}
		
		return SERVER.get()!!
	}
	
	class JUnitServer(
		thread: Thread,
		access: LevelStorageSource.LevelStorageAccess,
		pack: PackRepository,
		stem: WorldStem,
		private val tempDir: Path) : MinecraftServer(thread, access, pack, stem, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, ChunkProgressListenerFactory { radius: Int -> LoggerChunkProgressListener.createFromGameruleRadius(radius) }) {
		public override fun initServer(): Boolean {
			this.setPlayerList(object : PlayerList(this, this.registries(), this.playerDataStorage, 1) {})
			ServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(this)
			
//			net.neoforged.neoforge.server.ServerLifecycleHooks.handleServerAboutToStart(this)
			LOGGER.info("Started ephemeral JUnit server")
//			net.neoforged.neoforge.server.ServerLifecycleHooks.handleServerStarting(this)
			ServerLifecycleEvents.SERVER_STARTED.invoker().onServerStarted(this)
			return true
		}
		
		override fun tickServer(sup: BooleanSupplier) {
			super.tickServer(sup)
			// Consider the server started the first time it ticks
			SERVER.compareAndSet(null, this)
		}
		
		override fun saveEverything(p_195515_: Boolean, p_195516_: Boolean, p_195517_: Boolean): Boolean {
			// The server is ephemeral
			return false
		}
		
		override fun stopServer() {
			LOGGER.info("Stopping server")
			this.getConnection().stop()
			getPlayerList().removeAll()
			
			try {
				storageSource.deleteLevel()
				this.storageSource.close()
				
				Files.delete(tempDir)
			} catch (ioexception: IOException) {
				LOGGER.error("Failed to unlock level {}", this.storageSource.getLevelId(), ioexception)
			}
		}
		
		public override fun waitUntilNextTick() {
			this.runAllTasks()
		}
		
		override fun fillServerSystemReport(report: SystemReport): SystemReport {
			report.setDetail("Type", "Test ephemeral server")
			return report
		}
		
		override fun isHardcore(): Boolean {
			return false
		}
		
		override fun getOperatorUserPermissionLevel(): Int {
			return 0
		}
		
		override fun getFunctionCompilationLevel(): Int {
			return 4
		}
		
		override fun shouldRconBroadcast(): Boolean {
			return false
		}
		
		override fun isDedicatedServer(): Boolean {
			return false
		}
		
		override fun getRateLimitPacketsPerSecond(): Int {
			return 0
		}
		
		override fun isEpollEnabled(): Boolean {
			return false
		}
		
		override fun isCommandBlockEnabled(): Boolean {
			return true
		}
		
		override fun isPublished(): Boolean {
			return false
		}
		
		override fun shouldInformAdmins(): Boolean {
			return false
		}
		
		override fun isSingleplayerOwner(profile: GameProfile): Boolean {
			return false
		}
		
		private val sampleLogger = LocalSampleLogger(1)
		
		override fun getTickTimeLogger(): SampleLogger {
			return sampleLogger
		}
		
		override fun isTickTimeLoggingEnabled(): Boolean {
			return false
		}
		
		companion object {
			private val LOGGER: Logger = LogUtils.getLogger()
			private val NO_SERVICES = Services(null, ServicesKeySet.EMPTY, null, null)
			private val TEST_GAME_RULES: GameRules = Util.make<GameRules>(GameRules(FeatureFlags.REGISTRY.allFlags()), Consumer { rules: GameRules ->
				rules.getRule<GameRules.BooleanValue>(GameRules.RULE_DOMOBSPAWNING).set(false, null)
				rules.getRule<GameRules.BooleanValue>(GameRules.RULE_WEATHER_CYCLE).set(false, null)
			})
			private val WORLD_OPTIONS = WorldOptions(0L, false, false)
			
			fun create(
				thread: Thread, tempDir: Path, access: LevelStorageSource.LevelStorageAccess, resources: PackRepository): JUnitServer {
				resources.reload()
				val config = WorldDataConfiguration(
						DataPackConfig(ArrayList<String?>(resources.getAvailableIds()), mutableListOf<String?>()), FeatureFlags.REGISTRY.allFlags()
				)
				val levelsettings = LevelSettings(
						"Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, TEST_GAME_RULES, config
				)
				val `worldloader$packconfig` = WorldLoader.PackConfig(resources, config, false, true)
				val `worldloader$initconfig` = WorldLoader.InitConfig(`worldloader$packconfig`, Commands.CommandSelection.DEDICATED, 4)
				
				try {
					LOGGER.debug("Starting resource loading")
					val stopwatch = Stopwatch.createStarted()
					val worldstem = Util.blockUntilDone { exec: Executor ->
						WorldLoader.load(
								`worldloader$initconfig`,
								{ ctx: WorldLoader.DataLoadContext? ->
									val registry = MappedRegistry(Registries.LEVEL_STEM, Lifecycle.stable()).freeze()
									val `worlddimensions$complete` = ctx!!.datapackWorldgen()
										.lookupOrThrow(Registries.WORLD_PRESET)
										.getOrThrow(WorldPresets.FLAT)
										.value()
										.createWorldDimensions()
										.bake(registry)
									WorldLoader.DataLoadOutput(
											PrimaryLevelData(
													levelsettings, WORLD_OPTIONS, `worlddimensions$complete`.specialWorldProperty(), `worlddimensions$complete`.lifecycle()
											),
											`worlddimensions$complete`.dimensionsRegistryAccess()
									)
								},
								{ resourceManager: CloseableResourceManager?, dataPackResources: ReloadableServerResources?, registries: LayeredRegistryAccess<RegistryLayer?>?, worldData: PrimaryLevelData? -> WorldStem(resourceManager, dataPackResources, registries, worldData) },
								Util.backgroundExecutor(),
								exec
						)
					}
						.get()
					stopwatch.stop()
					LOGGER.debug("Finished resource loading after {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS))
					return JUnitServer(thread, access, resources, worldstem, tempDir)
				} catch (exception: Exception) {
					LOGGER.warn("Failed to load vanilla datapack, bit oops", exception)
					exitProcess(-1)
					throw IllegalStateException()
				}
			}
		}
	}
}