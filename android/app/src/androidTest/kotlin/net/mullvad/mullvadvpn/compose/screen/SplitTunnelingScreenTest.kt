package net.mullvad.mullvadvpn.compose.screen

import android.graphics.Bitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import net.mullvad.mullvadvpn.applist.AppData
import net.mullvad.mullvadvpn.applist.ApplicationsIconManager
import net.mullvad.mullvadvpn.compose.setContentWithTheme
import net.mullvad.mullvadvpn.compose.state.SplitTunnelingUiState
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class SplitTunnelingScreenTest {
    @JvmField @RegisterExtension val composeExtension = createComposeExtension()

    private val mockBitmap: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    private val testModule = module {
        single {
            mockk<ApplicationsIconManager>().apply {
                every { getAppIcon(any()) } returns mockBitmap
            }
        }
    }

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        loadKoinModules(testModule)
    }

    @AfterEach
    fun tearDown() {
        unloadKoinModules(testModule)
        unmockkAll()
    }

    @Test
    fun testLoadingState() =
        composeExtension.use {
            // Arrange
            setContentWithTheme { SplitTunnelingScreen(uiState = SplitTunnelingUiState.Loading) }

            // Assert
            onNodeWithText(TITLE).assertExists()
            onNodeWithText(DESCRIPTION).assertExists()
            onNodeWithText(EXCLUDED_APPLICATIONS).assertDoesNotExist()
            onNodeWithText(SHOW_SYSTEM_APPS).assertDoesNotExist()
            onNodeWithText(ALL_APPLICATIONS).assertDoesNotExist()
        }

    @Test
    fun testListDisplayed() =
        composeExtension.use {
            // Arrange
            val excludedApp =
                AppData(
                    packageName = EXCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = EXCLUDED_APP_NAME
                )
            val includedApp =
                AppData(
                    packageName = INCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = INCLUDED_APP_NAME
                )
            setContentWithTheme {
                SplitTunnelingScreen(
                    uiState =
                        SplitTunnelingUiState.ShowAppList(
                            excludedApps = listOf(excludedApp),
                            includedApps = listOf(includedApp),
                            showSystemApps = false
                        )
                )
            }

            // Assert
            onNodeWithText(TITLE).assertExists()
            onNodeWithText(DESCRIPTION).assertExists()
            onNodeWithText(EXCLUDED_APPLICATIONS).assertExists()
            onNodeWithText(EXCLUDED_APP_NAME).assertExists()
            onNodeWithText(SHOW_SYSTEM_APPS).assertExists()
            onNodeWithText(ALL_APPLICATIONS).assertExists()
            onNodeWithText(INCLUDED_APP_NAME).assertExists()
        }

    @Test
    fun testNoExcludedApps() =
        composeExtension.use {
            // Arrange
            val includedApp =
                AppData(
                    packageName = INCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = INCLUDED_APP_NAME
                )
            setContentWithTheme {
                SplitTunnelingScreen(
                    uiState =
                        SplitTunnelingUiState.ShowAppList(
                            excludedApps = emptyList(),
                            includedApps = listOf(includedApp),
                            showSystemApps = false
                        )
                )
            }

            // Assert
            onNodeWithText(TITLE).assertExists()
            onNodeWithText(DESCRIPTION).assertExists()
            onNodeWithText(EXCLUDED_APPLICATIONS).assertDoesNotExist()
            onNodeWithText(EXCLUDED_APP_NAME).assertDoesNotExist()
            onNodeWithText(SHOW_SYSTEM_APPS).assertExists()
            onNodeWithText(ALL_APPLICATIONS).assertExists()
            onNodeWithText(INCLUDED_APP_NAME).assertExists()
        }

    @Test
    fun testClickIncludedItem() =
        composeExtension.use {
            // Arrange
            val excludedApp =
                AppData(
                    packageName = EXCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = EXCLUDED_APP_NAME
                )
            val includedApp =
                AppData(
                    packageName = INCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = INCLUDED_APP_NAME
                )
            val mockedClickHandler: (String) -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                SplitTunnelingScreen(
                    uiState =
                        SplitTunnelingUiState.ShowAppList(
                            excludedApps = listOf(excludedApp),
                            includedApps = listOf(includedApp),
                            showSystemApps = false
                        ),
                    onExcludeAppClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithText(INCLUDED_APP_NAME).performClick()

            // Assert
            verify { mockedClickHandler.invoke(INCLUDED_APP_PACKAGE_NAME) }
        }

    @Test
    fun testClickExcludedItem() =
        composeExtension.use {
            // Arrange
            val excludedApp =
                AppData(
                    packageName = EXCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = EXCLUDED_APP_NAME
                )
            val includedApp =
                AppData(
                    packageName = INCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = INCLUDED_APP_NAME
                )
            val mockedClickHandler: (String) -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                SplitTunnelingScreen(
                    uiState =
                        SplitTunnelingUiState.ShowAppList(
                            excludedApps = listOf(excludedApp),
                            includedApps = listOf(includedApp),
                            showSystemApps = false
                        ),
                    onIncludeAppClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithText(EXCLUDED_APP_NAME).performClick()

            // Assert
            verify { mockedClickHandler.invoke(EXCLUDED_APP_PACKAGE_NAME) }
        }

    @Test
    fun testClickShowSystemApps() =
        composeExtension.use {
            // Arrange
            val excludedApp =
                AppData(
                    packageName = EXCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = EXCLUDED_APP_NAME
                )
            val includedApp =
                AppData(
                    packageName = INCLUDED_APP_PACKAGE_NAME,
                    iconRes = 0,
                    name = INCLUDED_APP_NAME
                )
            val mockedClickHandler: (Boolean) -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                SplitTunnelingScreen(
                    uiState =
                        SplitTunnelingUiState.ShowAppList(
                            excludedApps = listOf(excludedApp),
                            includedApps = listOf(includedApp),
                            showSystemApps = false
                        ),
                    onShowSystemAppsClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithText(SHOW_SYSTEM_APPS).performClick()

            // Assert
            verify { mockedClickHandler.invoke(true) }
        }

    companion object {
        private const val EXCLUDED_APP_PACKAGE_NAME = "excluded-pkg"
        private const val EXCLUDED_APP_NAME = "Excluded Name"
        private const val INCLUDED_APP_PACKAGE_NAME = "included-pkg"
        private const val INCLUDED_APP_NAME = "Included Name"
        private const val TITLE = "Split tunneling"
        private const val DESCRIPTION = "Choose the apps you want to exclude from the VPN tunnel."
        private const val EXCLUDED_APPLICATIONS = "Excluded applications"
        private const val SHOW_SYSTEM_APPS = "Show system apps"
        private const val ALL_APPLICATIONS = "All applications"
    }
}
