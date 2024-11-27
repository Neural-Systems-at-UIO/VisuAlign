package visualign;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressPop {
	private final Stage stage = new Stage();
	private final ProgressBar progressbar = new ProgressBar();

	public ProgressPop(String msg) {
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setOnCloseRequest(event -> event.consume());
		stage.setTitle(msg);
//	    stage.setWidth(new Text(msg+"                 ").getLayoutBounds().getWidth());

		progressbar.setPrefWidth(new Text(msg + "           ").getLayoutBounds().getWidth());

		stage.setScene(new Scene(progressbar));
		stage.sizeToScene();
		stage.show();
	}

	public void bindTask(Task<Void> task) {
		progressbar.progressProperty().bind(task.progressProperty());
	}

	public void close() {
		stage.close();
	}
}
