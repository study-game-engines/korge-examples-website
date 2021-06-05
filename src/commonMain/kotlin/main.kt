import box2d.*
import com.soywiz.kds.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korgw.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import com.soywiz.korio.util.*
import dragonbones.*
import extension.*
import filters.*
import gestures.*
import scene1.*
import scene2.*
import spine.*

suspend fun main() {
	ext.preinit()
	Korge(title = "KorGE Web Samples", width = 800, height = 600, bgcolor = Colors["#2b2b2b"], quality = GameWindow.Quality.PERFORMANCE) {
		val GROUP_BASICS = "Basics"
		val GROUP_ADVANCED = "Advanced"
		val GROUP_PHYSICS = "Physics"
		val GROUP_SKELETON = "Skeleton"
		val GROUP_INPUT = "Input"
		register(
			SceneInfo(title = "Rotating Image", group = GROUP_BASICS, srcPath = "scene1/Scene1.kt") { Scene1() },
			SceneInfo(title = "Tinting", group = GROUP_BASICS, srcPath = "scene2/Scene2.kt") { Scene2() },
			SceneInfo(title = "Filters", group = GROUP_ADVANCED, srcPath = "filters/FiltersScene.kt") { FiltersScene() },
			SceneInfo(title = "Simple Box2d", group = GROUP_PHYSICS, srcPath = "box2d/SimpleBox2dScene.kt") { SimpleBox2dScene() },
			//SceneInfo(title = "Dragonbones", group = GROUP_SKELETON, srcPath = "dragonbones/DragonbonesScene.kt") { DragonbonesScene() }, // JS-IR has issues with this demo
			SceneInfo(title = "Spine", group = GROUP_SKELETON, srcPath = "spine/SpineScene.kt") { SpineScene() },
			SceneInfo(title = "Gestures", group = GROUP_INPUT, srcPath = "gestures/GesturesScene.kt") { GesturesScene() },
		)

		// Elements
		run {
			this.mainSceneContainer = sceneContainer()
			//ext.hasExternalLayout
			if (!OS.isJs) {
				uiComboBox(items = registeredScenes.values.toList()).onSelectionUpdate {
					launchImmediately {
						changeToScene(stage, it.selectedItem?.className)
					}
				}
			} else {
				// On JS we have
			}
		}

		ext.init(this)
		ext.registerEvent("changeScene") { detail ->
			launchImmediately {
				changeToScene(stage, detail.toString())
			}
		}

		ext.registerEvent("hashchange") { detail ->
			launchImmediately {
				changeToSceneDefault(stage)
			}
		}

		changeToSceneDefault(stage)
	}
}

var Stage.mainSceneContainer: SceneContainer? by extraProperty { null }

suspend fun changeToSceneDefault(stage: Stage) {
	changeToScene(stage, ext.getSelectedSceneName())
}

suspend fun changeToScene(stage: Stage, sceneName: String?) {
	val registeredScenes = stage.registeredScenes
	val realSceneName = sceneName ?: registeredScenes.keys.first()
	val sceneInfo = registeredScenes[realSceneName] ?: registeredScenes.values.first()
	stage.mainSceneContainer!!.changeTo(sceneInfo.clazz)
	ext.dispatchCustomEvent("changedScene", sceneInfo.className)
}
