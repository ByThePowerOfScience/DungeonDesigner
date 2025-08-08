package btpos.gradle.preprocessor.transformers.forge

import btpos.gradle.preprocessor.ClassNode

private const val ITF_NAME = "btpos/mcmods/devutil/multiplatform/api/IPlatformConnectRedstone"

/**
 * All we need is to remove the interface to match Forge's IBlockExtensions.
 * Since the method name and signature is the same, it should be fine.
 */
fun TConnectRedstoneForge(node: ClassNode) {
	node.interfaces = node.interfaces.filter { it != ITF_NAME }
}