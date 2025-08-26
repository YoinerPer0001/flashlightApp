import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.linternapro.core.navigation.MainSC
import com.example.linternapro.presenter.screens.MainScreen
import com.example.linternapro.presenter.viewmodels.PermissionsCameraVM
import com.example.linternapro.presenter.viewmodels.TorchManager


@Composable
fun NavigationManager(callback:()-> Unit){

    val navController = rememberNavController()

    NavHost(navController, startDestination = MainSC){

        composable<MainSC> {
            val viewmodel = hiltViewModel<PermissionsCameraVM>()
            val torchVM = hiltViewModel<TorchManager>()
            MainScreen (viewmodel, torchVM){
                callback()
            }
        }
    }

}