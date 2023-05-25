import javafx.scene.Scene;

import java.util.Dictionary;

enum UserInput {
    UP,
    DOWN,
    LEFT,
    RIGHT
}
public class InputManager {
    public InputManager(Scene scene, Dictionary<UserInput, Boolean> inputsPressed, DevHUD devHUD) {
        inputsPressed.put(UserInput.UP, false);
        inputsPressed.put(UserInput.DOWN, false);
        inputsPressed.put(UserInput.LEFT, false);
        inputsPressed.put(UserInput.RIGHT, false);

        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case W:
                    inputsPressed.put(UserInput.UP, true);
                    break;
                case DOWN:
                case S:
                    inputsPressed.put(UserInput.DOWN, true);
                    break;
                case LEFT:
                case A:
                    inputsPressed.put(UserInput.LEFT, true);
                    break;
                case RIGHT:
                case D:
                    inputsPressed.put(UserInput.RIGHT, true);
                    break;
                case I:
                    devHUD.toggleDevHud();
                    break;
                default:
                    break;
            }

        });

        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case W:
                    inputsPressed.put(UserInput.UP, false);
                    break;
                case DOWN:
                case S:
                    inputsPressed.put(UserInput.DOWN, false);
                    break;
                case LEFT:
                case A:
                    inputsPressed.put(UserInput.LEFT, false);
                    break;
                case RIGHT:
                case D:
                    inputsPressed.put(UserInput.RIGHT, false);
                    break;
                default:
                    break;
            }

        });
    }
}