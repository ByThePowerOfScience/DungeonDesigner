package btpos.gradle.preprocessor

import btpos.gradle.preprocessor.transformers.forge.TConnectRedstoneForge
import btpos.gradle.preprocessor.transformers.testing.JUnitExtendWithFabric
import btpos.gradle.preprocessor.transformers.testing.JUnitExtendWithNeo
import dev.architectury.transformer.Transformer
import dev.architectury.transformer.transformers.base.ClassEditTransformer
import org.gradle.internal.cc.base.logger

// Lets me easily swap between using the original for the docs and using the shaded one for the compat
typealias ClassNode = dev.architectury.transformer.shadowed.impl.org.objectweb.asm.tree.ClassNode
typealias Type = dev.architectury.transformer.shadowed.impl.org.objectweb.asm.Type

// prefix = dev.architectury.transformer.shadowed.impl.

class MultiplatformPreTransformer_Forge : ClassEditTransformer {
	override fun doEdit(name: String, node: ClassNode): ClassNode {
		TConnectRedstoneForge(node)
//		JUnitExtendWithNeo(node)
		return node
	}
}

class MultiplatformPreTransformer_Fabric : ClassEditTransformer {
	override fun doEdit(name: String, node: ClassNode): ClassNode {
//		JUnitExtendWithFabric(node)
		return node
	}
}

fun getForgeTransformers(): List<ClassEditTransformer> {
	return listOf(MultiplatformPreTransformer_Forge())
}

fun getFabricTransformers(): List<ClassEditTransformer> {
	return listOf(MultiplatformPreTransformer_Fabric())
}

/*
open class MultiplatformPreTransformer @Inject constructor(platform: String) : Action<Task> {
	val COMMON_TRANSFORMERS = listOf<ClassVisitor2>()

	fun getPlatformTransformers(platform: String): List<ClassVisitor2> {
		return when (platform) {
			"neoforge" -> listOf(
					TConnectRedstoneForge(),
					JUnitExtendWithNeo()
			)
			"fabric" -> listOf(
					JUnitExtendWithFabric()
			)
			else -> listOf()
		}
	}

	*/
/**
	 * Assemble a big nested stack of transformers that hopefully gets optimized by the JVM into a nice functional transformation
	 *//*

	val combinedVisitor: ClassVisitor2? = listOf(COMMON_TRANSFORMERS, getPlatformTransformers(platform))
			.flatMap { it }
			.takeIf { it.isNotEmpty() }
			?.reduce { first, second ->
				first.setDelegate(second)
				second
			}
			?.let {
				val terminal = MultiThreadedTerminalVisitor()
				it.setDelegate(terminal)
				terminal
			}


	override fun execute(t: Task) {
		when (t) {
			is AbstractCompile -> postCompile(t.destinationDirectory)
			is KotlinJvmCompile -> postCompile(t.destinationDirectory)
		}
	}

	fun postCompile(destination: DirectoryProperty) {
		if (combinedVisitor == null)
			return
		StreamSupport.stream(destination.asFileTree.spliterator(), true)
			.filter { it.isFile && it.endsWith(".class") }
			.forEach { file ->
				val bytesIn = file.readBytes()
				val reader = ClassReader(bytesIn)
				val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS)

				combinedVisitor.setDelegate(writer)

				reader.accept(combinedVisitor, 0)

				val bytesOut = writer.toByteArray()

				if (!bytesOut.contentEquals(bytesIn))
					file.writeBytes(bytesOut)
			}
	}
}

class MultiThreadedTerminalVisitor : ClassVisitor2() {
	val local: ThreadLocal<ClassVisitor> = ThreadLocal()

	override fun setDelegate(visitor: ClassVisitor?) {
		local.set(visitor)
	}

	override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean): AnnotationVisitor? {
		return local.get().visitTypeAnnotation(typeRef, typePath, descriptor, visible)
	}

	override fun visitSource(source: String?, debug: String?) {
		local.get().visitSource(source, debug)
	}

	override fun visitRecordComponent(name: String?, descriptor: String?, signature: String?): RecordComponentVisitor? {
		return local.get().visitRecordComponent(name, descriptor, signature)
	}

	override fun visitPermittedSubclass(permittedSubclass: String?) {
		local.get().visitPermittedSubclass(permittedSubclass)
	}

	override fun visitOuterClass(owner: String?, name: String?, descriptor: String?) {
		local.get().visitOuterClass(owner, name, descriptor)
	}

	override fun visitNestMember(nestMember: String?) {
		local.get().visitNestMember(nestMember)
	}

	override fun visitNestHost(nestHost: String?) {
		local.get().visitNestHost(nestHost)
	}

	override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor? {
		return local.get().visitModule(name, access, version)
	}

	override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String?>?): MethodVisitor? {
		return local.get().visitMethod(access, name, descriptor, signature, exceptions)
	}

	override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
		local.get().visitInnerClass(name, outerName, innerName, access)
	}

	override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor? {
		return local.get().visitField(access, name, descriptor, signature, value)
	}

	override fun visitEnd() {
		local.get().visitEnd()
	}

	override fun visitAttribute(attribute: Attribute?) {
		local.get().visitAttribute(attribute)
	}

	override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
		return local.get().visitAnnotation(descriptor, visible)
	}

	override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String?>?) {
		local.get().visit(version, access, name, signature, superName, interfaces)
	}
}*/
