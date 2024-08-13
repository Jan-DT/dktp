package nl.jandt.dktp.scene;

import nl.jandt.dktp.poison.Poison;
import org.jetbrains.annotations.Nullable;

public record SceneContext(@Nullable Poison poison, @Nullable Poison.PoisonEffect previousEffect) {
}
