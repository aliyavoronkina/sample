package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        // Press home
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(result, textToSet)
    }

    @Test
    fun testEmptyStringInput() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val inputField = device.findObject(By.res(packageName, "userInput"))
        val changeButton = device.findObject(By.res(packageName, "buttonChange"))
        val textView = device.findObject(By.res(packageName, "textToBeChanged"))

        val originalText = textView.text
        inputField.text = ""
        changeButton.click()

        assertEquals("При вводе пустой строки текст не должен меняться",
            originalText, textView.text)
    }

    @Test
    fun testWhitespaceStringInput() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val inputField = device.findObject(By.res(packageName, "userInput"))
        val changeButton = device.findObject(By.res(packageName, "buttonChange"))
        val textView = device.findObject(By.res(packageName, "textToBeChanged"))

        val originalText = textView.text
        inputField.text = "   "
        changeButton.click()

        assertEquals("При вводе строки из пробелов текст не должен меняться",
            originalText, textView.text)
    }

    @Test
    fun testOpenTextInNewActivity() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val testText = "Привет, UI Automator!"

        val inputField = device.findObject(By.res(packageName, "userInput"))
        val openActivityButton = device.findObject(By.res(packageName, "buttonActivity"))

        inputField.text = testText
        openActivityButton.click()

        val secondTextView = device.wait(
            Until.findObject(By.res(packageName, "text")),
            TIMEOUT
        )

        assertNotNull("TextView во второй Activity не найден", secondTextView)
        assertEquals("Текст во второй Activity не совпадает с введенным",
            testText, secondTextView.text)
    }
}