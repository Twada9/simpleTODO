import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.simpletodo.ViewModel.MainViewModel

@Composable
fun AddButton(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = {
            viewModel.resetTodo()
            viewModel.openModalView()
        },
        modifier
            .wrapContentSize(Alignment.BottomEnd)
            .padding(),
        shape = CircleShape,
    ) {
        Icon(Icons.Filled.Add, "Large floating action button")
    }
}
