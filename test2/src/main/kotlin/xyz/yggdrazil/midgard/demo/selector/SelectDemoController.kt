package xyz.yggdrazil.midgard.demo.selector

import javafx.application.Platform
import xyz.yggdrazil.midgard.demo.selector.SelectDemoView
import xyz.yggdrazil.midgard.demo.fortune.FortuneView
import tornadofx.Controller
import tornadofx.FX

class SelectDemoController : Controller() {
    val selectDemoView: SelectDemoView by inject()
    val fortuneView: FortuneView by inject()

    fun init() {
        with (config) {
            showLoginScreen("Please log in")
        }
    }

    fun showLoginScreen(message: String, shake: Boolean = false) {
        if (FX.primaryStage.scene.root != selectDemoView.root) {
            FX.primaryStage.scene.root = selectDemoView.root
            FX.primaryStage.sizeToScene()
            FX.primaryStage.centerOnScreen()
        }

        selectDemoView.title = message

    }

    fun showWorkbench() {
        if (FX.primaryStage.scene.root != fortuneView.root) {
            FX.primaryStage.scene.root = fortuneView.root
            FX.primaryStage.sizeToScene()
            FX.primaryStage.centerOnScreen()
        }
    }

    fun tryLogin(username: String, password: String, remember: Boolean) {
        runAsync {
            username == "admin" && password == "secret"
        } ui { successfulLogin ->

            if (successfulLogin) {
                selectDemoView.clear()

                if (remember) {
                    with (config) {
                        set(USERNAME to username)
                        set(PASSWORD to password)
                        save()
                    }
                }

                showWorkbench()
            } else {
                showLoginScreen("Login failed. Please try again.", true)
            }
        }
    }

    fun logout() {
        with (config) {
            remove(USERNAME)
            remove(PASSWORD)
            save()
        }

        showLoginScreen("Log in as another user")
    }

    companion object {
        val USERNAME = "username"
        val PASSWORD = "password"
    }




    fun showFortuneView() {
        if (FX.primaryStage.scene.root != fortuneView.root) {
            FX.primaryStage.scene.root = fortuneView.root
            FX.primaryStage.sizeToScene()
            FX.primaryStage.centerOnScreen()
        }
    }

}