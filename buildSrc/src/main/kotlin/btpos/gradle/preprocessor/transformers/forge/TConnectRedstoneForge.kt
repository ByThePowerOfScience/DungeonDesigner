package btpos.gradle.preprocessor.transformers.forge

private const val ITF_NAME = "Lbtpos/mcmods/devutil/multiplatform/api/IPlatformConnectRedstone;"

///**
// * All we need is to remove the interface to match Forge's IBlockExtensions.
// * Since the method name and signature is the same, it should be fine.
// */
//class TConnectRedstoneForge : ClassVisitor2() {
//	override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String?>?) {
//		val newItfs = if (interfaces != null && interfaces.contains(ITF_NAME)) {
//			interfaces.filterTo(ArrayList(interfaces.size - 1)) { name != ITF_NAME }.toTypedArray()
//		} else {
//			interfaces
//		}
//		super.visit(version, access, name, signature, superName, newItfs)
//	}
//}

fun TConnectRedstoneForge(node: dev.architectury.transformer.shadowed.impl.org.objectweb.asm.tree.ClassNode) {
	if (node.interfaces.any { "IPlatformConnectRedstone" in it }) {
		println("Found IPlatformConnectRedstone in ${node.name}! Removing!")
		println("Interfaces before filter: {${node.interfaces.joinToString(", ") { it }}}")
		node.interfaces = node.interfaces.filter { it != ITF_NAME }
		println("Interfaces after filter: {${node.interfaces.joinToString(", ") { it }}}")
	}
}